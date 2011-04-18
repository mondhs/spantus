/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.spantus.externals.recognition.services.impl;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.sound.sampled.AudioInputStream;

import org.spantus.core.IValues;
import org.spantus.core.extractor.IExtractorInputReader;
import org.spantus.core.extractor.IGeneralExtractor;
import org.spantus.core.marker.Marker;
import org.spantus.core.marker.MarkerSet;
import org.spantus.core.marker.MarkerSetHolder;
import org.spantus.core.marker.MarkerSetHolder.MarkerSetHolderEnum;
import org.spantus.core.threshold.IClassifier;
import org.spantus.core.wav.AudioManager;
import org.spantus.core.wav.AudioManagerFactory;
import org.spantus.exception.ProcessingException;
import org.spantus.externals.recognition.bean.CorpusEntry;
import org.spantus.externals.recognition.bean.RecognitionResult;
import org.spantus.externals.recognition.services.CorpusEntryExtractor;
import org.spantus.externals.recognition.services.CorpusService;
import org.spantus.externals.recognition.services.RecognitionServiceFactory;
import org.spantus.extractor.ExtractorsFactory;
import org.spantus.extractor.impl.ExtractorEnum;
import org.spantus.logger.Logger;
import org.spantus.segment.ISegmentatorService;
import org.spantus.segment.SegmentFactory;
import org.spantus.segment.SegmentFactory.SegmentatorServiceEnum;
import org.spantus.segment.online.OnlineDecisionSegmentatorParam;
import org.spantus.utils.Assert;
import org.spantus.work.services.ExtractorReaderService;
import org.spantus.work.services.ExtractorReaderServiceImpl;

/**
 * 
 * @author mondhs
 */
public class CorpusEntryExtractorFileImpl implements CorpusEntryExtractor {

	private static final Logger log = Logger
			.getLogger(CorpusEntryExtractorFileImpl.class);
	private ExtractorReaderService readerService;
	private ExtractorEnum[] extractors;
	private ISegmentatorService segmentator;
	private CorpusService corpusService;
	private AudioManager audioManager;
	private OnlineDecisionSegmentatorParam segmentionParam;
	private int windowLengthInMilSec = ExtractorsFactory.DEFAULT_WINDOW_LENGHT;
	private int overlapInPerc = ExtractorsFactory.DEFAULT_WINDOW_OVERLAP;
	private String segmentatorServiceType = SegmentatorServiceEnum.offline.name();
	private String rulePath;
	private boolean rulesTurnedOn;

	/**
	 * Find segments(markers), then put them to corpus
	 * 
	 * @param filePath
	 * @return
	 */
	public List<CorpusEntry> extractInMemory(File filePath) {
		Assert.isTrue(filePath.exists(), "file not exists" + filePath);
		List<CorpusEntry> result = new ArrayList<CorpusEntry>();
		// find markers
		IExtractorInputReader reader = createReaderWithClassifier(filePath);

		MarkerSetHolder markerSetHorlder = findMarkers(reader);
		MarkerSet segments = findSegementedLowestMarkers(markerSetHorlder);

		// process markers
		Assert.isTrue(segments != null);
		for (Marker marker : segments.getMarkers()) {
			CorpusEntry corpusEntry = create(marker, reader);
			result.add(corpusEntry);
		}

		return result;
	}
	/**
	 * 
	 * @param wavFilePath
	 * @return
	 */
	public MarkerSetHolder extract(File wavFilePath) {
		Assert.isTrue(wavFilePath.exists(), "file not exists" + wavFilePath);

		// find markers
		IExtractorInputReader reader = createReaderWithClassifier(wavFilePath);

		MarkerSetHolder markerSetHorlder = findMarkers(reader);
		return markerSetHorlder;
	}

	/**
	 * 
	 * @param extractors2
	 * @param wavFilePath
	 * @return
	 */
	public IExtractorInputReader createReaderWithClassifier(File wavFilePath) {
		log.debug(
				"[createReaderWithClassifier]\nWindow size: {0};\nOverlap: {1};\nSegmenator: {2};\nExtractors: {3}",
				getWindowLengthInMilSec(), getOverlapInPerc(),
				getSegmentatorServiceType(), toString(getExtractors()));

		IExtractorInputReader reader = getReaderService()
				.createReaderWithClassifier(getExtractors(), wavFilePath);
		return reader;
	}

	/**
	 * 
	 * @param wavFilePath
	 * @return
	 */
	public MarkerSetHolder extractAndLearn(File wavFilePath) {
		MarkerSetHolder markerSetHorlder = extract(wavFilePath);

		MarkerSet segments = findSegementedLowestMarkers(markerSetHorlder);
		log.debug("[extractAndLearn] marker size {0}", segments.getMarkers()
				.size());

		IExtractorInputReader reader = createReaderWithClassifier(wavFilePath);
		extractAndLearn(wavFilePath, segments, reader );

		return markerSetHorlder;
	}
	/**
	 * 
	 * @param extractors2
	 * @return
	 */
	private String toString(ExtractorEnum[] extractors2) {
		StringBuilder sb = new StringBuilder();
		sb.append("{");
		String seperator = "";
		for (ExtractorEnum extractorEnum : extractors2) {
			sb.append(seperator).append(extractorEnum.name());
			seperator = ", ";
		}
		sb.append("]");
		return sb.toString();
	}
	/**
	 * 
	 */
	public MarkerSet extractAndLearn(File filePath,
			MarkerSet segments, IExtractorInputReader reader) {

		IExtractorInputReader localReader = null;
		if (reader == null) {
			localReader = createReaderWithClassifier(filePath);
		} else {
			localReader = reader;
		}
		URL fileUrl = toUrl(filePath);
		int result = 0;
		// process markers
		Assert.isTrue(segments != null);
		for (Marker marker : segments.getMarkers()) {
			marker.setLabel(createLabel(filePath, marker, result));

			if (marker.getLength() > 10) {
				log.error("!!!remove marker!!!!. this should be eliminated by rules");
				learn(fileUrl, marker, localReader);
			}
			result++;
		}
		return segments;

	}

	public String createLabel(File filePath, Marker marker) {
		return createLabel(filePath, marker, 0);
	}
	
	/**
	 * 
	 * @param filePath
	 * @param marker
	 * @param result
	 * @return
	 */
	protected String createLabel(File filePath, Marker marker, int result) {
		return MessageFormat.format("{0}-{1}-{2}", marker.getLabel().trim(),
				filePath.getName(), (result + 1)).toString();
	}

	/**
	 * 
	 * @param markerSetHolder
	 * @return
	 */
	public MarkerSet findSegementedLowestMarkers(MarkerSetHolder markerSetHolder) {
		MarkerSet segments = markerSetHolder.getMarkerSets().get(
				MarkerSetHolderEnum.phone.name());
		if (segments == null) {
			segments = markerSetHolder.getMarkerSets().get(
					MarkerSetHolderEnum.word.name());
		}
		return segments;
	}
	/**
	 * 
	 * @param markerSetHolder
	 * @return
	 */
	public MarkerSet findSegementedHighestMarkers(MarkerSetHolder markerSetHolder) {
		MarkerSet segments = markerSetHolder.getMarkerSets().get(
				MarkerSetHolderEnum.word.name());
		if (segments == null) {
			segments = markerSetHolder.getMarkerSets().get(
					MarkerSetHolderEnum.phone.name());
		}
		return segments;
	}

	/**
	 * Find markers
	 * 
	 * @param filePath
	 * @return
	 */
	protected MarkerSetHolder findMarkers(IExtractorInputReader reader) {
		Collection<IClassifier> clasifiers = new ArrayList<IClassifier>();
		for (IGeneralExtractor extractor : reader.getGeneralExtractor()) {
			if (extractor instanceof IClassifier) {
				clasifiers.add((IClassifier) extractor);
			}
		}

		log.debug("[findMarkers] clasifiers size {0}", clasifiers.size());
		MarkerSetHolder markerSetHorlder = getSegmentator().extractSegments(
				clasifiers, getSegmentionParam());

		return markerSetHorlder;
	}

	/**
	 * 
	 * @param file
	 * @return
	 */
	protected URL toUrl(File file) {
		URL fileUrl = null;
		try {
			fileUrl = file.toURI().toURL();
			return fileUrl;
		} catch (MalformedURLException ex) {
			throw new ProcessingException(ex);
		}
	}

	protected CorpusEntry create(Marker marker, IExtractorInputReader reader) {
		Map<String, IValues> fvv = getReaderService()
				.findAllVectorValuesForMarker(reader, marker);
		CorpusEntry corpusEntry = getCorpusService().create(marker.getLabel(),
				fvv);
		return corpusEntry;
	}

	/**
	 * 
	 * @param fileUrl
	 * @param marker
	 * @param reader
	 * @return
	 */
	public CorpusEntry learn(URL fileUrl, Marker marker,
			IExtractorInputReader reader) {

		Float signalLength = AudioManagerFactory.createAudioManager()
				.findLength(fileUrl);
		Long markerEnd = marker.getEnd();

		if (marker.getStart() > (signalLength * 1000)) {
			log.error("[learn] incorect length " + marker);
			return null;
		}
		if ((signalLength * 1000) < markerEnd) {
			log.error("[learn] exceed length " + marker);
			marker.setEnd((long) (1000 * signalLength));
		}

		AudioInputStream ais = AudioManagerFactory.createAudioManager()
				.findInputStreamInMils(fileUrl, marker.getStart(),
						marker.getLength());

		CorpusEntry corpusEntry = create(marker, reader);
		getCorpusService().learn(corpusEntry, ais);
		return corpusEntry;
	}

	/**
	 * 
	 * @param marker
	 * @param reader
	 * @return
	 */
	public RecognitionResult match(Marker marker,
			IExtractorInputReader reader) {
		CorpusEntry corpusEntry = create(marker, reader);
		RecognitionResult result = getCorpusService()
				.matchByCorpusEntry(corpusEntry);
		return result;
	}

	public OnlineDecisionSegmentatorParam getSegmentionParam() {
		if (segmentionParam == null) {
			segmentionParam = new OnlineDecisionSegmentatorParam();
			segmentionParam.setMinLength(91L);
			segmentionParam.setMinSpace(61L);
			segmentionParam.setExpandStart(60L);
			segmentionParam.setExpandEnd(60L);
		}
		return segmentionParam;
	}

	public void setSegmentionParam(
			OnlineDecisionSegmentatorParam segmentionParam) {
		this.segmentionParam = segmentionParam;
	}

	public ISegmentatorService getSegmentator() {
		if (segmentator == null) {
			segmentator = SegmentFactory
					.createSegmentator(getSegmentatorServiceType());
		}
		return segmentator;
	}

	public void setSegmentator(ISegmentatorService segmentator) {
		this.segmentator = segmentator;
	}

	public ExtractorReaderService getReaderService() {
		if (readerService == null) {
			ExtractorReaderServiceImpl readerServiceImpl = new ExtractorReaderServiceImpl();
			readerServiceImpl
					.setWindowLengthInMilSec(getWindowLengthInMilSec());
			readerServiceImpl.setOverlapInPerc(getOverlapInPerc());
			readerServiceImpl.setRulesTurnedOn(isRulesTurnedOn());
			readerServiceImpl.setRulePath(getRulePath());
			readerService = readerServiceImpl;
		}
		return readerService;
	}

	public void setReaderService(ExtractorReaderService readerService) {
		this.readerService = readerService;
	}

	public ExtractorEnum[] getExtractors() {
		if (extractors == null) {
			extractors = new ExtractorEnum[] { ExtractorEnum.MFCC_EXTRACTOR,
					ExtractorEnum.LPC_EXTRACTOR, ExtractorEnum.FFT_EXTRACTOR,
					ExtractorEnum.SPECTRAL_FLUX_EXTRACTOR,
			// ExtractorEnum.SIGNAL_ENTROPY_EXTRACTOR
			};
		}
		return extractors;
	}

	public void setExtractors(ExtractorEnum[] extractors) {
		this.extractors = extractors;
	}

	public CorpusService getCorpusService() {
		if (corpusService == null) {
			corpusService = RecognitionServiceFactory.createCorpusService();
		}
		return corpusService;
	}

	public void setCorpusService(CorpusService corpusService) {
		this.corpusService = corpusService;
	}

	public AudioManager getAudioManager() {
		return audioManager;
	}

	public void setAudioManager(AudioManager audioManager) {
		this.audioManager = audioManager;
	}

	public int getWindowLengthInMilSec() {
		return windowLengthInMilSec;
	}

	public void setWindowLengthInMilSec(int windowLengthInMilSec) {
		this.windowLengthInMilSec = windowLengthInMilSec;
		setReaderService(null);
	}

	public int getOverlapInPerc() {
		return overlapInPerc;
	}

	public void setOverlapInPerc(int overlapInPerc) {
		this.overlapInPerc = overlapInPerc;
		setReaderService(null);
	}

	public String getSegmentatorServiceType() {
		return segmentatorServiceType;
	}

	public void setSegmentatorServiceType(String segmentatorService) {
		this.segmentatorServiceType = segmentatorService;
	}
	public String getRulePath() {
		return rulePath;
	}
	public void setRulePath(String rulePath) {
		this.rulePath = rulePath;
	}
	public boolean isRulesTurnedOn() {
		return rulesTurnedOn;
	}
	public void setRulesTurnedOn(boolean rulesTurnedOn) {
		this.rulesTurnedOn = rulesTurnedOn;
	}
}

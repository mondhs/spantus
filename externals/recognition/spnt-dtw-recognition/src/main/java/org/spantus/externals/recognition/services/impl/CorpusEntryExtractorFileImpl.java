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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sound.sampled.AudioInputStream;

import org.spantus.core.IValues;
import org.spantus.core.beans.RecognitionResult;
import org.spantus.core.beans.SignalSegment;
import org.spantus.core.extractor.ExtractorParam;
import org.spantus.core.extractor.IExtractorInputReader;
import org.spantus.core.marker.Marker;
import org.spantus.core.marker.MarkerSet;
import org.spantus.core.marker.MarkerSetHolder;
import org.spantus.core.marker.MarkerSetHolder.MarkerSetHolderEnum;
import org.spantus.core.service.CorpusService;
import org.spantus.core.threshold.ClassifierEnum;
import org.spantus.core.wav.AudioManager;
import org.spantus.core.wav.AudioManagerFactory;
import org.spantus.exception.ProcessingException;
import org.spantus.externals.recognition.services.CorpusEntryExtractor;
import org.spantus.externals.recognition.services.RecognitionServiceFactory;
import org.spantus.extractor.ExtractorsFactory;
import org.spantus.extractor.impl.ExtractorEnum;
import org.spantus.logger.Logger;
import org.spantus.segment.ISegmentatorService;
import org.spantus.segment.SegmentFactory;
import org.spantus.segment.SegmentationServiceImpl;
import org.spantus.segment.online.OnlineDecisionSegmentatorParam;
import org.spantus.utils.Assert;
import org.spantus.work.services.WorkExtractorReaderService;
import org.spantus.work.services.impl.WorkExtractorReaderServiceImpl;

/**
 * 
 * @author mondhs
 */
public class CorpusEntryExtractorFileImpl implements CorpusEntryExtractor {

	private static final Logger LOG = Logger
			.getLogger(CorpusEntryExtractorFileImpl.class);
	private WorkExtractorReaderService readerService;
	private ExtractorEnum[] extractors;
	private ISegmentatorService segmentator;
	private CorpusService corpusService;
	private AudioManager audioManager;
	private int windowLengthInMilSec = ExtractorsFactory.DEFAULT_WINDOW_LENGHT;
	private int overlapInPerc = ExtractorsFactory.DEFAULT_WINDOW_OVERLAP;
	private String rulePath;
	private boolean rulesTurnedOn;
	private Map<String, ExtractorParam> params;
	private ClassifierEnum classifier = ClassifierEnum.rulesOffline;
	
	private SegmentationServiceImpl segmentationService;

	/**
	 * Find segments(markers), then put them to corpus
	 * 
	 * @param filePath
	 * @return
	 */
	public List<SignalSegment> extractInMemory(File filePath) {
		Assert.isTrue(filePath.exists(), "file not exists" + filePath);
		List<SignalSegment> result = new ArrayList<SignalSegment>();
		// find markers
		IExtractorInputReader reader = createReaderWithClassifier(filePath);

		MarkerSetHolder markerSetHorlder = getSegmentationService().findMarkers(reader);
		MarkerSet segments = getSegmentationService().findSegementedLowestMarkers(markerSetHorlder);

		// process markers
		Assert.isTrue(segments != null);
		for (Marker marker : segments.getMarkers()) {
			SignalSegment corpusEntry = create(marker, reader);
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
		return extract(wavFilePath, reader);
	}

	/**
	 * 
	 * @param wavFilePath
	 * @return
	 */
	public MarkerSetHolder extract(File wavFilePath,
			IExtractorInputReader reader) {
		Assert.isTrue(wavFilePath.exists(), "file not exists" + wavFilePath);
		MarkerSetHolder markerSetHorlder = getSegmentationService().findMarkers(reader);
		return markerSetHorlder;
	}

	/**
	 * 
	 * @param extractors2
	 * @param wavFilePath
	 * @return
	 */
	public IExtractorInputReader createReaderWithClassifier(File wavFilePath) {
		LOG.debug(
				"[createReaderWithClassifier]\nWindow size: {0};\nOverlap: {1};\nSegmenator: {2};\nExtractors: {3}",
				getWindowLengthInMilSec(), getOverlapInPerc(),
				getSegmentatorServiceType(), toString(getExtractors()));

		IExtractorInputReader reader = getReaderService()
				.createReaderWithClassifier(getExtractors(), wavFilePath,
						getParams(), getClassifier() );
		return reader;
	}

	/**
	 * 
	 * @param wavFilePath
	 * @return
	 */
	public MarkerSetHolder extractAndLearn(File wavFilePath) {
		MarkerSetHolder markerSetHorlder = extract(wavFilePath);

		MarkerSet segments = getSegmentationService().findSegementedLowestMarkers(markerSetHorlder);
		LOG.debug("[extractAndLearn] marker size {0}", segments.getMarkers()
				.size());

		IExtractorInputReader reader = createReaderWithClassifier(wavFilePath);
		extractAndLearn(wavFilePath, segments, reader);

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
	public MarkerSet extractAndLearn(File filePath, MarkerSet segments,
			IExtractorInputReader reader) {

		IExtractorInputReader localReader = null;
		if (reader == null) {
			localReader = createReaderWithClassifier(filePath);
		} else {
			localReader = reader;
		}
		URL fileUrl = toUrl(filePath);
		int result = -1;
		// process markers
		Assert.isTrue(segments != null);
		for (Marker marker : segments.getMarkers()) {
			result++;
			marker.setLabel(createLabel(filePath, marker, result));
			// if(marker.getLabel().contains(".wav")){
			// continue;
			// }
			if (marker.getLength() < 10) {
				LOG.error("this should be eliminated by rules" + marker);
			}
			learn(fileUrl, marker, localReader);

		}
		return segments;

	}

	public String createLabel(File filePath, Marker marker) {
		return createLabel(filePath, marker, 0);
	}

	public String createLabelByMarkers(File filePath, Marker marker) {
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
	public MarkerSet findSegementedHighestMarkers(
			MarkerSetHolder markerSetHolder) {
		MarkerSet segments = markerSetHolder.getMarkerSets().get(
				MarkerSetHolderEnum.word.name());
		if (segments == null) {
			segments = markerSetHolder.getMarkerSets().get(
					MarkerSetHolderEnum.phone.name());
		}
		return segments;
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

	protected SignalSegment create(Marker marker, IExtractorInputReader reader) {
		Map<String, IValues> fvv = getReaderService()
				.findAllVectorValuesForMarker(reader, marker);
		return  RecognitionServiceFactory.createSignalSegment(marker.getLabel(),
				fvv);
	}

	/**
	 * 
	 * @param ais
	 * @param marker
	 * @return
	 */
	protected SignalSegment create(AudioInputStream ais, Marker marker) {
		IExtractorInputReader localReader = getReaderService().createReader(
				getExtractors(), ais);
		// CorpusEntry corpusEntry = create(marker, reader);
		Map<String, IValues> fvv = getReaderService()
				.findAllVectorValuesForMarker(localReader);
		SignalSegment corpusEntry = RecognitionServiceFactory.createSignalSegment(marker.getLabel(),
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
      @Override
	public SignalSegment learn(URL fileUrl, Marker marker,
			IExtractorInputReader reader) {
		SignalSegment corpusEntry = null;

		Long signalLength = (long) (1000L * AudioManagerFactory
				.createAudioManager().findLength(fileUrl));
		Long markerEnd = marker.getEnd();

		if (signalLength < markerEnd) {
			LOG.error("[learn] exceed length " + marker);
			marker.setEnd(signalLength);
		}

		AudioInputStream ais = AudioManagerFactory.createAudioManager()
				.findInputStreamInMils(fileUrl, marker.getStart(),
						marker.getLength());
		corpusEntry = create(ais, marker);

		ais = AudioManagerFactory.createAudioManager().findInputStreamInMils(
				fileUrl, marker.getStart(), marker.getLength());

		// CorpusEntry corpusEntry = create(marker, reader);
		getCorpusService().learn(corpusEntry, ais);
		return corpusEntry;
	}

	/**
	 * 
	 * @param marker
	 * @param reader
	 * @return
	 */
	public RecognitionResult match(Marker marker, IExtractorInputReader reader) {
		SignalSegment corpusEntry = create(marker, reader);
		RecognitionResult result = getCorpusService().matchByCorpusEntry(
				corpusEntry);
		return result;
	}

	/**
	 * 
	 * @param marker
	 * @param reader
	 * @return
	 */
	public Map<String, RecognitionResult> bestMatchesForFeatures(URL fileUrl,
			Marker marker, IExtractorInputReader reader) {
		SignalSegment corpusEntry = null;
		corpusEntry = create(marker, reader);
//		try {
//			AudioInputStream ais = AudioManagerFactory.createAudioManager()
//					.findInputStreamInMils(fileUrl, marker.getStart(),
//							marker.getLength());
//			corpusEntry = create(ais, marker);
//		} catch (ProcessingException e) {
//			// this is not audio file
//			corpusEntry = create(marker, reader);
//		}

		Map<String, RecognitionResult> result = getCorpusService()
				.bestMatchesForFeatures(corpusEntry);

		return result;
	}

	public OnlineDecisionSegmentatorParam getSegmentionParam() {
		return getSegmentationService().getSegmentionParam();
	}

	public void setSegmentionParam(
			OnlineDecisionSegmentatorParam segmentionParam) {
		getSegmentationService().setSegmentionParam(segmentionParam);
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

	public WorkExtractorReaderService getReaderService() {
		if (readerService == null) {
			WorkExtractorReaderServiceImpl readerServiceImpl = new WorkExtractorReaderServiceImpl();
			readerServiceImpl
					.setWindowLengthInMilSec(getWindowLengthInMilSec());
			readerServiceImpl.setOverlapInPerc(getOverlapInPerc());
			readerServiceImpl.setRulesTurnedOn(isRulesTurnedOn());
			readerServiceImpl.setRulePath(getRulePath());
			readerService = readerServiceImpl;
		}
		return readerService;
	}

	public void setReaderService(WorkExtractorReaderService readerService) {
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
		return getSegmentationService().getSegmentatorServiceType();
	}

	public void setSegmentatorServiceType(String segmentatorServiceType) {
		getSegmentationService().setSegmentatorServiceType(segmentatorServiceType);
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

	public Map<String, ExtractorParam> getParams() {
		if (params == null) {
			params = new HashMap<String, ExtractorParam>();
		}
		return params;
	}

	public void setParams(Map<String, ExtractorParam> params) {
		this.params = params;
	}

	public ClassifierEnum getClassifier() {
		return classifier;
	}

	public void setClassifier(ClassifierEnum classifier) {
		this.classifier = classifier;
	}

	public SegmentationServiceImpl getSegmentationService() {
		if(segmentationService == null){
			segmentationService = new SegmentationServiceImpl();
		}
		return segmentationService;
	}

	public void setSegmentationService(SegmentationServiceImpl segmentationService) {
		this.segmentationService = segmentationService;
	}
}

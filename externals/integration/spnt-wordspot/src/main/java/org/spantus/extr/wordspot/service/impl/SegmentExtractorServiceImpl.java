package org.spantus.extr.wordspot.service.impl;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.spantus.core.IValues;
import org.spantus.core.beans.SignalSegment;
import org.spantus.core.extractor.ExtractorParam;
import org.spantus.core.extractor.IExtractorConfig;
import org.spantus.core.extractor.IExtractorInputReader;
import org.spantus.core.extractor.SignalFormat;
import org.spantus.core.extractor.preemphasis.Preemphasis.PreemphasisEnum;
import org.spantus.core.io.SignalReader;
import org.spantus.core.marker.Marker;
import org.spantus.core.marker.MarkerSet;
import org.spantus.core.marker.MarkerSetHolder;
import org.spantus.core.marker.MarkerSetHolder.MarkerSetHolderEnum;
import org.spantus.core.threshold.ClassifierEnum;
import org.spantus.core.threshold.IClassifier;
import org.spantus.extr.wordspot.service.SegmentExtractorService;
import org.spantus.extractor.ExtractorConfigUtil;
import org.spantus.extractor.ExtractorsFactory;
import org.spantus.extractor.impl.ExtractorEnum;
import org.spantus.extractor.impl.ExtractorModifiersEnum;
import org.spantus.extractor.impl.ExtractorUtils;
import org.spantus.logger.Logger;
import org.spantus.segment.ISegmentatorService;
import org.spantus.segment.SegmentFactory;
import org.spantus.segment.SegmentFactory.SegmentatorServiceEnum;
import org.spantus.segment.online.OnlineDecisionSegmentatorParam;
import org.spantus.utils.ExtractorParamUtils;
import org.spantus.work.io.WorkAudioFactory;
import org.spantus.work.services.WorkExtractorReaderService;
import org.spantus.work.services.WorkServiceFactory;
/**
 * 
 * @author Mindaugas Greibus
 * @since 0.3
 * Created: May 7, 2012
 *
 */
public class SegmentExtractorServiceImpl implements SegmentExtractorService {

	private static final Logger LOG = Logger
			.getLogger(SegmentExtractorServiceImpl.class);

	private Map<String, ExtractorParam> extractorParams = new HashMap<String, ExtractorParam>();

	private PreemphasisEnum preephasis = PreemphasisEnum.full;

	private ClassifierEnum classifier = ClassifierEnum.online;

	private Float thresholdCoef = 3F;

	private SegmentatorServiceEnum segmentation = SegmentatorServiceEnum.basic;

	private OnlineDecisionSegmentatorParam segmentationParam;

	// private RecognitionMarkerSegmentatorListenerImpl iSegmentatorListener;

	private ExtractorEnum[] extractors = new ExtractorEnum[] {
			ExtractorEnum.SIGNAL_ENTROPY_EXTRACTOR,
			ExtractorEnum.SPECTRAL_FLUX_EXTRACTOR,
			ExtractorEnum.ENERGY_EXTRACTOR,

	};

	private WorkExtractorReaderService extractorReaderService;

	String repositoryPath;

	public SegmentExtractorServiceImpl() {
		segmentationParam = new OnlineDecisionSegmentatorParam();
		segmentationParam.setMinSpace(0L);
		segmentationParam.setMinLength(0L);
		segmentationParam.setExpandStart(0L);
		segmentationParam.setExpandEnd(0L);

		updateParams();
	}

	public void updateParams() {
		extractorParams = new HashMap<String, ExtractorParam>();
		for (ExtractorEnum extractEnum : extractors) {
			ExtractorParam param = new ExtractorParam();
			ExtractorParamUtils.setValue(param,
					ExtractorModifiersEnum.smooth.name(), Boolean.TRUE);
			ExtractorParamUtils.setValue(param,
					ExtractorModifiersEnum.mean.name(), Boolean.TRUE);
			ExtractorParamUtils.<Float> setValue(param,
					ExtractorParamUtils.commonParam.threasholdCoef.name(),
					getThresholdCoef());

			extractorParams.put(extractEnum.name(), param);

		}
	}

	/**
	 * 
	 */
	@Override
	public Collection<SignalSegment> extractSegmentsOnline(URL urlFile) {
		RecognitionMarkerSegmentatorListenerImpl listener = new RecognitionMarkerSegmentatorListenerImpl();
		listenSegments(urlFile, listener);
		Collection<SignalSegment> segments = listener.getSignalSegments();
		LOG.debug("[extractSegments]Found syllables {0}", segments);
		return segments;
	}
	/**
	 * 
	 */
	@Override
	public void listenSegments(URL urlFile, RecognitionMarkerSegmentatorListenerImpl listener) {
		listener.setRepositoryPath(getRepositoryPath());
		readSignal(urlFile, getExtractors(), listener);
	}

	/**
	 * 
	 */
	public Collection<SignalSegment> extractSegmentsOffline(URL urlFile) {
		Collection<SignalSegment> segments = new ArrayList<SignalSegment>();

		IExtractorInputReader extractorReader = readSignal(urlFile,
				getExtractors(), null);
		Set<IClassifier> classifiers = ExtractorUtils
				.filterOutClassifers(extractorReader);
		MarkerSetHolder markerSetHolder = extractMarkerSetHolder(classifiers,
				getSegmentationParam());
		MarkerSet syllables = markerSetHolder.getMarkerSets().get(
				MarkerSetHolderEnum.phone.name());
		for (Marker marker : syllables) {
			SignalSegment segment = new SignalSegment();
			segment.setMarker(marker);
			Map<String, IValues> features = getExtractorReaderService()
					.findAllVectorValuesForMarker(extractorReader, marker);
			segment.putAll(features);
			segments.add(segment);
		}
		LOG.debug("[extractSegments]Found syllables {0}", segments);
		return segments;

	}

	/**
	 * 
	 * @param urlFile
	 * @param extractors
	 * @param iSegmentatorListener
	 *            -- optional
	 * @return
	 */
	protected IExtractorInputReader readSignal(URL urlFile,
			ExtractorEnum[] extractors,
			RecognitionMarkerSegmentatorListenerImpl iSegmentatorListener) {
		SignalReader reader = WorkAudioFactory.createAudioReader(urlFile);
		SignalFormat format = reader.getFormat(urlFile);

		IExtractorConfig config = ExtractorConfigUtil.defaultConfig(
				format.getSampleRate(), 10, 33);// 10 ms and 33 %
		config.setPreemphasis(getPreephasis().name());

		IExtractorInputReader extractorReader = ExtractorsFactory
				.createReader(config);
		List<IClassifier> classifiers = ExtractorUtils.registerThreshold(
				extractorReader, extractors, getExtractorParams(),
				getClassifier());

		if (iSegmentatorListener != null) {
			iSegmentatorListener.setConfig(config);
			for (IClassifier iClassifier : classifiers) {
				iClassifier.addClassificationListener(iSegmentatorListener);
			}
		}

		reader.readSignal(urlFile, extractorReader);

		return extractorReader;

	}

	private MarkerSetHolder extractMarkerSetHolder(
			Set<IClassifier> classifiers, OnlineDecisionSegmentatorParam param) {
		MarkerSetHolder markerSetHolder = null;
		ISegmentatorService online = (ISegmentatorService) SegmentFactory
				.createSegmentator(getSegmentation().name());
		markerSetHolder = online.extractSegments(classifiers, param);
		return markerSetHolder;
	}

	public Map<String, ExtractorParam> getExtractorParams() {
		return extractorParams;
	}

	public void setExtractorParams(Map<String, ExtractorParam> extractorParams) {
		this.extractorParams = extractorParams;
	}

	public ClassifierEnum getClassifier() {
		return classifier;
	}

	public void setClassifier(ClassifierEnum classifier) {
		this.classifier = classifier;
	}

	public ExtractorEnum[] getExtractors() {
		return extractors;
	}

	public void setExtractors(ExtractorEnum[] extractors) {
		this.extractors = extractors;
	}

	public OnlineDecisionSegmentatorParam getSegmentationParam() {
		return segmentationParam;
	}

	public void setSegmentationParam(
			OnlineDecisionSegmentatorParam segmentationParam) {
		this.segmentationParam = segmentationParam;
	}

	public SegmentatorServiceEnum getSegmentation() {
		return segmentation;
	}

	public void setSegmentation(SegmentatorServiceEnum segmentation) {
		this.segmentation = segmentation;
	}

	public PreemphasisEnum getPreephasis() {
		return preephasis;
	}

	public void setPreephasis(PreemphasisEnum preephasis) {
		this.preephasis = preephasis;
	}

	public Float getThresholdCoef() {
		return thresholdCoef;
	}

	public void setThresholdCoef(Float thresholdCoef) {
		this.thresholdCoef = thresholdCoef;
	}

	public WorkExtractorReaderService getExtractorReaderService() {
		if (extractorReaderService == null) {
			extractorReaderService = WorkServiceFactory
					.createExtractorReaderService();
		}
		return extractorReaderService;
	}

	public String getRepositoryPath() {
		return repositoryPath;
	}

	public void setRepositoryPath(String repositoryPath) {
		this.repositoryPath = repositoryPath;
	}



}

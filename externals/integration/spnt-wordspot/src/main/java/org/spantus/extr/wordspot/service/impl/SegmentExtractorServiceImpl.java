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
import org.spantus.core.extractor.*;
import org.spantus.core.io.SignalReader;
import org.spantus.core.marker.Marker;
import org.spantus.core.marker.MarkerSet;
import org.spantus.core.marker.MarkerSetHolder;
import org.spantus.core.marker.MarkerSetHolder.MarkerSetHolderEnum;
import org.spantus.core.threshold.IClassifier;
import org.spantus.extr.wordspot.domain.SegmentExtractorServiceConfig;
import org.spantus.extr.wordspot.service.SegmentExtractorService;
import org.spantus.extractor.ExtractorConfigUtil;
import org.spantus.extractor.ExtractorsFactory;
import org.spantus.extractor.impl.ExtractorEnum;
import org.spantus.extractor.impl.ExtractorModifiersEnum;
import org.spantus.extractor.impl.ExtractorUtils;
import org.spantus.logger.Logger;
import org.spantus.math.windowing.WindowingEnum;
import org.spantus.segment.ISegmentatorService;
import org.spantus.segment.SegmentFactory;
import org.spantus.segment.online.AsyncMarkerSegmentatorListenerImpl;
import org.spantus.segment.online.ISegmentatorListener;
import org.spantus.segment.online.OnlineDecisionSegmentatorParam;
import org.spantus.utils.ExtractorParamUtils;
import org.spantus.work.io.WorkAudioFactory;
import org.spantus.work.services.WorkExtractorReaderService;
import org.spantus.work.services.WorkServiceFactory;

/**
 * 
 * @author Mindaugas Greibus
 * @since 0.3 Created: May 7, 2012
 * 
 */
public class SegmentExtractorServiceImpl implements SegmentExtractorService {

	private static final Logger LOG = Logger
			.getLogger(SegmentExtractorServiceImpl.class);

	private SegmentExtractorServiceConfig config = new SegmentExtractorServiceConfig();
	private WorkExtractorReaderService extractorReaderService;

	public SegmentExtractorServiceImpl() {

		updateParams();
	}

	public void updateParams() {
		getConfig().setExtractorParams(new HashMap<String, ExtractorParam>());
		for (ExtractorEnum extractEnum : getConfig().getExtractors()) {
			ExtractorParam param = new ExtractorParam();
			ExtractorParamUtils.setValue(param,
					ExtractorModifiersEnum.smooth.name(), Boolean.TRUE);
			ExtractorParamUtils.setValue(param,
					ExtractorModifiersEnum.mean.name(), Boolean.TRUE);
			ExtractorParamUtils.<Float> setValue(param,
					ExtractorParamUtils.commonParam.threasholdCoef.name(),
					getConfig().getThresholdCoef());

			getConfig().getExtractorParams().put(extractEnum.name(), param);
		}
	}

	/**
	 * 
	 */
	@Override
	public Collection<SignalSegment> extractSegmentsOnline(URL urlFile) {
		RecognitionMarkerSegmentatorListenerImpl listener = new RecognitionMarkerSegmentatorListenerImpl();
		listener.setRepositoryPath(getConfig().getRepositoryPath());
		AsyncMarkerSegmentatorListenerImpl asyncListener = new AsyncMarkerSegmentatorListenerImpl(
				listener);
		listenSegments(urlFile, asyncListener);
		Collection<SignalSegment> segments = asyncListener.getSignalSegments();
		if(LOG.isDebugMode()){
			for (SignalSegment segment : segments) {
				LOG.debug("[extractSegmentsOnline]Found marker {0}", segment.getMarker());
			}
		}
		return segments;
	}

	/**
	 * 
	 */
	@Override
	public void listenSegments(URL urlFile, ISegmentatorListener listener) {
		readSignal(urlFile, getConfig().getExtractors(), listener);
	}

	/**
	 * 
	 */
	public Collection<SignalSegment> extractSegmentsOffline(URL urlFile) {
		Collection<SignalSegment> segments = new ArrayList<SignalSegment>();

		IExtractorInputReader extractorReader = readSignal(urlFile, getConfig()
				.getExtractors(), null);
		Set<IClassifier> classifiers = ExtractorUtils
				.filterOutClassifers(extractorReader);
		MarkerSetHolder markerSetHolder = extractMarkerSetHolder(classifiers,
				getConfig().getSegmentationParam());
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
		if(LOG.isDebugMode()){
			for (Marker marker : syllables) {
				LOG.debug("[extractSegmentsOffline]Found marker {0}", marker);
			}
		}
		
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
			ISegmentatorListener iSegmentatorListener) {
		SignalReader reader = WorkAudioFactory.createAudioReader(urlFile);
		SignalFormat format = reader.getFormat(urlFile);

		IExtractorConfig config = ExtractorConfigUtil.defaultConfig(
				format.getSampleRate(), getConfig().getWindowLength(),
				getConfig().getOverlapInPerc());// 10 ms and 33 %
                config.setWindowing(WindowingEnum.Hamming.name());
		config.setPreemphasis(getConfig().getPreephasis().name());
		IExtractorInputReader extractorReader = ExtractorsFactory
				.createReader(config);
		List<IClassifier> classifiers = ExtractorUtils.registerThreshold(
				extractorReader, extractors, getConfig().getExtractorParams(),
				getConfig().getClassifier());

		if (iSegmentatorListener != null) {
			iSegmentatorListener.setConfig(config);
                        if(iSegmentatorListener instanceof IExtractorInputReaderAware){
                            ((IExtractorInputReaderAware)iSegmentatorListener).setExtractorInputReader(extractorReader);
                        }
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
				.createSegmentator(getConfig().getSegmentation().name());
		markerSetHolder = online.extractSegments(classifiers, param);
		return markerSetHolder;
	}

	public SegmentExtractorServiceConfig getConfig() {
		return config;
	}

	public void setConfig(SegmentExtractorServiceConfig config) {
		this.config = config;
	}

	public WorkExtractorReaderService getExtractorReaderService() {
		if (extractorReaderService == null) {
			extractorReaderService = WorkServiceFactory
					.createExtractorReaderService();
		}
		return extractorReaderService;
	}

}

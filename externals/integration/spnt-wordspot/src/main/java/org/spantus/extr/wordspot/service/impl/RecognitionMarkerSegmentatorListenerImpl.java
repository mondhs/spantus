package org.spantus.extr.wordspot.service.impl;

import java.util.List;
import java.util.Map;

import org.spantus.core.FrameValues;
import org.spantus.core.FrameVectorValues;
import org.spantus.core.beans.FrameVectorValuesHolder;
import org.spantus.core.beans.RecognitionResult;
import org.spantus.core.beans.SignalSegment;
import org.spantus.core.extractor.IExtractorConfig;
import org.spantus.core.service.CorpusService;
import org.spantus.externals.recognition.services.RecognitionServiceFactory;
import org.spantus.extractor.impl.ExtractorEnum;
import org.spantus.extractor.impl.MFCCExtractor;
import org.spantus.logger.Logger;
import org.spantus.segment.online.MarkerSegmentatorListenerImpl;
import org.spantus.utils.Assert;
import org.spantus.utils.StringUtils;

public class RecognitionMarkerSegmentatorListenerImpl extends
		MarkerSegmentatorListenerImpl {
	
	private static final Logger LOG = Logger.getLogger(RecognitionMarkerSegmentatorListenerImpl.class);
	
	private IExtractorConfig config;
	
	private CorpusService corpusService;

	private String repositoryPath = null;
	
	@Override
	protected void processEndedSegment(SignalSegment signalSegment) {

		Map<String, FrameVectorValuesHolder> vectorMap = signalSegment.getFeatureFrameVectorValuesMap();
		FrameVectorValuesHolder signalWindows = vectorMap.get(MarkerSegmentatorListenerImpl.SIGNAL_WINDOWS);
		MFCCExtractor mfcc = new MFCCExtractor();
		
		mfcc.setConfig(config);
		FrameVectorValues mffcValues =mfcc.newFrameVectorValues();
		for (List<Double> signalWindow : signalWindows.getValues()) {
			mffcValues.addAll(mfcc.calculateWindow((FrameValues) signalWindow));
		}
		vectorMap.put(ExtractorEnum.MFCC_EXTRACTOR.name(), new FrameVectorValuesHolder(mffcValues));
		RecognitionResult result = getCorpusService().matchByCorpusEntry(signalSegment);
		if(result == null){
			throw new IllegalArgumentException("No recognition information in corpus is found");
		}
		String name = result.getInfo().getName();
		
		signalSegment.setName(name);
		signalSegment.getMarker().setLabel(signalSegment.getName());
		
		LOG.debug("[processEndedSegment] spotted: {0} in time [{1}:{2}] ", signalSegment.getName(), signalSegment.getMarker().getStart(), signalSegment.getMarker().getEnd() );
		
	}

	public IExtractorConfig getConfig() {
		return config;
	}

	public void setConfig(IExtractorConfig config) {
		this.config = config;
	}

	public CorpusService getCorpusService() {
		if(corpusService == null){
			Assert.isTrue(StringUtils.hasText(repositoryPath));
			corpusService = RecognitionServiceFactory.createCorpusService(repositoryPath);
		}
		return corpusService;
	}

	public void setCorpusService(CorpusService corpusService) {
		this.corpusService = corpusService;
	}

	public String getRepositoryPath() {
		return repositoryPath;
	}

	public void setRepositoryPath(String repositoryPath) {
		this.repositoryPath = repositoryPath;
	}
}

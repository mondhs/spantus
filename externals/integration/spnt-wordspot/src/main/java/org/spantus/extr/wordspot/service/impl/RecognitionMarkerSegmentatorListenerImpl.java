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
/**
 * 
 * @author Mindaugas Greibus
 * @since 0.3
 * Created: May 7, 2012
 *
 */
public class RecognitionMarkerSegmentatorListenerImpl extends
		MarkerSegmentatorListenerImpl {
	
	private static final Logger LOG = Logger.getLogger(RecognitionMarkerSegmentatorListenerImpl.class);
	
	private IExtractorConfig config;
	
	private CorpusService corpusService;

	private String repositoryPath = null;
	
	@Override
	protected boolean processEndedSegment(SignalSegment signalSegment) {

		Map<String, FrameVectorValuesHolder> vectorMap = signalSegment.getFeatureFrameVectorValuesMap();
		FrameVectorValuesHolder signalWindows = vectorMap.get(MarkerSegmentatorListenerImpl.SIGNAL_WINDOWS);
		MFCCExtractor mfcc = new MFCCExtractor();
		
		mfcc.setConfig(config);
		FrameVectorValues mffcValues =mfcc.newFrameVectorValues();
		for (List<Double> signalWindow : signalWindows.getValues()) {
			mffcValues.addAll(mfcc.calculateWindow((FrameValues) signalWindow));
		}
                if(mffcValues.size()==0){
                    LOG.error("nothing to recognize" + signalSegment);
                    return false;
                }
                    
		Assert.isTrue(mffcValues.size()>0, "MFCC is not calculated. Size {0} ", mffcValues.size());
//		SignalSegment recognitionSignalSegment = new SignalSegment();
//		recognitionSignalSegment.setMarker(signalSegment.getMarker());
//		recognitionSignalSegment.setName(signalSegment.getName());
//		recognitionSignalSegment.getFeatureFrameVectorValuesMap().put(ExtractorEnum.MFCC_EXTRACTOR.name(), new FrameVectorValuesHolder(mffcValues));
		signalSegment.getFeatureFrameVectorValuesMap().put(ExtractorEnum.MFCC_EXTRACTOR.name(), new FrameVectorValuesHolder(mffcValues));
		
		String name = match(signalSegment);
	
		
		signalSegment.setName(name);
		signalSegment.getMarker().setLabel(name);
		
		LOG.debug("[processEndedSegment] spotted: {0} in time [{1}:{2}] ", signalSegment.getName(), signalSegment.getMarker().getStart(), signalSegment.getMarker().getEnd() );
		return !"-".equals(name);
	}
	/**
	 * 
	 * @param segment 
	 * @param signalSegment
	 * @return
	 */
	protected String match(SignalSegment segment) {
		RecognitionResult result = getCorpusService().matchByCorpusEntry(segment);
		
		if(result == null){
			throw new IllegalArgumentException("No recognition information in corpus is found");
		}
		String name = result.getInfo().getName();
		return name;
	}

	public IExtractorConfig getConfig() {
		return config;
	}

	public void setConfig(IExtractorConfig config) {
		this.config = config;
	}

	public CorpusService getCorpusService() {
		if(corpusService == null){
			Assert.isTrue(StringUtils.hasText(repositoryPath), "Repository path not set");
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

package org.spantus.extr.wordspot.service.impl;

import java.util.Map;
import org.spantus.core.FrameVectorValues;

import org.spantus.core.IValues;
import org.spantus.core.beans.FrameVectorValuesHolder;
import org.spantus.core.beans.RecognitionResult;
import org.spantus.core.beans.SignalSegment;
import org.spantus.core.extractor.IExtractorConfig;
import org.spantus.core.extractor.IExtractorInputReader;
import org.spantus.core.extractor.IExtractorInputReaderAware;
import org.spantus.core.marker.Marker;
import org.spantus.core.service.CorpusService;
import org.spantus.externals.recognition.services.RecognitionServiceFactory;
import org.spantus.extr.wordspot.domain.SegmentExtractorServiceConfig;
import org.spantus.extr.wordspot.domain.SegmentExtractorServiceConfigAware;
import org.spantus.extractor.impl.ExtractorEnum;
import org.spantus.logger.Logger;
import org.spantus.segment.online.MarkerSegmentatorListenerImpl;
import org.spantus.utils.Assert;
import org.spantus.utils.StringUtils;
import org.spantus.work.services.WorkExtractorReaderService;
import org.spantus.work.services.WorkServiceFactory;

/**
 *
 * @author Mindaugas Greibus
 * @since 0.3 Created: May 7, 2012
 *
 */
public class RecognitionMarkerSegmentatorListenerImpl extends MarkerSegmentatorListenerImpl
    implements IExtractorInputReaderAware, SegmentExtractorServiceConfigAware {

    private static final Logger LOG = Logger.getLogger(RecognitionMarkerSegmentatorListenerImpl.class);
    private IExtractorConfig config;
    private CorpusService corpusService;
    private String repositoryPath = null;
    private IExtractorInputReader extractorInputReader;
    private WorkExtractorReaderService extractorReaderService;
    private SegmentExtractorServiceConfig serviceConfig;
	private int operationCount;
    public RecognitionMarkerSegmentatorListenerImpl() {
        LOG.debug("Init");
    }
    
    

    protected boolean processEndedSegment(SignalSegment signalSegment) {

//        Map<String, FrameVectorValuesHolder> vectorMap = signalSegment.getFeatureFrameVectorValuesMap();
        //		FrameVectorValuesHolder signalWindows = vectorMap.get(MarkerSegmentatorListenerImpl.SIGNAL_WINDOWS);
        Marker aMarker = signalSegment.getMarker().clone();
        aMarker.setStart(aMarker.getStart());

        RecognitionResult result = recalculateAndMatch(signalSegment);
        if(result == null){
            return false;
        }
        String name = result.getInfo().getName();

        signalSegment.setName(name);
        signalSegment.getMarker().setLabel(name);

        LOG.debug("[processEndedSegment] spotted: {0} in time [{1}:{2}] ", signalSegment.getName(), signalSegment.getMarker().getStart(), signalSegment.getMarker().getEnd());
        return !"-".equals(name);
    }
    
       public void setExtractorReaderService(WorkExtractorReaderService extractorReaderService) {
        this.extractorReaderService = extractorReaderService;
    }

    public SegmentExtractorServiceConfig getServiceConfig() {
        return this.serviceConfig;
    }

    public void setServiceConfig(SegmentExtractorServiceConfig serviceConfig) {
        this.serviceConfig = serviceConfig;
    }

    protected RecognitionResult recalculateAndMatch(SignalSegment theSignalSegment) {
        Marker aMarker = theSignalSegment.getMarker();
        recalculateFeatures(theSignalSegment, aMarker);
        RecognitionResult result = match(theSignalSegment);
        return result;
    }

    protected boolean recalculateFeatures(SignalSegment theSignalSegment, Marker theMarker) {
         Map<String, IValues> fvv = recalculateFeatures(theMarker);
        if(fvv == null){
            return false;
        }
        IValues mffcValues = fvv.get(ExtractorEnum.MFCC_EXTRACTOR.name());

        if (mffcValues == null) {
            return false;
        }
        Assert.isTrue(mffcValues.size() > 0, "MFCC is not calculated. Size {0} ", mffcValues.size());

        theSignalSegment.getFeatureFrameVectorValuesMap().put(ExtractorEnum.MFCC_EXTRACTOR.name(),
                new FrameVectorValuesHolder((FrameVectorValues) mffcValues));
        return true;
    }
    
    protected Map<String, IValues> recalculateFeatures(Marker theMarker) {
         Map<String, IValues> fvv = getExtractorReaderService().recalcualteValues(getExtractorInputReader(),
                theMarker,
                ExtractorEnum.MFCC_EXTRACTOR.name());
        return fvv;
    }
    
    /**
     *
     * @param segment
     * @param signalSegment
     * @return
     */
    protected RecognitionResult match(SignalSegment segment) {
        RecognitionResult result = getCorpusService().matchByCorpusEntry(segment);
        this.operationCount++;
        if (result == null) {
            throw new IllegalArgumentException("No recognition information in corpus is found");
        }
        
        return result;
    }
    

    public IExtractorConfig getConfig() {
        return config;
    }

    public void setConfig(IExtractorConfig config) {
        this.config = config;
    }

    public CorpusService getCorpusService() {
        if (corpusService == null) {
            Assert.isTrue(StringUtils.hasText(repositoryPath), "Repository path not set");
            corpusService = RecognitionServiceFactory.createCorpusServicePartialSearch(
                    repositoryPath, 
                    getServiceConfig().getSyllableDtwRadius(), ExtractorEnum.MFCC_EXTRACTOR.name());
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

    @Override
    public void setExtractorInputReader(IExtractorInputReader extractorInputReader) {
        this.extractorInputReader = extractorInputReader;
    }

    public IExtractorInputReader getExtractorInputReader() {
        return extractorInputReader;
    }

    public WorkExtractorReaderService getExtractorReaderService() {
        if (extractorReaderService == null) {
            extractorReaderService = WorkServiceFactory.createExtractorReaderService();
        }
        return extractorReaderService;
    }



	public int getOperationCount() {
		return operationCount;
	}

 

    
}

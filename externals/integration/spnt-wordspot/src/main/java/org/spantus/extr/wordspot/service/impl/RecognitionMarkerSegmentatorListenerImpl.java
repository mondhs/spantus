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
import org.spantus.core.service.CorpusService;
import org.spantus.externals.recognition.services.RecognitionServiceFactory;
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
public class RecognitionMarkerSegmentatorListenerImpl extends MarkerSegmentatorListenerImpl implements IExtractorInputReaderAware {

    private static final Logger LOG = Logger.getLogger(RecognitionMarkerSegmentatorListenerImpl.class);
    private IExtractorConfig config;
    private CorpusService corpusService;
    private String repositoryPath = null;
    private IExtractorInputReader extractorInputReader;    
    private WorkExtractorReaderService extractorReaderService;

    protected boolean processEndedSegment(SignalSegment signalSegment) {

        Map<String, FrameVectorValuesHolder> vectorMap = signalSegment.getFeatureFrameVectorValuesMap();
//		FrameVectorValuesHolder signalWindows = vectorMap.get(MarkerSegmentatorListenerImpl.SIGNAL_WINDOWS);

        
        
        Map<String, IValues> fvv  = getExtractorReaderService().recalcualteValues(getExtractorInputReader(), 
                signalSegment.getMarker(), 
                ExtractorEnum.MFCC_EXTRACTOR.name());
        IValues mffcValues = fvv.get(ExtractorEnum.MFCC_EXTRACTOR.name());

        if(mffcValues==null){
            return false;
        }
        Assert.isTrue(mffcValues.size() > 0, "MFCC is not calculated. Size {0} ", mffcValues.size());

        signalSegment.getFeatureFrameVectorValuesMap().put(ExtractorEnum.MFCC_EXTRACTOR.name(), 
                new FrameVectorValuesHolder((FrameVectorValues)mffcValues));

        String name = match(signalSegment);


        signalSegment.setName(name);
        signalSegment.getMarker().setLabel(name);

        LOG.debug("[processEndedSegment] spotted: {0} in time [{1}:{2}] ", signalSegment.getName(), signalSegment.getMarker().getStart(), signalSegment.getMarker().getEnd());
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

        if (result == null) {
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
        if (corpusService == null) {
            Assert.isTrue(StringUtils.hasText(repositoryPath), "Repository path not set");
            corpusService = RecognitionServiceFactory.createCorpusServicePartialSearch(repositoryPath);
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

    public void setExtractorReaderService(WorkExtractorReaderService extractorReaderService) {
        this.extractorReaderService = extractorReaderService;
    }
    
    
}

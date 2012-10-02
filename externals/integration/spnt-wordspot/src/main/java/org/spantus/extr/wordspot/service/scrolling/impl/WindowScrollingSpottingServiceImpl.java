/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.spantus.extr.wordspot.service.scrolling.impl;

import java.net.URL;
import java.util.List;
import java.util.Map;
import org.spantus.core.FrameVectorValues;
import org.spantus.core.IValues;
import org.spantus.core.beans.FrameVectorValuesHolder;
import org.spantus.core.beans.RecognitionResult;
import org.spantus.core.beans.SignalSegment;
import org.spantus.core.extractor.IExtractorInputReader;
import org.spantus.core.marker.Marker;
import org.spantus.core.service.CorpusService;
import org.spantus.externals.recognition.services.RecognitionServiceFactory;
import org.spantus.extr.wordspot.domain.SegmentExtractorServiceConfig;
import org.spantus.extr.wordspot.dto.SpottingSyllableCtx;
import org.spantus.extr.wordspot.service.SpottingListener;
import org.spantus.extr.wordspot.service.impl.SpottingService;
import org.spantus.extractor.impl.ExtractorEnum;
import org.spantus.utils.Assert;
import org.spantus.work.services.WorkExtractorReaderService;
import org.spantus.work.services.WorkServiceFactory;

/**
 *
 * @author mondhs
 * @since 0.3
 */
public class WindowScrollingSpottingServiceImpl implements SpottingService {

    private WorkExtractorReaderService extractorReaderService;
    private CorpusService corpusService;
    private SignalSegment keySegment;
    /**
     * 
     * @param urlFile
     * @param wordSpottingListener 
     */
    @Override
    public void wordSpotting(URL urlFile, SpottingListener spottingListener) {
        SpottingSyllableCtx ctx = new SpottingSyllableCtx();
        ctx.setMinFirstMfccValue(Double.MAX_VALUE);
        IExtractorInputReader aReader = getExtractorReaderService().createReader(new ExtractorEnum[]{ExtractorEnum.SIGNAL_EXTRACTOR}, urlFile);
        long availableStartMs = aReader.getAvailableSignalLengthMs() - keySegment.getMarker().getLength();
        for (long start = 10; start < availableStartMs; start += 10) {
            Marker iMarker = new Marker();
            iMarker.setStart(start);
            iMarker.setLength(keySegment.getMarker().getLength());
            SignalSegment segment = recalculateFeatures(aReader, iMarker);
            if (segment == null) {
                continue;
            }
            List<RecognitionResult> result = match(segment);
            if (result.size() > 0) {
                RecognitionResult first = null;
                for (RecognitionResult recognitionResult : result) {
                    if (keySegment.getMarker().getLabel().equals(
                            recognitionResult.getInfo().getName())) {
                        first = recognitionResult;
                    }
                }
                if (first == null) {
                    continue;
                }
                String name = first.getInfo().getName();
                Double firstMfccValue = first.getDetails().getDistances().get(ExtractorEnum.MFCC_EXTRACTOR.name());
                ctx.getResultMap().put(iMarker.getStart(), result);
                ctx.getSyllableNameMap().put(iMarker.getStart(), name);
                ctx.getMinMfccMap().put(iMarker.getStart(), firstMfccValue);
                if(ctx.getMinFirstMfccValue().doubleValue()>firstMfccValue.doubleValue()){
                    ctx.setMinFirstMfccValue(firstMfccValue);
                    ctx.setMinFirstMfccStart(iMarker.getStart());
                }
            }
            result.size();
        }
        //        ctx.printDeltas();
        ctx.printMFCC();
        ctx.printSyllableFrequence();
        
        spottingListener.foundSegment(null, new SignalSegment(new Marker(
                ctx.getMinFirstMfccStart(), 
                keySegment.getMarker().getLength(),
                keySegment.getMarker().getLabel())), null);
    }
    /**
     * 
     * @param segment
     * @return 
     */
    protected List<RecognitionResult> match(SignalSegment segment) {
        List<RecognitionResult> result = getCorpusService().findMultipleMatchFull(segment);

        if (result == null) {
            throw new IllegalArgumentException("No recognition information in corpus is found");
        }

        return result;
    }
    /**
     * 
     * @param theExtractorReader
     * @param theMarker
     * @return 
     */
    protected SignalSegment recalculateFeatures(IExtractorInputReader theExtractorReader, Marker theMarker) {
        Map<String, IValues> fvv = getExtractorReaderService().recalcualteValues(theExtractorReader,
                theMarker,
                ExtractorEnum.MFCC_EXTRACTOR.name());
        if (fvv == null) {
            return null;
        }
        IValues mffcValues = fvv.get(ExtractorEnum.MFCC_EXTRACTOR.name());

        if (mffcValues == null) {
            return null;
        }
        Assert.isTrue(mffcValues.size() > 0, "MFCC is not calculated. Size {0} ", mffcValues.size());
        SignalSegment aSegment = new SignalSegment();
        aSegment.setMarker(theMarker);
        aSegment.getFeatureFrameVectorValuesMap().put(ExtractorEnum.MFCC_EXTRACTOR.name(),
                new FrameVectorValuesHolder((FrameVectorValues) mffcValues));
        return aSegment;
    }

    public void setExtractorReaderService(WorkExtractorReaderService extractorReaderService) {
        this.extractorReaderService = extractorReaderService;
    }

    public void setCorpusService(CorpusService corpusService) {
        this.corpusService = corpusService;
    }

    public WorkExtractorReaderService getExtractorReaderService() {
        return extractorReaderService;
    }

    public CorpusService getCorpusService() {
        return corpusService;
    }

    public SignalSegment getKeySegment() {
        return keySegment;
    }

    public void setKeySegment(SignalSegment keySegment) {
        this.keySegment = keySegment;
    }
    
    
}

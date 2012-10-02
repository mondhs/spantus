/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.spantus.extr.wordspot.service.scrolling.impl.test;

import java.util.List;
import java.util.Map;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
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
import org.spantus.extr.wordspot.service.impl.test.AbstractSegmentExtractorTest;
import org.spantus.extractor.impl.ExtractorEnum;
import org.spantus.utils.Assert;
import org.spantus.utils.StringUtils;
import org.spantus.work.services.WorkExtractorReaderService;
import org.spantus.work.services.WorkServiceFactory;

/**
 *
 * @author as
 */
public class WindowScrollingSpottingTest extends AbstractSegmentExtractorTest {

    private WorkExtractorReaderService extractorReaderService;
    private CorpusService corpusService;

    @Test
    public void testWordSpotting() {
        SpottingSyllableCtx ctx = new SpottingSyllableCtx();

        IExtractorInputReader aReader = getExtractorReaderService().createReader(new ExtractorEnum[]{ExtractorEnum.ENERGY_EXTRACTOR}, getWavFile());
        for (long start = 0; start < aReader.getAvailableSignalLengthMs(); start += 10) {
            Marker iMarker = new Marker();
            iMarker.setStart(start);
            iMarker.setLength(200L);
            SignalSegment segment = recalculateFeatures(aReader, iMarker);
            if(segment == null){
                continue;
            }
            List<RecognitionResult> result = match(segment);
            if(result.size()>0){
                RecognitionResult first = null;
                for (RecognitionResult recognitionResult : result) {
                    if("skirt".equals(
                        recognitionResult.getInfo().getName())){
                        first = recognitionResult;
                    }
                }
                if(first == null){
                    continue;
                }
                String name = first.getInfo().getName();
                Double firstMfccValue = first.getDetails().getDistances().get(ExtractorEnum.MFCC_EXTRACTOR.name());
                ctx.getResultMap().put(iMarker.getStart(), result);
                ctx.getSyllableNameMap().put(iMarker.getStart(), name);
                ctx.getMinMfccMap().put(iMarker.getStart(), firstMfccValue);
            }
            result.size();
        }
        //        ctx.printDeltas();
        ctx.printMFCC();
        ctx.printSyllableFrequence();
        assertTrue(true);
    }

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

    protected List<RecognitionResult> match(SignalSegment segment) {
        List<RecognitionResult> result = getCorpusService().findMultipleMatchFull(segment);

        if (result == null) {
            throw new IllegalArgumentException("No recognition information in corpus is found");
        }

        return result;
    }

    public WorkExtractorReaderService getExtractorReaderService() {
        if (extractorReaderService == null) {
            SegmentExtractorServiceConfig config = getSegmentExtractorService().getServiceConfig();
            extractorReaderService = WorkServiceFactory.createExtractorReaderService(
                    config.getWindowLength(),
                    config.getOverlapInPerc());
        }
        return extractorReaderService;
    }

    public CorpusService getCorpusService() {
        if (corpusService == null) {
            Assert.isTrue(getRepositoryPath().isDirectory(), "Repository path not set");
            corpusService = RecognitionServiceFactory.createCorpusServicePartialSearch(
                    getRepositoryPath().getAbsolutePath(), 
                    getSegmentExtractorService().getServiceConfig().getSyllableDtwRadius(),
                    ExtractorEnum.MFCC_EXTRACTOR.name());
        }
        return corpusService;
    }
}

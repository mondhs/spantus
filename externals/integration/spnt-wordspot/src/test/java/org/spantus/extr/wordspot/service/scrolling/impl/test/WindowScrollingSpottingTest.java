/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.spantus.extr.wordspot.service.scrolling.impl.test;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Ignore;
import org.spantus.core.beans.RecognitionResult;
import org.spantus.core.beans.SignalSegment;
import org.spantus.core.extractor.IExtractorInputReader;
import org.spantus.core.marker.Marker;
import org.spantus.core.marker.MarkerSetHolder;
import org.spantus.core.service.CorpusService;
import org.spantus.externals.recognition.services.RecognitionServiceFactory;
import org.spantus.extr.wordspot.domain.SegmentExtractorServiceConfig;
import org.spantus.extr.wordspot.service.SpottingListener;
import org.spantus.extr.wordspot.service.impl.test.AbstractSegmentExtractorTest;
import org.spantus.extr.wordspot.service.scrolling.impl.WindowScrollingSpottingServiceImpl;
import org.spantus.extractor.impl.ExtractorEnum;
import org.spantus.work.services.WorkExtractorReaderService;
import org.spantus.work.services.WorkServiceFactory;

/**
 *
 * @author as
 */
public class WindowScrollingSpottingTest extends AbstractSegmentExtractorTest {

    private WindowScrollingSpottingServiceImpl spottingService;

    @Override
    public void setUp() throws Exception {
//        setRepositoryPath(new File(getRepositoryPathRoot(),"CORPUS/word"));
        super.setUp();
        SegmentExtractorServiceConfig config = getSegmentExtractorService().getServiceConfig();
        WorkExtractorReaderService extractorReaderService = WorkServiceFactory.createExtractorReaderService(
                config.getWindowLength(),
                config.getOverlapInPerc());
        CorpusService corpusService = RecognitionServiceFactory.createCorpusServicePartialSearch(
                getRepositoryPath().getAbsolutePath(),
                getSegmentExtractorService().getServiceConfig().getSyllableDtwRadius(),
                ExtractorEnum.MFCC_EXTRACTOR.name());

        spottingService = new WindowScrollingSpottingServiceImpl();
        spottingService.setCorpusService(corpusService);
        spottingService.setExtractorReaderService(extractorReaderService);
        MarkerSetHolder markers = findMarkerSetHolderByWav(getWavFile());
        Marker marker = findByLabel(markers);
        SignalSegment keySegment = new SignalSegment(new Marker(marker.getStart(), marker.getLength(),
                marker.getLabel()));
        spottingService.setKeySegment(keySegment);
    }


    @Test
    public void testWordSpotting() throws MalformedURLException {
        //given
        URL aWavUrl = getWavFile().toURI().toURL();
        final SignalSegment foundSegment =new SignalSegment();
        //when
        spottingService.wordSpotting(aWavUrl, new SpottingListener() {
            @Override
            public String foundSegment(String sourceId, SignalSegment newSegment, List<RecognitionResult> recognitionResults) {
                foundSegment.setMarker(newSegment.getMarker());
                return newSegment.getMarker().getLabel();
            }
        });
        //then
        assertNotNull(foundSegment.getMarker());
        assertEquals("start of found key marker same as matched", spottingService.getKeySegment().getMarker().getStart(),
                foundSegment.getMarker().getStart(), 10L);
    }

    @Test
    public void testExactPlaceWordSpotting() throws MalformedURLException {
        //given
        URL aWavUrl = getWavFile().toURI().toURL();
        Marker aMarker = new Marker(922L, 201L, "skirt");
        
        //when
        IExtractorInputReader reader = spottingService.createReader(aWavUrl);
        SignalSegment recalculatedFeatures = spottingService.recalculateFeatures(reader, aMarker);
        List<RecognitionResult> matchedResults = spottingService.match(recalculatedFeatures);
        
        //then
        
        assertNotNull(matchedResults);
        assertEquals("Results", 5, matchedResults.size());
        RecognitionResult matched = matchedResults.get(0);
        assertEquals("Results", 0, matched.getDetails().getDistances().get(ExtractorEnum.MFCC_EXTRACTOR.name()), 1);
        
    }

    protected Marker findByLabel(MarkerSetHolder markers) {
        return getMarkerService().findByLabel("skirt", markers);
    }
    
    protected Marker findKeyword(File aWavFile, String keyWord) {
        MarkerSetHolder markers = findMarkerSetHolderByWav(aWavFile);
        Marker lietMarker = getMarkerService().findByLabel(keyWord, markers);
        return lietMarker;
    }
    

    public WindowScrollingSpottingServiceImpl getSpottingService() {
        return spottingService;
    }
    
    

}

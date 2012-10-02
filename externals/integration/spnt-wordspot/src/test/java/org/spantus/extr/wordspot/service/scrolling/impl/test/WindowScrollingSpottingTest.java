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
import org.spantus.core.beans.RecognitionResult;
import org.spantus.core.beans.SignalSegment;
import org.spantus.core.marker.Marker;
import org.spantus.core.marker.MarkerSetHolder;
import org.spantus.core.service.CorpusService;
import org.spantus.externals.recognition.services.RecognitionServiceFactory;
import org.spantus.extr.wordspot.domain.SegmentExtractorServiceConfig;
import org.spantus.extr.wordspot.dto.SpottingSyllableCtx;
import org.spantus.extr.wordspot.service.SpottingListener;
import org.spantus.extr.wordspot.service.impl.SpottingListenerLogger;
import org.spantus.extr.wordspot.service.impl.test.AbstractSegmentExtractorTest;
import org.spantus.extr.wordspot.service.scrolling.impl.WindowScrollingSpottingServiceImpl;
import org.spantus.extractor.impl.ExtractorEnum;
import org.spantus.utils.Assert;
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
        setRepositoryPath(new File(getRepositoryPathRoot(),"CORPUS/word"));
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
        Marker marker = getMarkerService().findByLabel("skirti", markers);
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
                foundSegment.getMarker().getStart(), 60);
    }
}

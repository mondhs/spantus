/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.spantus.extr.wordspot.service.scrolling.impl.test;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import org.junit.Ignore;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.experimental.categories.Category;
import org.spantus.core.beans.RecognitionResult;
import org.spantus.core.beans.SignalSegment;
import org.spantus.core.extractor.IExtractorInputReader;
import org.spantus.core.junit.SlowTests;
import org.spantus.core.marker.Marker;
import org.spantus.core.marker.MarkerSetHolder;
import org.spantus.extr.wordspot.service.SpottingListener;
import org.spantus.extractor.impl.ExtractorEnum;

/**
 *
 * @author as
 */
public class WindowScrollingSpottingExp extends WindowScrollingSpottingTest {

    @Override
    protected void setUpPath() throws Exception {
        super.setUpPath();
        setRepositoryPathRoot(new File("/home/as/tmp/garsynas.lietuvos-syn-wopitch"));
        setWavFile(new File(getRepositoryPathRoot(), "TRAIN/lietuvos_mbr_test-30_1.wav"));
        setRepositoryPath(new File(getRepositoryPathRoot(), "CORPUS/phone"));
    }

    @Override
    protected Marker findByLabel(MarkerSetHolder markers) {
        Marker rtn = findKeyword(getWavFile(), "-l-ie-t");
        //new Marker(4137L, 363L, "liet");
        rtn.setLabel("liet");
        return rtn;
        
    }

    @Test
    @Override
    public void testWordSpotting() throws MalformedURLException {
        //given
        URL aWavUrl = getWavFile().toURI().toURL();
        final SignalSegment foundSegment = new SignalSegment();
        //when
        getSpottingService().wordSpotting(aWavUrl, new SpottingListener() {
            @Override
            public String foundSegment(String sourceId, SignalSegment newSegment, List<RecognitionResult> recognitionResults) {
                foundSegment.setMarker(newSegment.getMarker());
                return newSegment.getMarker().getLabel();
            }
        });
        //then
        assertNotNull(foundSegment.getMarker());
        assertEquals("start of found key marker same as matched", getSpottingService().getKeySegment().getMarker().getStart(),
                foundSegment.getMarker().getStart(), 100L);
    }

    @Test
    @Override
    public void testExactPlaceWordSpotting() throws MalformedURLException {
        //given
        URL aWavUrl = getWavFile().toURI().toURL();
        Marker aMarker = findByLabel(null);
        getSpottingService().setDelta(1);

        //when
        IExtractorInputReader reader = getSpottingService().createReader(aWavUrl);
        SignalSegment recalculatedFeatures = getSpottingService().recalculateFeatures(reader, aMarker);
        List<RecognitionResult> matchedResults = getSpottingService().match(recalculatedFeatures);

        //then

        assertNotNull(matchedResults);
        assertEquals("Results", 2, matchedResults.size());
        RecognitionResult matched = matchedResults.get(0);
        assertEquals("Results", 5E9, matched.getDetails().getDistances().get(ExtractorEnum.MFCC_EXTRACTOR.name()), 1E9);
    }
}

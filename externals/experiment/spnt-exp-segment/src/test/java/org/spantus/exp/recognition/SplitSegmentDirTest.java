/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.spantus.exp.recognition;

import java.io.File;

import org.junit.Test;
import org.spantus.core.marker.MarkerSet;
import org.spantus.core.marker.MarkerSetHolder;
import org.spantus.logger.Logger;
import org.spantus.utils.FileUtils;
import org.spantus.work.services.WorkServiceFactory;

/**
 *
 * @author mondhs
 */
public class SplitSegmentDirTest extends AbstractSegmentDirTest {

    private static final Logger log = Logger.getLogger(SplitSegmentDirTest.class);

    @Test
    public void testExtract() {
        clearCorpus();

        File wavDir = new File(DIR_LEARN_WAV, "WAV/AK1/");
        File markerDir = new File(DIR_LEARN_WAV, "GRID/AK1/");
        
        int sum = 0;
        for (File filePath : wavDir.listFiles(new WavFileNameFilter())) {
            log.debug("reading", filePath);
            String markersPath = FileUtils.stripExtention(filePath);
            markersPath += ".TextGrid";
            MarkerSetHolder markerSetHolder = WorkServiceFactory.createMarkerDao().read(
                    new File(markerDir, markersPath));
            
            MarkerSet markerSet = getSegementedMarkers(markerSetHolder);
            int count = markerSet.getMarkers().size();

            getExtractor().extractAndLearn(
                    filePath.getAbsoluteFile(), markerSetHolder, null);
            
            log.debug("accept: {0}:{1}", filePath, markerSetHolder);
            sum += count;
        }
//        Assert.assertEquals(70, sum);
//        verifyMatches();


    }

   
}

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
 * read labeled info and learn samples
 * @author mondhs
 */
public class TexGridSplitSegmentAndLearnDirTest extends AbstractSegmentDirTest {

    private static final Logger log = Logger.getLogger(TexGridSplitSegmentAndLearnDirTest.class);

    @Test
    public void testExtract() {
        clearCorpus();

        File wavDir = new File(DIR_LEARN_WAV, "WAV/AK1/");
        File markerDir = new File(DIR_LEARN_WAV, "GRID/TRAIN/");
        
        int sum = 0;
        for (File filePath : wavDir.listFiles(new WavFileNameFilter())) {
            log.debug("reading", filePath);
            String markersPath = FileUtils.replaceExtention(filePath, ".TextGrid");
            File textGridFile = new File(markerDir, markersPath); 
            if(!textGridFile.exists()){
            	continue;
            }
            MarkerSetHolder markerSetHolder = WorkServiceFactory.createMarkerDao().read(
            		textGridFile);
            
            MarkerSet markerSet = findSegementedMarkers(markerSetHolder);
            int count = markerSet.getMarkers().size();

            getExtractor().extractAndLearn(
                    filePath.getAbsoluteFile(), markerSet, null);
            
            log.debug("accept: {0}:{1}", filePath, markerSet);
            sum += count;
        }
//        Assert.assertEquals(70, sum);
//        verifyMatches();


    }

   
}

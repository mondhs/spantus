/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.spantus.exp.recognition;

import java.io.File;

import org.junit.Before;
import org.junit.Test;
import org.spantus.core.marker.MarkerSet;
import org.spantus.core.marker.MarkerSetHolder;
import org.spantus.exp.recognition.filefilter.TextGridNameFilter;
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
        
        int sum = 0;
        for (File markerFile : getExpConfig().getMarkerDir().listFiles(new TextGridNameFilter())) {
            log.debug("reading: {0}", markerFile);
            String markersPath = FileUtils.replaceExtention(markerFile, ".wav");
            File wavFile = new File(getExpConfig().getWavDir(), markersPath); 
            if(!wavFile.exists()){
            	continue;
            }
            MarkerSetHolder markerSetHolder = WorkServiceFactory.createMarkerDao().read(
            		markerFile);
            
            MarkerSet markerSet = findSegementedMarkers(markerSetHolder);
            int count = markerSet.getMarkers().size();

            getExtractor().extractAndLearn(
            		wavFile.getAbsoluteFile(), markerSet, null);
            
            log.debug("accept: {0}:{1}", wavFile, markerSet);
            sum += count;
        }
//        Assert.assertEquals(70, sum);
//        verifyMatches();


    }

   
}

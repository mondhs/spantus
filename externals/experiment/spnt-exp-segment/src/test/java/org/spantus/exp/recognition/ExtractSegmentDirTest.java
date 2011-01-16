/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.spantus.exp.recognition;

import java.io.File;
import junit.framework.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.spantus.core.marker.MarkerSetHolder;
import org.spantus.logger.Logger;
import org.spantus.utils.FileUtils;
import org.spantus.work.services.WorkServiceFactory;

/**
 *
 * @author mondhs
 */
public class ExtractSegmentDirTest extends AbstractSegmentDirTest{
    private static final Logger log = Logger.getLogger(AbstractSegmentDirTest.class);
   

    @Test@Ignore
    public void testExtract() {
        clearCorpus();
                 
        int sum = 0; 
        for (File filePath : getLearnDir().listFiles(new WavFileNameFilter())) {
           MarkerSetHolder markerSetHolder = getExtractor().extractAndLearn(filePath.getAbsoluteFile());
           int count = getSegementedMarkers(markerSetHolder).getMarkers().size();
           String markersPath = FileUtils.stripExtention(filePath);
           markersPath += ".mspnt.xml";
           WorkServiceFactory.createMarkerDao().write(markerSetHolder, new File(DIR_LEARN, markersPath));
           log.debug("accept: {0}:{1}",filePath, markerSetHolder);
           sum += count;
        }
        Assert.assertEquals(70, sum);
//        verifyMatches();

       
    }
    

    
}

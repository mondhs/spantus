/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.spantus.exp.sylcount;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import junit.framework.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.spantus.core.marker.Marker;
import org.spantus.core.marker.MarkerSetHolder;
import org.spantus.exp.sylcount.filefilter.WavFileNameFilter;
import org.spantus.logger.Logger;
import org.spantus.utils.FileUtils;
import org.spantus.work.services.WorkServiceFactory;

/**
 *
 * @author mondhs
 */
public class ExtractSegmentDirTest extends AbstractSegmentDirTest{
    private static final Logger log = Logger.getLogger(ExtractSegmentDirTest.class);
   

    @Test@Ignore
    public void testExtract() throws IOException {
//        clearCorpus();
                 
        int sum = 0;

        Path rootPath = Paths.get("/home/mondhs/src/tmp/sylcount_exp/files.txt");
        FileWriter fw = new FileWriter("/home/mondhs/src/tmp/sylcount_exp/spantus_result.txt", false);
        BufferedWriter out = new BufferedWriter(fw);
        List<String> files = Files.readAllLines(rootPath);


        int total = files.size();
        int index = 0;
        for (String filePath : files) {
            File wavFile = new File(rootPath.getParent().toFile(), filePath);
            int count = -1;
            try {
                MarkerSetHolder markerSetHolder = getExtractor().extract(wavFile.getAbsoluteFile());
                List<Marker> markers = findSegementedMarkers(markerSetHolder).getMarkers();
                count = markers.size();
            }catch (IllegalArgumentException e){
                log.error("Processed file: {0}", wavFile);
                log.error("Something bad happened", e);
            }
//           String markersPath = FileUtils.replaceExtention(filePath,".mspnt.xml");
//           WorkServiceFactory.createMarkerDao().write(markerSetHolder, new File(getExpConfig().getDirLearn(), markersPath));
            log.error("accept: {0}: {1}, {2}/{3}", filePath, count, ++index, total );
            out.write(wavFile.getName() + "," + count);
            out.newLine();
            sum += count;
//                break;
//            if(index>200){
//                break;
//            }
        }
        out.close();
        //Assert.assertEquals(70, sum);
//        verifyMatches();

       
    }
    

    
}

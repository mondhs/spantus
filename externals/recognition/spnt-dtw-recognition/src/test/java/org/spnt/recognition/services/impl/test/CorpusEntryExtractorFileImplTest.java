/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.spnt.recognition.services.impl.test;

import java.io.File;
import java.net.URL;
import java.util.List;
import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;
import org.spantus.externals.recognition.bean.CorpusEntry;
import org.spantus.externals.recognition.services.impl.CorpusEntryExtractorFileImpl;

/**
 *
 * @author mondhs
 */
public class CorpusEntryExtractorFileImplTest {

    public final static String DIR_TEST = "../../../data";
    public final static String FILE_TEST = "t_1_2.wav";
    
    private File path;
    CorpusEntryExtractorFileImpl extractor;
    
    @Before
    public void onSetup(){
        path = new File(DIR_TEST);
        extractor = new CorpusEntryExtractorFileImpl();
    }
    
    
    @Test
    public void testSegmentExtractInMemory(){
       //given
       File filePath = new File(path, FILE_TEST);
      
       List<CorpusEntry> entries = extractor.extractInMemory(filePath);
       //then
       Assert.assertEquals("entries: " , 2, entries.size());
    }
    
     @Test
    public void testSegmentExtractAndSave(){
         //given
       File filePath = new File(path, FILE_TEST);
        //when
       int entries = extractor.extractAndSave(filePath);
       //then
       Assert.assertEquals("entries: " , 2, entries);
     }
    
}

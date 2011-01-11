/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.spnt.recognition.services.impl.test;

import java.io.File;
import java.util.List;
import junit.framework.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.spantus.core.marker.MarkerSet;
import org.spantus.core.marker.MarkerSetHolder;
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
    
    
    @Test @Ignore
    public void testSegmentExtractInMemory(){
       //given
       File filePath = new File(path, FILE_TEST);
      
       List<CorpusEntry> entries = extractor.extractInMemory(filePath);
       //then
       Assert.assertEquals("entries: " , 2, entries.size());
    }
    
     @Test @Ignore
    public void testSegmentExtractAndSave(){
         //given
       File filePath = new File(path, FILE_TEST);
        //when
       MarkerSetHolder entries = extractor.extractAndLearn(filePath);
       //then
       Assert.assertNotNull(entries);
       MarkerSet words = entries.getMarkerSets().get(MarkerSetHolder.MarkerSetHolderEnum.word);
       Assert.assertNotNull(words);
       Assert.assertEquals("entries: " , 2, words.getMarkers().size());
     }
    
}

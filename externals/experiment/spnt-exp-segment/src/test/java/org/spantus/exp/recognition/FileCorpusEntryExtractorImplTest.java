/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.spantus.exp.recognition;

import java.io.File;
import java.util.List;
import junit.framework.Assert;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.spantus.externals.recognition.bean.CorpusEntry;

/**
 *
 * @author mondhs
 */
public class FileCorpusEntryExtractorImplTest {

    public final static String DIR_AD400 = "/home/mondhs/src/garsynai/alpha_digits/speech/400";
    public final static String DIR_TEST = "/home/mondhs/src/spnt-code/data";
    public final static String FILE_TEST = "t_1_2.wav";
    
    private File path;
    FileCorpusEntryExtractorImpl extractor;
    
    @Before
    public void onSetup(){
        path = new File(DIR_TEST);
        extractor = new FileCorpusEntryExtractorImpl();
    }
    
    
    @Test
    public void testSegmentExtractInMemory(){
       //given
       File filePath = new File(path, FILE_TEST);
       List<CorpusEntry> entries = extractor.extractInMemory(filePath);
       Assert.assertEquals("entries: " , 2, entries.size());
    }
    
}

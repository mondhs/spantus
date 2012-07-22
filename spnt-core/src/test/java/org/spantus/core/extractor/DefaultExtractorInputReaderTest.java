/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.spantus.core.extractor;

import org.junit.AfterClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.BeforeClass;
import org.spantus.core.FrameValues;

/**
 *
 * @author mondhs
 */
public class DefaultExtractorInputReaderTest {
    
    private DefaultExtractorInputReader defaultExtractorInputReader;
    
    @Before
    public void onSetup(){
        defaultExtractorInputReader = new DefaultExtractorInputReader();
        DefaultExtractorConfig config = new DefaultExtractorConfig();
        config.setSampleRate(10.0);
        defaultExtractorInputReader.setConfig(config);
    }

   
    

    @Test
    public void testSomeMethod() {
        //given
        for (long i = 0; i < 12; i++) {
            defaultExtractorInputReader.put(i, Double.valueOf(i));
        }
        
        //when
        FrameValues frameValue = defaultExtractorInputReader.findSignalValues(200L, 1100L);
        //then
        assertEquals("values extracted", 10, frameValue.size(), 0);
        assertEquals("first value", 1, frameValue.getFirst(), 0);
        assertEquals("values extracted", 10, frameValue.getLast(), 0);
    }
}

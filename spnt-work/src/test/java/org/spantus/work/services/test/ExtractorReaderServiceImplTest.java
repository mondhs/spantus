/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.spantus.work.services.test;

import org.junit.Before;
import org.junit.Test;
import org.junit.Assert;
import org.spantus.core.FrameVectorValues;
import org.spantus.core.extractor.IExtractorInputReader;
import org.spantus.core.extractor.IExtractorVector;
import org.spantus.core.marker.Marker;
import org.spantus.work.services.ExtractorReaderServiceImpl;

/**
 *
 * @author mondhs
 */

public class ExtractorReaderServiceImplTest {
    private ExtractorReaderServiceImpl extractorReaderService;
    
    @Before
    public void onSetup(){
        extractorReaderService = new ExtractorReaderServiceImpl();
    }
    @Test
    public void testExtractValuesForMarker(){
        //given
        IExtractorInputReader reader = new DumyExtractorInputReader();
        Marker marker = new Marker();
        marker.setStart(2L);
        marker.setEnd(5L);

        String extractorName = "testFeature";

        DummyExtractorVector extractor = new DummyExtractorVector();
        extractor.setName("BUFFERED_" +extractorName);
        FrameVectorValues fullFVV = generateOutputValues(10);
        
        extractor.setOutputValues(fullFVV);
        reader.getExtractorRegister3D().add(extractor);

        //When
        FrameVectorValues fvv = extractorReaderService.findFeatureVectorValuesForMarker(
                reader, marker, extractorName);
        
        //then
        Assert.assertNotNull(fvv);
        Assert.assertEquals(3, fvv.size());
    }

    protected FrameVectorValues generateOutputValues(int index){
        FrameVectorValues fullFVV = new FrameVectorValues();
        fullFVV.setSampleRate(1000);

        for (int i = 0; i < index; i++) {
            float f = i+.1F;
            fullFVV.add(new Float[]{f, f});
        }
        return fullFVV;
    }
}

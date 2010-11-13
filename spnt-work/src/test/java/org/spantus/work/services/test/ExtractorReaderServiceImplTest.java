/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.spantus.work.services.test;

import java.util.Map;
import org.junit.Before;
import org.junit.Test;
import org.junit.Assert;
import org.junit.Ignore;
import org.spantus.core.FrameVectorValues;
import org.spantus.core.IValues;
import org.spantus.core.extractor.IExtractorInputReader;
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
        reader.getExtractorRegister3D().add(createExtractor(extractorName+"1"));
        reader.getExtractorRegister3D().add(createExtractor(extractorName));
        reader.getExtractorRegister3D().add(createExtractor(extractorName+"2"));

        //When
        Map<String, IValues> fvv = extractorReaderService.findAllVectorValuesForMarker(
                reader, marker);
        
        //then
        Assert.assertNotNull(fvv);
        Assert.assertEquals(3, fvv.size());
        Assert.assertEquals("", 0.003F, fvv.get(extractorName).getTime(),0);
    }
    @Test 
    public void testFindAllVectorValuesForMarker(){
         //given
        IExtractorInputReader reader = new DumyExtractorInputReader();
        Marker marker = new Marker();
        marker.setStart(2L);
        marker.setEnd(5L);

        String extractorName = "testFeature";
        reader.getExtractorRegister3D().add(createExtractor(extractorName+"1"));
        reader.getExtractorRegister3D().add(createExtractor(extractorName));
        reader.getExtractorRegister3D().add(createExtractor(extractorName+"2"));

        //When
        FrameVectorValues fvv = extractorReaderService.findFeatureVectorValuesForMarker(
                reader, marker, extractorName);

        //then
        Assert.assertNotNull(fvv);
        Assert.assertEquals(3, fvv.size());
        Assert.assertEquals("", 0.003F, fvv.getTime(),0);
    }


    protected FrameVectorValues generateOutputValues(int index){
        FrameVectorValues fullFVV = new FrameVectorValues();
        fullFVV.setSampleRate(1000);

        for (int i = 0; i < index; i++) {
            float f = i+.1F;
            fullFVV.add(new Float[]{f, f, f});
        }
        return fullFVV;
    }

    private DummyExtractorVector createExtractor(String extractorName) {
        DummyExtractorVector extractor = new DummyExtractorVector();
        extractor.setName("BUFFERED_" +extractorName);
        FrameVectorValues fullFVV = generateOutputValues(10);
        extractor.setOutputValues(fullFVV);
        return extractor;
    }
}

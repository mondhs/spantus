/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.spantus.work.services.test;

import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.spantus.core.FrameVectorValues;
import org.spantus.core.IValues;
import org.spantus.core.extractor.IExtractorInputReader;
import org.spantus.core.marker.Marker;
import org.spantus.core.test.DumyExtractorInputReader;
import org.spantus.work.services.impl.WorkExtractorReaderServiceImpl;

/**
 *
 * @author mondhs
 */

public class ExtractorReaderServiceImplTest {
    private WorkExtractorReaderServiceImpl extractorReaderService;
    
    @Before
    public void onSetup(){
        extractorReaderService = new WorkExtractorReaderServiceImpl();
    }
    @Test
    public void testExtractValuesForMarker(){
        //given
        IExtractorInputReader reader = new DumyExtractorInputReader();
        Marker marker = new Marker();
        marker.setStart(2L);
        marker.setEnd(5L);

        String extractorName = "testFeature";
        reader.getExtractorRegister3D().add(DumyExtractorInputReader.createExtractorVector(extractorName+"1"));
        reader.getExtractorRegister3D().add(DumyExtractorInputReader.createExtractorVector(extractorName));
        reader.getExtractorRegister3D().add(DumyExtractorInputReader.createExtractorVector(extractorName+"2"));

        //When
        Map<String, IValues> fvv = extractorReaderService.findAllVectorValuesForMarker(
                reader, marker);
        
        //then
        Assert.assertNotNull(fvv);
        Assert.assertEquals(3, fvv.size());
        Assert.assertEquals("", 3, fvv.get(extractorName).getTime(),0);
    }
    @Test 
    public void testFindAllVectorValuesForMarker(){
         //given
        IExtractorInputReader reader = new DumyExtractorInputReader();
        Marker marker = new Marker();
        marker.setStart(2L);
        marker.setEnd(5L);

        String extractorName = "testFeature";
        reader.getExtractorRegister3D().add(DumyExtractorInputReader.createExtractorVector(extractorName+"1"));
        reader.getExtractorRegister3D().add(DumyExtractorInputReader.createExtractorVector(extractorName));
        reader.getExtractorRegister3D().add(DumyExtractorInputReader.createExtractorVector(extractorName+"2"));

        //When
        FrameVectorValues fvv = extractorReaderService.findFeatureVectorValuesForMarker(
                reader, marker, extractorName);

        //then
        Assert.assertNotNull(fvv);
        Assert.assertEquals(3, fvv.size());
        Assert.assertEquals("", 3, fvv.getTime(),0);
    }



}

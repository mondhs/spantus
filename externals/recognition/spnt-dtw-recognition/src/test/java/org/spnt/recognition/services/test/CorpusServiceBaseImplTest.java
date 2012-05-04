/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.spnt.recognition.services.test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sound.sampled.AudioInputStream;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.spantus.core.FrameValues;
import org.spantus.core.FrameVectorValues;
import org.spantus.core.IValues;
import org.spantus.core.beans.FrameVectorValuesHolder;
import org.spantus.core.beans.RecognitionResult;
import org.spantus.core.beans.SignalSegment;
import org.spantus.core.marker.Marker;
import org.spantus.core.service.CorpusRepository;
import org.spantus.externals.recognition.services.CorpusServiceBaseImpl;

/**
 *
 * @author mondhs
 */
public class CorpusServiceBaseImplTest {

    public static final String DU = "du";
    public static final String TRYS = "trys";
    public static final String VIENAS = "vienas";
    private CorpusServiceBaseImpl corpusServiceBaseImpl;
    private CorpusRepository corpusRepository;
    public final static String Feature1 = "Feature1";
    public final static String Feature2 = "Feature2";
    public final static String Feature3 = "Feature3";
    private Collection<SignalSegment> corpusEntries;

    @Before
    public void onSetup() {
        corpusServiceBaseImpl = new CorpusServiceBaseImpl();
        corpusRepository = Mockito.mock(CorpusRepository.class);
        corpusServiceBaseImpl.setCorpus(corpusRepository);
        corpusEntries = new ArrayList<SignalSegment>();

        SignalSegment corpusEntry = new SignalSegment();
        corpusEntry.setMarker(new Marker());
        corpusEntry.getMarker().setLabel(VIENAS);
        corpusEntry.getMarker().setStart(0L);
        corpusEntry.getMarker().setLength(50L);
        corpusEntry.setId("1");
        corpusEntry.setName(VIENAS);
        addFeatureData(corpusEntry.getFeatureFrameVectorValuesMap(), Feature1, 0D, 1D, 1D, 1D, 1D);
        addFeatureData(corpusEntry.getFeatureFrameVectorValuesMap(), Feature2, 0D, 2D, 2D, 2D, 2D);
        corpusEntries.add(corpusEntry);


        corpusEntry = new SignalSegment();
        corpusEntry.setId("2");
        corpusEntry.setName(DU);
        corpusEntry.setMarker(new Marker());
        corpusEntry.getMarker().setLabel(DU);
        corpusEntry.getMarker().setStart(0L);
        corpusEntry.getMarker().setLength(50L);
        addFeatureData(corpusEntry.getFeatureFrameVectorValuesMap(), Feature1, 0D, 1D, 1D, 1D, 1D);
        addFeatureData(corpusEntry.getFeatureFrameVectorValuesMap(), Feature2, 0D, 4D, 5D, 6D, 7D);
        corpusEntries.add(corpusEntry);

        corpusEntry = new SignalSegment();
        corpusEntry.setId("3");
        corpusEntry.setName(TRYS);
        corpusEntry.setMarker(new Marker());
        corpusEntry.getMarker().setLabel(TRYS);
        corpusEntry.getMarker().setStart(0L);
        corpusEntry.getMarker().setLength(50L);
        addFeatureData(corpusEntry.getFeatureFrameVectorValuesMap(), Feature1, 1D, 2D, 3D, 4D);
        addFeatureData(corpusEntry.getFeatureFrameVectorValuesMap(), Feature2, 4D, 5D, 9D, 7D, 8D);
        corpusEntries.add(corpusEntry);
    }

     @Test
    public void testLearn() {
         //given
    	 SignalSegment savedResult = new SignalSegment();
         savedResult.setId("1");
         Mockito.when(corpusRepository.save((SignalSegment) Mockito.any())).
                 thenReturn(savedResult);
         AudioInputStream ais = Mockito.mock(AudioInputStream.class);
         Mockito.when(corpusRepository.update(
                  Mockito.any(SignalSegment.class),
                 Mockito.any(AudioInputStream.class) ))
                 .thenReturn(savedResult);
        //when
        Map<String, IValues> features = new HashMap<String, IValues>();
        features.put(Feature1, createFrameValues(1D, 2D, 3D, 4D, 5D));
        features.put(Feature2, createFrameValues(3D, 4D, 5D, 6D));
        SignalSegment result = corpusServiceBaseImpl.create(VIENAS,features);
        result = corpusServiceBaseImpl.learn(result, ais);
        
        //then
        Assert.assertNotNull("Result not saved", result);
        Assert.assertNotNull("Result not saved", result.getId());

        Assert.assertEquals(savedResult.getId(), result.getId());
    }
    
    @Test
    public void testMatch() {
         //given
        Mockito.when(corpusRepository.findAllEntries()).thenReturn(
                corpusEntries);
        //when
        Map<String, IValues> target = new HashMap<String, IValues>();
        target.put(Feature1, createFrameValues(1D, 2D, 3D, 4D));
        target.put(Feature2, createFrameValues(4D, 5D, 6D, 7D, 8D));
        RecognitionResult result = corpusServiceBaseImpl.match(target);
         //then
        Assert.assertEquals("3", result.getInfo().getId());

    }

    @Test
    public void testFindMultipleMatch() {
        //given
        Mockito.when(corpusRepository.findAllEntries()).thenReturn(corpusEntries);
        Mockito.when(corpusRepository.findAudioFileById("1")).thenReturn("1.wav");
        Mockito.when(corpusRepository.findAudioFileById("2")).thenReturn("2.wav");
        Mockito.when(corpusRepository.findAudioFileById("3")).thenReturn("3.wav");

        //when
        Map<String, IValues> target = new HashMap<String, IValues>();
        target.put(Feature1, createFrameValues(1D, 2D, 3D, 4D));
        target.put(Feature2, createFrameValues(4D, 5D, 6D, 7D));
        List<RecognitionResult> results = corpusServiceBaseImpl.findMultipleMatchFull(target);

        //then
        Assert.assertEquals("All 3 entries ", 3, results.size());
        RecognitionResult first = results.get(0);
        RecognitionResult second = results.get(1);
        Assert.assertEquals("match", "3", first.getInfo().getId());
        Assert.assertEquals("Audio file is set", first.getDetails().getAudioFilePath(), "3.wav");
        Assert.assertEquals("first sample length", 40, first.getDetails().getSampleLegths().get(Feature1),0);
        Assert.assertEquals("first target length", 40, first.getDetails().getTargetLegths().get(Feature1),0);
        Assert.assertEquals("second sample length", 50, second.getDetails().getSampleLegths().get(Feature2),0);
        Assert.assertEquals("second target length", 40, second.getDetails().getTargetLegths().get(Feature2),0);

    }
    
    @Test
    public void testbestMatchesForFeatures() {
    	//given
        Mockito.when(corpusRepository.findAllEntries()).thenReturn(corpusEntries);
        Mockito.when(corpusRepository.findAudioFileById("1")).thenReturn("1.wav");
        Mockito.when(corpusRepository.findAudioFileById("2")).thenReturn("2.wav");
        Mockito.when(corpusRepository.findAudioFileById("3")).thenReturn("3.wav");

        //when
        Map<String, IValues> target = new HashMap<String, IValues>();
        target.put(Feature1, createFrameValues(1D, 2D, 3D, 4D));
        target.put(Feature2, createFrameValues(4D, 5D, 9D, 7D));
        Map<String, RecognitionResult> results = corpusServiceBaseImpl.bestMatchesForFeatures(target);
        
      //then
        Assert.assertEquals("All 3 entries ", 2, results.size());
        Assert.assertEquals("All 3 entries ", TRYS, results.get(Feature1).getInfo().getName());
        Assert.assertEquals("All 3 entries ", TRYS, results.get(Feature2).getInfo().getName());
        
    }
    

    protected FrameVectorValues createFrameValues(Double... args) {
        FrameVectorValues vectors = new FrameVectorValues();
        vectors.setSampleRate(100D);
        for (Double f : args) {
        	FrameValues fv = new FrameValues(new Double[]{f, f});
        	fv.setSampleRate(1.0);
            vectors.add(fv);
        }
        return vectors;
    }

    public void addFeatureData(Map<String, FrameVectorValuesHolder> data, String featureName, Double... args) {
    	FrameVectorValuesHolder fd = new FrameVectorValuesHolder();
        fd.setValues(createFrameValues(args));
        data.put(featureName, fd);
    }
}

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
import org.spantus.externals.recognition.bean.CorpusEntry;
import org.spantus.externals.recognition.bean.FeatureData;
import org.spantus.externals.recognition.bean.RecognitionResult;
import org.spantus.externals.recognition.bean.RecognitionResultDetails;
import org.spantus.externals.recognition.corpus.CorpusRepository;
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
    private Collection<CorpusEntry> corpusEntries;

    @Before
    public void onSetup() {
        corpusServiceBaseImpl = new CorpusServiceBaseImpl();
        corpusRepository = Mockito.mock(CorpusRepository.class);
        corpusServiceBaseImpl.setCorpus(corpusRepository);
        corpusEntries = new ArrayList<CorpusEntry>();

        CorpusEntry corpusEntry = new CorpusEntry();
        corpusEntry.setId(1L);
        corpusEntry.setName(VIENAS);
        addFeatureData(corpusEntry.getFeatureMap(), Feature1, 0D, 1D, 1D, 1D, 1D);
        addFeatureData(corpusEntry.getFeatureMap(), Feature2, 0D, 2D, 2D, 2D, 2D);
        corpusEntries.add(corpusEntry);


        corpusEntry = new CorpusEntry();
        corpusEntry.setId(2L);
        corpusEntry.setName(DU);
        addFeatureData(corpusEntry.getFeatureMap(), Feature1, 0D, 1D, 1D, 1D, 1D);
        addFeatureData(corpusEntry.getFeatureMap(), Feature2, 0D, 4D, 5D, 6D, 7D);
        corpusEntries.add(corpusEntry);

        corpusEntry = new CorpusEntry();
        corpusEntry.setId(3L);
        corpusEntry.setName(TRYS);
        addFeatureData(corpusEntry.getFeatureMap(), Feature1, 1D, 2D, 3D, 4D);
        addFeatureData(corpusEntry.getFeatureMap(), Feature2, 4D, 5D, 6D, 7D, 8D);
        corpusEntries.add(corpusEntry);
    }

     @Test
    public void testLearn() {
         //given
         CorpusEntry savedResult = new CorpusEntry();
         savedResult.setId(1L);
         Mockito.when(corpusRepository.save((CorpusEntry) Mockito.any())).
                 thenReturn(savedResult);
         AudioInputStream ais = Mockito.mock(AudioInputStream.class);
         Mockito.when(corpusRepository.update(
                  Mockito.any(CorpusEntry.class),
                 Mockito.any(AudioInputStream.class) ))
                 .thenReturn(savedResult);
        //when
        Map<String, IValues> features = new HashMap<String, IValues>();
        features.put(Feature1, createFrameValues(1D, 2D, 3D, 4D, 5D));
        features.put(Feature2, createFrameValues(3D, 4D, 5D, 6D));
        CorpusEntry result = corpusServiceBaseImpl.create(VIENAS,features);
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
        Assert.assertEquals(3, result.getInfo().getId().intValue());

    }

    @Test
    public void testFindMultipleMatch() {
        //given
        Mockito.when(corpusRepository.findAllEntries()).thenReturn(corpusEntries);
        Mockito.when(corpusRepository.findAudioFileById(1L)).thenReturn("1.wav");
        Mockito.when(corpusRepository.findAudioFileById(2L)).thenReturn("2.wav");
        Mockito.when(corpusRepository.findAudioFileById(3L)).thenReturn("3.wav");

        //when
        Map<String, IValues> target = new HashMap<String, IValues>();
        target.put(Feature1, createFrameValues(1D, 2D, 3D, 4D));
        target.put(Feature2, createFrameValues(4D, 5D, 6D, 7D));
        List<RecognitionResultDetails> results = corpusServiceBaseImpl.findMultipleMatch(target);

        //then
        Assert.assertEquals("All 3 entries ", 3, results.size());
        RecognitionResultDetails first = results.get(0);
        RecognitionResultDetails second = results.get(1);
        Assert.assertEquals("match", 3, first.getInfo().getId().longValue());
        Assert.assertEquals("Audio file is set", first.getAudioFilePath(), "3.wav");
        Assert.assertEquals("first sample length", 40D, first.getSampleLegths().get(Feature1));
        Assert.assertEquals("first target length", 40D, first.getTargetLegths().get(Feature1));
        Assert.assertEquals("second sample length", 50D, second.getSampleLegths().get(Feature2));
        Assert.assertEquals("second target length", 40D, second.getTargetLegths().get(Feature2));

    }
    
    @Test
    public void testbestMatchesForFeatures() {
    	//given
        Mockito.when(corpusRepository.findAllEntries()).thenReturn(corpusEntries);
        Mockito.when(corpusRepository.findAudioFileById(1L)).thenReturn("1.wav");
        Mockito.when(corpusRepository.findAudioFileById(2L)).thenReturn("2.wav");
        Mockito.when(corpusRepository.findAudioFileById(3L)).thenReturn("3.wav");

        //when
        Map<String, IValues> target = new HashMap<String, IValues>();
        target.put(Feature1, createFrameValues(1D, 2D, 3D, 4D));
        target.put(Feature2, createFrameValues(4D, 5D, 6D, 7D));
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
            vectors.add(new FrameValues(new Double[]{f, f}));
        }
        return vectors;
    }

    public void addFeatureData(Map<String, FeatureData> data, String featureName, Double... args) {
        FeatureData fd = new FeatureData();
        fd.setName(featureName);
        fd.setValues(createFrameValues(args));
        data.put(fd.getName(), fd);
    }
}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.spnt.recognition.service.test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import junit.framework.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mockito;
import org.spantus.core.FrameValues;
import org.spantus.core.FrameVectorValues;
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
    private List<CorpusEntry> corpusEntries;

    @Before
    public void onSetup() {
        corpusServiceBaseImpl = new CorpusServiceBaseImpl();
        corpusRepository = Mockito.mock(CorpusRepository.class);
        corpusServiceBaseImpl.setCorpus(corpusRepository);
        corpusEntries = new ArrayList<CorpusEntry>();

        CorpusEntry corpusEntry = new CorpusEntry();
        corpusEntry.setId(1L);
        corpusEntry.setName(VIENAS);
        addFeatureData(corpusEntry.getFeatureMap(), Feature1, 1F, 1F, 1F, 1F);
        addFeatureData(corpusEntry.getFeatureMap(), Feature2, 2F, 2F, 2F, 2F);
        corpusEntries.add(corpusEntry);


        corpusEntry = new CorpusEntry();
        corpusEntry.setId(2L);
        corpusEntry.setName(DU);
        addFeatureData(corpusEntry.getFeatureMap(), Feature1, 1F, 1F, 1F, 1F);
        addFeatureData(corpusEntry.getFeatureMap(), Feature2, 4F, 5F, 6F, 7F);
        corpusEntries.add(corpusEntry);

        corpusEntry = new CorpusEntry();
        corpusEntry.setId(3L);
        corpusEntry.setName(TRYS);
        addFeatureData(corpusEntry.getFeatureMap(), Feature1, 1F, 2F, 3F, 4F);
        addFeatureData(corpusEntry.getFeatureMap(), Feature2, 4F, 5F, 6F, 7F);
        corpusEntries.add(corpusEntry);
    }

    @Test
    public void testMatch() {
         //given
        Mockito.when(corpusRepository.findAllEntries()).thenReturn(corpusEntries);
        //when
        Map<String, FrameVectorValues> target = new HashMap<String, FrameVectorValues>();
        target.put(Feature1, createFrameValues(1F, 2F, 3F, 4F));
        target.put(Feature2, createFrameValues(4F, 5F, 6F, 7F));
        RecognitionResult result = corpusServiceBaseImpl.match(target);
         //then
        Assert.assertEquals(3, result.getInfo().getId().intValue());

    }

    @Test
    public void testFindMultipleMatch() {
        //given
        Mockito.when(corpusRepository.findAllEntries()).thenReturn(corpusEntries);

        //when
        Map<String, FrameVectorValues> target = new HashMap<String, FrameVectorValues>();
        target.put(Feature1, createFrameValues(1F, 2F, 3F, 4F));
        target.put(Feature2, createFrameValues(4F, 5F, 6F, 7F));
        List<RecognitionResultDetails> results = corpusServiceBaseImpl.findMultipleMatch(target);

        //then
        Assert.assertEquals("All 3 entries ", 3, results.size());
        Assert.assertEquals("match", 3, results.get(0).getInfo().getId().longValue());
    }

    protected FrameVectorValues createFrameValues(Float... args) {
        FrameVectorValues vectors = new FrameVectorValues();
        for (Float f : args) {
            vectors.add(new FrameValues(new Float[]{f, f}));
        }
        return vectors;
    }

    public void addFeatureData(Map<String, FeatureData> data, String featureName, Float... args) {
        FeatureData fd = new FeatureData();
        fd.setName(featureName);
        fd.setValues(createFrameValues(args));
        data.put(fd.getName(), fd);
    }
}

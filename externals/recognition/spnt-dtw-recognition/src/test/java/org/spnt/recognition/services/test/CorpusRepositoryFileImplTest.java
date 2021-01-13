/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.spnt.recognition.services.test;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.MessageFormat;

import javax.sound.sampled.AudioInputStream;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.junit.Ignore;
import org.spantus.core.FrameValues;
import org.spantus.core.beans.FrameValuesHolder;
import org.spantus.core.beans.SignalSegment;
import org.spantus.core.wav.AudioManagerFactory;
import org.spantus.externals.recognition.bean.CorpusFileEntry;
import org.spantus.externals.recognition.corpus.CorpusRepositoryFileImpl;

/**
 *
 * @author mondhs
 */
public class CorpusRepositoryFileImplTest {
    CorpusRepositoryFileImpl corpusRepository;  
    @Before
    public void onSetup(){
        corpusRepository = new CorpusRepositoryFileImpl();
        corpusRepository.setRepositoryPath("./target/test-classes/corpus");
    }
    
    @Test @Ignore
    public void testCRUDCorpusEntry(){
        //given 
    	SignalSegment corpusEntry = new SignalSegment();
        corpusEntry.setName("Name1");
        FrameValuesHolder fd = new FrameValuesHolder();
//        fd.setName("Feature1");
        fd.setValues(new FrameValues(new Double[]{1D, 2D, 3D}));
        corpusEntry.getFeatureFrameValuesMap().put("Feature1", fd);
        int initialSize = corpusRepository.findAllEntries().size();
        
        //when
        SignalSegment savedCorpusEntry =corpusRepository.save(corpusEntry);
        String savedId =  savedCorpusEntry.getId();
        int savedSize = corpusRepository.findAllEntries().size();

        SignalSegment updatedCorpusEntry =corpusRepository.update(savedCorpusEntry);
        String updatedId =  updatedCorpusEntry.getId();
        int updatedSize = corpusRepository.findAllEntries().size();

        SignalSegment deltedCorpusEntry =corpusRepository.delete(updatedCorpusEntry.getId());
        int deletedSize = corpusRepository.findAllEntries().size();
        String deletedId = deltedCorpusEntry.getId();


        //then
        Assert.assertNotNull(savedId);
        Assert.assertEquals(updatedId, savedId);
        Assert.assertEquals(1, savedSize-initialSize);
        Assert.assertEquals(deletedId, savedId);
        Assert.assertEquals(1, updatedSize-deletedSize);

    }
    @Test @Ignore
    public void testUpdateDeleteWav(){
        //given 
        File inputWavFile = new File("../../../data/text1.wav");
        URL wavUrl = null;
        try {
            wavUrl = inputWavFile.toURI().toURL();
        } catch (MalformedURLException ex) {
            Assert.fail("not working: " + ex.getMessage());
        }
        Assert.assertNotNull("file not found in " 
                +inputWavFile.getAbsoluteFile()
                , wavUrl);
        AudioInputStream ais = AudioManagerFactory.createAudioManager().findInputStream(
                wavUrl,
                null, null);
        SignalSegment corpusEntry = new SignalSegment();
        corpusEntry.setName("Name1");
        FrameValuesHolder fd = new FrameValuesHolder();
        FrameValues fv = new FrameValues(new Double[]{1D, 2D, 3D});
        fv.setSampleRate(1.0);
        fd.setValues(fv);
        corpusEntry.getFeatureFrameValuesMap().put("Feature1", fd);
        
        //when
        SignalSegment savedCorpusEntry =corpusRepository.save(corpusEntry);
        CorpusFileEntry updated = (CorpusFileEntry)corpusRepository.update(savedCorpusEntry, ais);
        boolean updatedWavExist = updated.getWavFile().exists();
        String wavFilePath =updated.getWavFile().getAbsolutePath();
        CorpusFileEntry deleted = (CorpusFileEntry)corpusRepository.delete(updated.getId());
        //then
        String fileName = MessageFormat.format("{0}/{1}-{2}.wav", 
                corpusRepository.getRepoDir(),
                updated.getName(), updated.getId());
       
        Assert.assertTrue(updatedWavExist);
        Assert.assertTrue(wavFilePath+" does not ends with " + fileName,
                wavFilePath.endsWith(fileName));
        Assert.assertFalse("wav file not exist", deleted.getWavFile().exists());
    }
    
}

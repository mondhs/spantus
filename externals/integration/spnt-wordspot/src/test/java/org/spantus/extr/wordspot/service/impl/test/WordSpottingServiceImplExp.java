/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.spantus.extr.wordspot.service.impl.test;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.spantus.core.beans.RecognitionResult;
import org.spantus.core.beans.SignalSegment;
import org.spantus.core.junit.SlowTests;
import org.spantus.core.wav.AudioManagerFactory;
import org.spantus.extr.wordspot.service.impl.WordSpottingListenerLogImpl;

/**
 *
 * @author as
 */
public class WordSpottingServiceImplExp extends WordSpottingServiceImplTest {

    
    @Override
    protected void initPaths() {
        setWavFile(new File("/home/as/tmp/garsynas.lietuvos-mg/TRAIN/", "1.wav"));
        setRepositoryPathRoot(new File("/home/as/tmp/garsynas.lietuvos-mg/"));
        setAcceptableSyllables(new String[]{"liet", "tuvos"});
        setSearchWord("lietuvos");
    }
    
    @Test
    @Category(SlowTests.class)
    @Override
    public void test_wordSpotting() throws MalformedURLException {
        //given
        Long length = 1000L * AudioManagerFactory.createAudioManager().findLength(getWavFile().toURI().toURL()).longValue();
        WordSpottingListenerLogImpl listener = new WordSpottingListenerLogImpl(getSearchWord(),
                getAcceptableSyllables(), 
                getRepositoryPathWord().getAbsolutePath());
        URL url = getWavFile().toURI().toURL();
        //when 
        long started = System.currentTimeMillis();
        wordSpottingServiceImpl.wordSpotting(url, listener);
        long ended = System.currentTimeMillis();
        Map<RecognitionResult, SignalSegment> segments = listener.getWordSegments();
        String resultsStr = extractResultStr(segments);

        //then
        //Assert.assertTrue("read time " + length + ">"+(ended-started), length > ended-started);
        Assert.assertEquals("Recognition", "lietuvos", resultsStr);

    }
}

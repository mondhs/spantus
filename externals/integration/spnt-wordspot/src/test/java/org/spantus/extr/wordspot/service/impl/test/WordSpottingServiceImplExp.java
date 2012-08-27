/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.spantus.extr.wordspot.service.impl.test;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;
import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.spantus.core.beans.RecognitionResult;
import org.spantus.core.beans.SignalSegment;
import org.spantus.core.extractor.dao.MarkerDao;
import org.spantus.core.junit.SlowTests;
import org.spantus.core.marker.Marker;
import org.spantus.core.marker.MarkerSetHolder;
import org.spantus.core.marker.service.IMarkerService;
import org.spantus.core.marker.service.MarkerServiceFactory;
import org.spantus.core.wav.AudioManagerFactory;
import org.spantus.extr.wordspot.service.impl.WordSpottingListenerLogImpl;
import org.spantus.utils.FileUtils;
import org.spantus.work.services.WorkServiceFactory;

/**
 *
 * @author as
 */
public class WordSpottingServiceImplExp extends WordSpottingServiceImplTest {

    private static final Logger log = Logger.getLogger(WordSpottingServiceImplExp.class);
    MarkerDao markerDao;
    IMarkerService markerService;
        
    @Before
    @Override
    public void setUp() throws Exception {
        super.setUp();
        markerDao = WorkServiceFactory.createMarkerDao();
        markerService = MarkerServiceFactory.createMarkerService();
    }
    
    @Override
    protected void initPaths() {
        setWavFile(new File("/home/as/tmp/garsynas.lietuvos-syn/TEST/", "RAj031004_13_11b-30_1.wav"));
        setRepositoryPathRoot(new File("/home/as/tmp/garsynas.lietuvos-syn/"));
        setAcceptableSyllables(new String[]{"liet", "tuvos"});
        setSearchWord("lietuvos");
    }
    

    
    @Test
    @Category(SlowTests.class)
    @Override 
    public void test_wordSpotting() throws MalformedURLException {
        //given
        Long length = 1000L * AudioManagerFactory.createAudioManager().findLength(getWavFile().toURI().toURL()).longValue();
        File markerFile = new File(getWavFile().getParentFile().getAbsoluteFile(),
                 FileUtils.replaceExtention(getWavFile(), ".mspnt.xml"));
        Marker marker = getMarkerService().findByLabel("-l'-ie-t-u-v-oo-s", getMarkerDao().read(markerFile));
        
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

        log.error("Marker =>" + marker);
        log.error(getWavFile() + "=>" + segments);
        
        //then
        //Assert.assertTrue("read time " + length + ">"+(ended-started), length > ended-started);
        Assert.assertEquals("Recognition", "lietuvos", resultsStr);

    }
    
    public MarkerDao getMarkerDao() {
        return markerDao;
    }

    public IMarkerService getMarkerService() {
        return markerService;
    }

    
}

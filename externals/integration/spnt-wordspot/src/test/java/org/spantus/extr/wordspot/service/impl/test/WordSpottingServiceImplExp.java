/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.spantus.extr.wordspot.service.impl.test;

import com.google.common.collect.Ordering;
import com.google.common.primitives.Longs;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;
import java.util.Map.Entry;
import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.spantus.core.beans.RecognitionResult;
import org.spantus.core.beans.SignalSegment;
import org.spantus.core.extractor.dao.MarkerDao;
import org.spantus.core.junit.SlowTests;
import org.spantus.core.marker.Marker;
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
            
    private static final Ordering<Entry<RecognitionResult, SignalSegment>> order = 
            new Ordering<Entry<RecognitionResult, SignalSegment>>(){

        @Override
        public int compare(Entry<RecognitionResult, SignalSegment> left, 
        Entry<RecognitionResult, SignalSegment> right) {
            return Longs.compare(left.getValue().getMarker().getStart(),
                        right.getValue().getMarker().getStart());
        }

//
//            @Override
//            public int compare(Entry<RecognitionResult, SignalSegment> left,
//                Entry<RecognitionResult, SignalSegment> right) {
//                Longs.compare(left.getValue().getMarker().getStart(),
//                        right.getValue().getMarker().getStart());
//            }
//            
//        
    };
        
    @Before
    @Override
    public void setUp() throws Exception {
        super.setUp();
        markerDao = WorkServiceFactory.createMarkerDao();
        markerService = MarkerServiceFactory.createMarkerService();
    }
    
    @Override
    protected void initPaths() {
        String path = 
                "/tmp/test"
//                "/home/as/tmp/garsynas.lietuvos-syn/TEST/"
                ;
        String fileName = 
                "RAj031004_13_11b-30_1.wav"
//                "RAj031004_13_16a-30_1.wav"
                ;
        setWavFile(new File(path, fileName));
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
        Marker marker = getMarkerService().findByLabel("-l'-ie-t-|-u-v-oo-s", getMarkerDao().read(markerFile));
        
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
        log.error(getWavFile() + "=>" + order.sortedCopy(segments.entrySet()));
        
        //then
        //Assert.assertTrue("read time " + length + ">"+(ended-started), length > ended-started);
        Assert.assertEquals("Recognition", "lietuvos", resultsStr);
        SignalSegment firstSegment = segments.values().iterator().next();
        Assert.assertEquals("Recognition start", marker.getStart(), firstSegment.getMarker().getStart(),120D);
        Assert.assertEquals("Recognition length", marker.getLength(), firstSegment.getMarker().getLength(),50);

    }
    
    public MarkerDao getMarkerDao() {
        return markerDao;
    }

    public IMarkerService getMarkerService() {
        return markerService;
    }

    
}

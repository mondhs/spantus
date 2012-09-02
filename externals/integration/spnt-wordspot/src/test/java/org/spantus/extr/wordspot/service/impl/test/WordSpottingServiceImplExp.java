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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
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
import org.spantus.core.marker.service.IMarkerService;
import org.spantus.core.marker.service.MarkerServiceFactory;
import org.spantus.core.wav.AudioManagerFactory;
import org.spantus.extr.wordspot.service.impl.WordSpottingListenerLogImpl;
import org.spantus.extr.wordspot.service.impl.test.util.ExtNameFilter;
import org.spantus.extr.wordspot.util.dao.WordSpotResult;
import org.spantus.extr.wordspot.util.dao.WspotJdbcDao;
import org.spantus.utils.FileUtils;
import org.spantus.work.services.WorkServiceFactory;

/**
 *
 * @author as
 */
public class WordSpottingServiceImplExp extends WordSpottingServiceImplTest {

    private static final Logger log = Logger.getLogger(WordSpottingServiceImplExp.class);
    private MarkerDao markerDao;
    private WspotJdbcDao wspotDao;
    private IMarkerService markerService;
    

    
    private static final Ordering<Entry<RecognitionResult, SignalSegment>> order =
            new Ordering<Entry<RecognitionResult, SignalSegment>>() {
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
         wspotDao = new WspotJdbcDao();
    }

    @Override
    protected void initPaths() {
        String path =
                "/tmp/test" //                "/home/as/tmp/garsynas.lietuvos-syn/TEST/"
                ;
        String fileName =
                //                "RAj031004_13_11b-30_1.wav"
                //                "RAj031004_13_16a-30_1.wav"
                //                "RAj031013_18_24a-30_1.wav"
//                "RCz041110_18_29-30_1.wav"
                "RBz041003_18_6-30_1.wav"
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
        
        WordSpotResult result = doWordspot(getWavFile());
        String resultsStr = extractResultStr(result.getSegments());

        log.error("Marker =>" + result.getOriginalMarker());
        log.error(getWavFile() + "=>" + order.sortedCopy(result.getSegments().entrySet()));

        //then
        //Assert.assertTrue("read time " + length + ">"+(ended-started), length > ended-started);
        Assert.assertEquals("Recognition", "lietuvos", resultsStr);
        SignalSegment firstSegment = result.getSegments().values().iterator().next();
        Assert.assertEquals("Recognition start", result.getOriginalMarker().getStart(), firstSegment.getMarker().getStart(), 120D);
        Assert.assertEquals("Recognition length", result.getOriginalMarker().getLength(), firstSegment.getMarker().getLength(), 100);

    }

 
    @Ignore
    @Test
    @Category(SlowTests.class)
    public void bulkTest() throws MalformedURLException {
        wspotDao.setRecreate(true);
        wspotDao.init();
        
        File[] files = getWavFile().getParentFile().listFiles(new ExtNameFilter("wav"));
        List<AssertionError> list = new ArrayList<>();
        
        for (File file : files) {
//            if(!file.getName().contains(
////                "RBz041003_18_6-30_1.wav"
//                    "RZm0826_13_32-30_1.wav"
//                    )){
//                continue;
//            }
                WordSpotResult result = doWordspot(file);
                wspotDao.save(result);
//                String resultsStr = extractResultStr(result.getSegments());
                log.debug("Processed: " + file);
        }
//        log.error("files =>" + files.length);
//        log.error("list =>" + list);
//        Assert.assertEquals(0, list.size());
        wspotDao.destroy();

    }

       public WordSpotResult doWordspot(File aWavFile) throws MalformedURLException {
        URL aWavUrl = aWavFile.toURI().toURL();
        
        WordSpotResult result = new WordSpotResult();
        WordSpottingListenerLogImpl listener = new WordSpottingListenerLogImpl(getSearchWord(),
                getAcceptableSyllables(),
                getRepositoryPathWord().getAbsolutePath());
        Long length = AudioManagerFactory.createAudioManager().findLengthInMils(
                aWavUrl);
        File markerFile = new File(aWavFile.getParentFile().getAbsoluteFile(),
                FileUtils.replaceExtention(aWavFile, ".mspnt.xml"));
        Marker marker = getMarkerService().findByLabel("-l'-ie-t-|-u-v-oo-s", getMarkerDao().read(markerFile));
        result.setAudioLength(length);
        result.setOriginalMarker(marker);
        result.setFileName(aWavFile.getName());
        
        //when 
        result.setExperimentStarted(System.currentTimeMillis());
        wordSpottingServiceImpl.wordSpotting(aWavUrl, listener);
        result.setExperimentEnded(System.currentTimeMillis());
        Map<RecognitionResult, SignalSegment> segments = listener.getWordSegments();
        result.setSegments(segments);
        return result;

    }
    
    public MarkerDao getMarkerDao() {
        return markerDao;
    }

    public IMarkerService getMarkerService() {
        return markerService;
    }
}

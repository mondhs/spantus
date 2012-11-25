/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.spantus.extr.wordspot.service.scrolling.impl.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.spantus.core.beans.RecognitionResult;
import org.spantus.core.beans.SignalSegment;
import org.spantus.core.extractor.IExtractorInputReader;
import org.spantus.core.junit.SlowTests;
import org.spantus.core.marker.Marker;
import org.spantus.core.marker.MarkerSetHolder;
import org.spantus.core.wav.AudioManagerFactory;
import org.spantus.extr.wordspot.guava.RecognitionResultSignalSegmentOrder;
import org.spantus.extr.wordspot.service.SpottingListener;
import org.spantus.extr.wordspot.service.impl.test.util.ExtNameFilter;
import org.spantus.extr.wordspot.util.dao.WordSpotResult;
import org.spantus.extr.wordspot.util.dao.WspotJdbcDao;
import org.spantus.extractor.impl.ExtractorEnum;

import com.google.common.collect.Ordering;

/**
 *
 * @author mondhs
 */
public class WordWindowScrollingSpottingExp extends WindowScrollingSpottingTest {

    private static final Logger log = LoggerFactory.getLogger(WordWindowScrollingSpottingExp.class);
    
	private WspotJdbcDao wspotDao;

	private static final String[] KEY_WORD_SEQUENCE_ARR = new String[]{"liet","uvoos"};
	private static final String KEY_WORD_NAME = "lietuvos";
	
    
    private static final Ordering<Entry<RecognitionResult, SignalSegment>> order = new RecognitionResultSignalSegmentOrder();

    @Override
    public void setUp() throws Exception {
    	super.setUp();
		wspotDao = new WspotJdbcDao();
    }
    
    
    @Override
    protected File createRepositoryPathRoot(){
        return  
//        		new File("/home/as/tmp/garsynas.lietuvos-syn-dynlen");
				new File("/home/as/tmp/garsynas.lietuvos-syn-wpitch");
//        		new File("/home/as/tmp/garsynas.lietuvos-syn-wopitch");
    }
    
    @Override
    protected File createRepositoryPath(File aRepositoryPathRoot) {
    	return new File(aRepositoryPathRoot,"CORPUS/word");
    }
    
	@Override
	protected File createWavFile(File aRepositoryPathRoot) {
		String internalPath = 
				"TRAIN/"
//				"TEST/"
				;
		String fileName = internalPath + 
//				"RBg031126_13_31-30_1.wav"
		 "lietuvos_mbr_test-30_1.wav"
		;
		return new File(aRepositoryPathRoot, fileName);
	}

    
	@Ignore
    @Test
    @Category(SlowTests.class)
    public void bulkTest() throws MalformedURLException {
        wspotDao.setRecreate(true);
        wspotDao.init();
        
        File[] files = getWavFile().getParentFile().listFiles(new ExtNameFilter("wav"));
        int foundSize = 0;
        for (File file : files) {
//            if(!file.getName().contains(
//                    "RBg031126_13_31-30_1.wav"
//                    )){
//                continue;
//            }
             log.debug("start: " + file);
                WordSpotResult result = doWordspot(file);
                wspotDao.save(result);
                foundSize += result.getSegments().size();
//                String resultsStr = extractResultStr(result.getSegments());
                log.debug("done: " + file);
                log.error("Marker =>" + result.getOriginalMarker());
                log.error(getWavFile() + "=>" + order.sortedCopy(result.getSegments().entrySet()));                
        }
//        log.error("files =>" + files.length);
        log.error("foundSize =>" + foundSize);
//        Assert.assertEquals(0, list.size());
        wspotDao.destroy();
        Assert.assertTrue("One element at least", foundSize>0);

    }
	


    @Test
    @Override
    public void testWordSpotting() throws MalformedURLException {
        //given
        URL aWavUrl = getWavFile().toURI().toURL();
        SignalSegment keySegment = findKeywordSegment(KEY_WORD_NAME, getWavFile(),  KEY_WORD_SEQUENCE_ARR);

        getSpottingService().addKeySegment(keySegment);
        final SignalSegment foundSegment = new SignalSegment();
        //when
        getSpottingService().wordSpotting(aWavUrl, new SpottingListener() {
            @Override
            public String foundSegment(String sourceId, SignalSegment newSegment, List<RecognitionResult> recognitionResults) {
                foundSegment.setMarker(newSegment.getMarker());
                return newSegment.getMarker().getLabel();
            }
        });
        //then
        assertNotNull("Keyword not found", foundSegment);
        assertNotNull("Keyword not found", foundSegment.getMarker());
        assertNotNull("Keyword not found", foundSegment.getMarker().getStart());
        assertEquals("start of found key marker same as matched", getSpottingService().getKeySegmentList().get(0).getMarker().getStart(),
                foundSegment.getMarker().getStart(), 220L);
    }
    
    /**
     * 
     * @param file
     * @return
     * @throws MalformedURLException
     */
    private WordSpotResult doWordspot(File aWavFile) throws MalformedURLException {
    	 WordSpotResult result = new WordSpotResult();
    	 URL aWavUrl = aWavFile.toURI().toURL();
    	 SignalSegment keySegment = findKeywordSegment(KEY_WORD_NAME, aWavFile,  KEY_WORD_SEQUENCE_ARR);
 	     Assert.assertNotNull("keyword not found", keySegment);
 		 Long length = AudioManagerFactory.createAudioManager()
				.findLengthInMils(aWavUrl);
         result.setAudioLength(length);
         result.getOriginalMarker().add(keySegment.getMarker());
         result.setFileName(aWavFile.getName());
         result.setExperimentStarted(System.currentTimeMillis());
         final Map<RecognitionResult, SignalSegment> segments = new LinkedHashMap<>();
         getSpottingService().addKeySegment(keySegment);
    	 

         final SignalSegment foundSegment = new SignalSegment();
         //when
         getSpottingService().wordSpotting(aWavUrl, new SpottingListener() {
             @Override
             public String foundSegment(String sourceId, SignalSegment newSegment, List<RecognitionResult> recognitionResults) {
                 foundSegment.setMarker(newSegment.getMarker());
                 segments.put(recognitionResults.get(0), newSegment);
                 return newSegment.getMarker().getLabel();
             }
         });

         result.setExperimentEnded(System.currentTimeMillis());
         result.setSegments(segments);
         
         return result;

	}
    @Test
    @Override
    public void testExactPlaceWordSpotting() throws MalformedURLException {
        //given
        URL aWavUrl = getWavFile().toURI().toURL();
        SignalSegment keySegment = findKeywordSegment(KEY_WORD_NAME,  getWavFile(), KEY_WORD_SEQUENCE_ARR);
        getSpottingService().setDelta(1);
        getSpottingService().addKeySegment(keySegment);

        //when
        IExtractorInputReader reader = getSpottingService().createReader(aWavUrl);
        SignalSegment recalculatedFeatures = getSpottingService().recalculateFeatures(reader, keySegment.getMarker());
        List<RecognitionResult> matchedResults = getSpottingService().match(recalculatedFeatures);

        //then

        assertNotNull(matchedResults);
        assertEquals("Results", 2, matchedResults.size());
        RecognitionResult matched = matchedResults.get(0);
        assertEquals("Results", KEY_WORD_NAME, matched.getInfo().getName());
        assertEquals("Results", 6E9, matched.getDetails().getDistances().get(ExtractorEnum.MFCC_EXTRACTOR.name()), 1E9);
    }
    
	protected SignalSegment findKeywordSegment(String keyWordName, File aWavFile, String... keyWordSequence) {
		MarkerSetHolder markers = findMarkerSetHolderByWav(aWavFile);
		Marker keywordMarker = getMarkerService().findFirstByPhrase(markers, keyWordSequence);
//    		Marker marker = findKeyword(aWavFile, keyWord);
    	 SignalSegment keySegment = new SignalSegment(new Marker(keywordMarker.getStart(),
    			 keywordMarker.getLength(),
    			 keyWordName));
        return keySegment;
    }
}

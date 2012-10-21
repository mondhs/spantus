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
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.log4j.Logger;
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
import org.spantus.extr.wordspot.service.SpottingListener;
import org.spantus.extr.wordspot.service.impl.test.util.ExtNameFilter;
import org.spantus.extr.wordspot.util.dao.WordSpotResult;
import org.spantus.extractor.impl.ExtractorEnum;

import com.google.common.collect.Ordering;
import com.google.common.primitives.Longs;

/**
 *
 * @author mgreibus
 */
public class WindowScrollingSpottingExp extends WindowScrollingSpottingTest {

    private static final Logger log = Logger.getLogger(WindowScrollingSpottingExp.class);
    
    private static final Ordering<Entry<RecognitionResult, SignalSegment>> order =
            new Ordering<Entry<RecognitionResult, SignalSegment>>() {
                @Override
                public int compare(Entry<RecognitionResult, SignalSegment> left,
                        Entry<RecognitionResult, SignalSegment> right) {
                    return Longs.compare(left.getValue().getMarker().getStart(),
                            right.getValue().getMarker().getStart());
                }
            };

    
    @Override
    protected File createRepositoryPathRoot(){
        return  new File("/home/as/tmp/garsynas.lietuvos-syn-wopitch");
    }
    
	@Override
	protected File createWavFile(File aRepositoryPathRoot) {
		String internalPath = "TEST/";
		String fileName = internalPath + "RBg031126_13_31-30_1.wav"
		// "lietuvos_mbr_test-30_1.wav"
		;
		return new File(aRepositoryPathRoot, fileName);
	}

    

    @Override
    protected Marker findByLabel(MarkerSetHolder markers) {
        Marker rtn = findKeyword(getWavFile(), "-l-ie-t");
        //new Marker(4137L, 363L, "liet");
        rtn.setLabel("liet");
        return rtn;
        
    }

    @Test
    @Category(SlowTests.class)
    public void bulkTest() throws MalformedURLException {
//        wspotDao.setRecreate(true);
//        wspotDao.init();
        
        File[] files = getWavFile().getParentFile().listFiles(new ExtNameFilter("wav"));
        List<AssertionError> list = new ArrayList<>();
        int foundSize = 0;
        for (File file : files) {
//            if(!file.getName().contains(
//                    "RZd0706_18_06-30_1.wav"
//                    )){
//                continue;
//            }
             log.debug("start: " + file);
                WordSpotResult result = doWordspot(file);
//                wspotDao.save(result);
                foundSize += result.getSegments().size();
//                String resultsStr = extractResultStr(result.getSegments());
                log.debug("done: " + file);
                log.error("Marker =>" + result.getOriginalMarker());
                log.error(getWavFile() + "=>" + order.sortedCopy(result.getSegments().entrySet()));                
        }
//        log.error("files =>" + files.length);
        log.error("foundSize =>" + foundSize);
//        Assert.assertEquals(0, list.size());
//        wspotDao.destroy();
        Assert.assertTrue("One element at least", foundSize>0);

    }
    
    @Ignore
    @Test
    @Override
    public void testWordSpotting() throws MalformedURLException {
        //given
        URL aWavUrl = getWavFile().toURI().toURL();
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
        assertNotNull(foundSegment.getMarker());
        assertEquals("start of found key marker same as matched", getSpottingService().getKeySegment().getMarker().getStart(),
                foundSegment.getMarker().getStart(), 100L);
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
 	     Marker keywordMarker = findByLabel(null);
 		 Long length = AudioManagerFactory.createAudioManager()
				.findLengthInMils(aWavUrl);
         result.setAudioLength(length);
         result.setOriginalMarker(keywordMarker);
         result.setFileName(aWavFile.getName());
         result.setExperimentStarted(System.currentTimeMillis());
         final Map<RecognitionResult, SignalSegment> segments = new LinkedHashMap<>();

         MarkerSetHolder markers = findMarkerSetHolderByWav(aWavFile);
         Marker marker = findByLabel(markers);
         SignalSegment keySegment = new SignalSegment(new Marker(marker.getStart(), marker.getLength(),
                 marker.getLabel()));
         getSpottingService().setKeySegment(keySegment);
    	 

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
    
    @Ignore
    @Test
    @Override
    public void testExactPlaceWordSpotting() throws MalformedURLException {
        //given
        URL aWavUrl = getWavFile().toURI().toURL();
        Marker aMarker = findByLabel(null);
        getSpottingService().setDelta(1);

        //when
        IExtractorInputReader reader = getSpottingService().createReader(aWavUrl);
        SignalSegment recalculatedFeatures = getSpottingService().recalculateFeatures(reader, aMarker);
        List<RecognitionResult> matchedResults = getSpottingService().match(recalculatedFeatures);

        //then

        assertNotNull(matchedResults);
        assertEquals("Results", 3, matchedResults.size());
        RecognitionResult matched = matchedResults.get(0);
        assertEquals("Results", 5E9, matched.getDetails().getDistances().get(ExtractorEnum.MFCC_EXTRACTOR.name()), 1E9);
    }
}

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
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Ordering;

/**
 * 
 * @author mondhs
 */
public class SyllableWindowScrollingSpottingExp extends
		WindowScrollingSpottingTest {

	private static final Logger LOG = LoggerFactory
			.getLogger(SyllableWindowScrollingSpottingExp.class);

	private WspotJdbcDao wspotDao;

	private Map<String, String> keyWordMap = ImmutableMap.of("uvoos", "tuvos", "liet", "liet");

	private static final Ordering<Entry<RecognitionResult, SignalSegment>> order = new RecognitionResultSignalSegmentOrder();
	
	@Override
	public void setUp() throws Exception {
		super.setUp();
		wspotDao = new WspotJdbcDao();
	}

	@Override
	protected File createRepositoryPathRoot() {
		return 
//				new File("/home/as/tmp/garsynas.lietuvos");
//				new File("/home/as/tmp/garsynas_2lietuvos/garsynas_wopitch");
//				new File("/home/as/tmp/garsynas_2lietuvos/garsynas_dynlen");
				new File("/home/as/tmp/garsynas_2lietuvos/garsynas_pitch");

	}

	@Override
	protected File createWavFile(File aRepositoryPathRoot) {
		String internalPath =
//				"TRAIN/"
				"TEST/"
				;
		String fileName = internalPath +
//		"RZd0826_18_12c.wav"
		"001-30_1.wav"
//		 "lietuvos_mbr_test-30_1.wav"
		;
		return new File(aRepositoryPathRoot, fileName);
	}
	
//	@Ignore
	@Test
	@Category(SlowTests.class)
	public void bulkTest() throws MalformedURLException {
		wspotDao.setRecreate(true);
		wspotDao.init();
		LOG.debug("path: {}", getWavFile().getParentFile().getAbsoluteFile());
		File[] files = getWavFile().getParentFile().listFiles(
				new ExtNameFilter("wav"));
		LOG.debug("fileSize: {}", files.length);
        int foundSize = 0;
        int index = 0;
		for (File file : files) {
			for (Entry<String, String> keyEntiry : keyWordMap.entrySet()) {
				// if(!file.getName().contains(
				// "RBg031126_13_31-30_1.wav"
				// )){
				// continue;
				// }
        		Long start = System.currentTimeMillis();
             	LOG.debug("start {}", file);
             	LOG.debug("index {} - {}", index,  keyEntiry);
				WordSpotResult result = doWordspot(keyEntiry.getValue(), file,
						keyEntiry.getKey());
				wspotDao.save(result);
				foundSize += result.getSegments().size();
				// String resultsStr = extractResultStr(result.getSegments());
                LOG.debug("Marker => {}", result.getOriginalMarker());
                LOG.debug("KeySegmentList => {}", getSpottingService().getKeySegmentList().size());
                
                LOG.debug("{} => {}",getWavFile(), order.sortedCopy(result.getSegments().entrySet()));
                LOG.debug("{} => {}",getWavFile(), order.sortedCopy(result.getSegments().entrySet()));
                LOG.debug("done {} in {} : {}\n", new Object[]{index,  System.currentTimeMillis()-start, file});
			}
			index++;
		}
		// log.error("files =>" + files.length);
        LOG.debug("foundSize =>{}", foundSize);
		// Assert.assertEquals(0, list.size());
		wspotDao.destroy();
		Assert.assertTrue("One element at least", foundSize > 0);

	}
	@Ignore
	@Test
	public void testDoWordSpotting() throws MalformedURLException {
		
		// given
		File file = getWavFile();
		for (Entry<String, String> keyEntiry : keyWordMap.entrySet()) {
			Collection<Marker> keySegments = findKeywordSegments(keyEntiry.getValue(),
					getWavFile(), keyEntiry.getKey());
			Marker keySegment = keySegments.iterator().next();
			getSpottingService().addKeySegment(new SignalSegment(keySegment));
    		Long start = System.currentTimeMillis();
         	
			LOG.debug("start {}", file);
         	LOG.debug("index {} ",  keyEntiry);
	        WordSpotResult result = doWordspot(keyEntiry.getValue(), file,
					keyEntiry.getKey());
            LOG.debug("done  in {} : {}\n", new Object[]{System.currentTimeMillis()-start, file});
			// then
			 assertEquals("foundSegments",2,  result.getSegments().size(),2);
			 final SignalSegment cKeySegment = getSpottingService().getKeySegmentList().get(0);
			 SignalSegment foundSegment = Iterables.find(result.getSegments().values(), new MatchedPredicate(cKeySegment));
			assertNotNull("Keyword not found", foundSegment);
			assertNotNull("Keyword not found", foundSegment.getMarker());
			assertNotNull("Keyword not found", foundSegment.getMarker()
					.getStart());
			assertEquals("Keyword not found", keyEntiry.getValue(), foundSegment.getMarker().getLabel());
			assertEquals(
					"start of found key marker should be same",
					getSpottingService().getKeySegmentList().get(0).getMarker().getStart(),
					foundSegment.getMarker().getStart(), 250L);
		}

	}

	@Ignore
	@Test
	@Override
	public void testWordSpotting() throws MalformedURLException {
		// given
		URL aWavUrl = getWavFile().toURI().toURL();
		for (Entry<String, String> keyEntiry : keyWordMap.entrySet()) {
			Collection<Marker> keySegments = findKeywordSegments(keyEntiry.getValue(),
					getWavFile(), keyEntiry.getKey());
			Marker keySegment = keySegments.iterator().next();
			getSpottingService().addKeySegment(new SignalSegment(keySegment));
	        final List<SignalSegment> foundSegments = Lists.newArrayList();
			// when
			getSpottingService().wordSpotting(aWavUrl, new SpottingListener() {
				@Override
				public String foundSegment(String sourceId,
						SignalSegment newSegment,
						List<RecognitionResult> recognitionResults) {
					foundSegments.add(newSegment);
					return newSegment.getMarker().getLabel();
				}
			});
			// then
			assertEquals("foundSegments",4,  foundSegments.size(),0);
			SignalSegment foundSegment = Iterables.find(foundSegments, new MatchedPredicate(keySegment));
			assertNotNull("Keyword not found", foundSegment);
			assertNotNull("Keyword not found", foundSegment.getMarker());
			assertNotNull("Keyword not found", foundSegment.getMarker()
					.getStart());
			assertEquals("Keyword not found", keyEntiry.getValue(), foundSegment.getMarker().getLabel());
			assertEquals(
					"start of found key marker should be same",
					keySegment.getStart(),
					foundSegment.getMarker().getStart(), 250L);
		}

	}

	/**
	 * 
	 * @param file
	 * @return
	 * @throws MalformedURLException
	 */
	private WordSpotResult doWordspot(String keywordValue, File aWavFile,
			String keyWordCode) throws MalformedURLException {
		WordSpotResult result = new WordSpotResult();
		URL aWavUrl = aWavFile.toURI().toURL();
		Collection<Marker> keySegments = findKeywordSegments(keywordValue, aWavFile,
				keyWordCode);
		Marker keySegment = keySegments.iterator().next();
		Assert.assertNotNull("keyword not found", keySegment);
		Long length = AudioManagerFactory.createAudioManager()
				.findLengthInMils(aWavUrl);
		result.setAudioLength(length);
		result.getOriginalMarker().addAll(keySegments);
		result.setFileName(aWavFile.getName());
		result.setExperimentStarted(System.currentTimeMillis());
		final Map<RecognitionResult, SignalSegment> segments = new LinkedHashMap<>();
        if(getSpottingService().getKeySegmentList() != null){
       	 getSpottingService().getKeySegmentList().clear();
        }
		getSpottingService().addKeySegment(new SignalSegment(keySegment));
		getSpottingService().setDelta(10);

		final SignalSegment foundSegment = new SignalSegment();
		// when
		getSpottingService().wordSpotting(aWavUrl, new SpottingListener() {
			@Override
			public String foundSegment(String sourceId,
					SignalSegment newSegment,
					List<RecognitionResult> recognitionResults) {
				foundSegment.setMarker(newSegment.getMarker());
				newSegment.getMarker().setId(Long.valueOf(segments.size()));
				segments.put(recognitionResults.get(0), newSegment);
				return newSegment.getMarker().getLabel();
			}
		});
		result.setOperationCount(getSpottingService().getOperationCount());
		result.setExperimentEnded(System.currentTimeMillis());
		result.setSegments(segments);

		return result;
	}
	@Ignore
	@Test
	@Override
	public void testExactPlaceWordSpotting() throws MalformedURLException {
		// given
		URL aWavUrl = getWavFile().toURI().toURL();
		Collection<Marker> keySegments = findKeywordSegments("liet", getWavFile(),
				"liet");
		Marker keySegment = keySegments.iterator().next();
		getSpottingService().setDelta(1);
		getSpottingService().addKeySegment(new SignalSegment(keySegment));

		// when
		IExtractorInputReader reader = getSpottingService().createReader(
				aWavUrl);
		SignalSegment recalculatedFeatures = getSpottingService()
				.recalculateFeatures(reader, keySegment);
		List<RecognitionResult> matchedResults = getSpottingService().match(
				recalculatedFeatures);
		// then
		assertNotNull(matchedResults);
		assertEquals("Results", 5, matchedResults.size());
		RecognitionResult matched = matchedResults.get(0);
		assertEquals("Results", "liet", matched.getInfo().getName());
		assertEquals("Results", 2.24, matched.getDetails()
				.getDistances().get(ExtractorEnum.MFCC_EXTRACTOR.name()), 5e9);
	}

	protected Collection<Marker> findKeywordSegments(String keyWordValue,
			File aWavFile, String keyWordCode) {
		MarkerSetHolder markers = findMarkerSetHolderByWav(aWavFile);
		Collection<Marker> markerList = getMarkerService().findAllByLabel(markers,
				keyWordCode);
		// marker marker = findkeyword(awavfile, keyword);
//		signalsegment keysegment = new signalsegment(new marker(
//				keywordmarker.getstart(), keywordmarker.getlength(),
//				keywordname));
		if(markerList == null || markerList.isEmpty()){
			Collection<String> phoneCollection = Collections.emptyList();
			if("liet".equals(keyWordValue)){
				phoneCollection = Lists.newArrayList("l'", "ie", "t"); 
			}else if ("tuvos".equals(keyWordValue)) {
				phoneCollection = Lists.newArrayList("u", "v", "o:", "s"); 
			}
			markerList = getMarkerService().findAllByPhrase(markers,phoneCollection);
		}
		long i = 0;
		for (Marker marker : markerList) {
			marker.setId(i++);
			marker.setLabel(keyWordValue);
		}
		return markerList;
	}
}

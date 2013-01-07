/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.spantus.extr.wordspot.service.scrolling.impl.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
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
import org.spantus.extr.wordspot.service.impl.AcceptableSyllableThresholdDaoImpl;
import org.spantus.extr.wordspot.service.impl.test.util.ExtNameFilter;
import org.spantus.extr.wordspot.util.dao.WordSpotResult;
import org.spantus.extr.wordspot.util.dao.WspotJdbcDao;
import org.spantus.extractor.impl.ExtractorEnum;

import com.google.common.collect.Lists;
import com.google.common.collect.Ordering;

/**
 * 
 * @author mondhs
 */
public class WordWindowScrollingSpottingExp extends WindowScrollingSpottingTest {

	private static final Logger LOG = LoggerFactory
			.getLogger(WordWindowScrollingSpottingExp.class);

	private WspotJdbcDao wspotDao;

	private static final String[] KEY_WORD_SEQUENCE_ARR = new String[] {
			"liet", "uvoos" };
	private static final String SEARCH_KEY_WORD = "lietuvos";

	private static final Ordering<Entry<RecognitionResult, SignalSegment>> order = new RecognitionResultSignalSegmentOrder();

	@Override
	public void setUp() throws Exception {
		super.setUp();
		wspotDao = new WspotJdbcDao();
	}

	@Override
	protected File createRepositoryPathRoot() {
		return 
//		 new File("/home/as/tmp/garsynas_2lietuvos/garsynas_wopitch");
//		 new File("/home/as/tmp/garsynas_2lietuvos/garsynas_pitch");
		 new File("/home/as/tmp/garsynas_2lietuvos/garsynas_dynlen");

	}

	@Override
	protected File createRepositoryPath(File aRepositoryPathRoot) {
		return new File(aRepositoryPathRoot, "CORPUS/word");
	}

	@Override
	protected File createWavFile(File aRepositoryPathRoot) {
		String internalPath =
//		 "TRAIN/";
		"TEST/";
		String fileName = internalPath + 
				"001-30_1.wav"
//		 "lietuvos_mbr_test-30_1.wav"
		;
		return new File(aRepositoryPathRoot, fileName);
	}

//	 @Ignore
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
			// if(!file.getName().contains(
			// "RBg031126_13_31-30_1.wav"
			// )){
			// continue;
			// }
			Long start = System.currentTimeMillis();
			LOG.debug("start {}: {}", index, file);
			WordSpotResult result = doWordspot(file);
			wspotDao.save(result);
			foundSize += result.getSegments().size();
			// String resultsStr = extractResultStr(result.getSegments());
			LOG.debug("Marker => {}", result.getOriginalMarker());
			LOG.debug("KeySegmentList => {}", getSpottingService()
					.getKeySegmentList().size());

			LOG.debug("{} => {}", getWavFile(),
					order.sortedCopy(result.getSegments().entrySet()));
			LOG.debug("{} => {}", getWavFile(),
					order.sortedCopy(result.getSegments().entrySet()));
			LOG.debug("done {} in {} : {}\n",
					new Object[] { index, System.currentTimeMillis() - start,
							file });
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
	@Override
	public void testWordSpotting() throws MalformedURLException {
		// given
		URL aWavUrl = getWavFile().toURI().toURL();
		Collection<Marker> markers = findKeywordSegment(SEARCH_KEY_WORD,
				getWavFile(), KEY_WORD_SEQUENCE_ARR);
		SignalSegment keySegment = new SignalSegment(markers.iterator().next());

		getSpottingService().addKeySegment(keySegment);
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
		assertEquals("foundSegments", 2, foundSegments.size(), 0);
		SignalSegment foundSegment = foundSegments.get(0);
		assertEquals("Results", SEARCH_KEY_WORD, foundSegment.getMarker()
				.getLabel());
		assertNotNull("Keyword not found", foundSegment);
		assertNotNull("Keyword not found", foundSegment.getMarker());
		assertNotNull("Keyword not found", foundSegment.getMarker().getStart());
		assertEquals("start of found key marker same as matched",
				getSpottingService().getKeySegmentList().get(0).getMarker()
						.getStart(), foundSegment.getMarker().getStart(), 220L);
	}

	/**
	 * 
	 * @param file
	 * @return
	 * @throws MalformedURLException
	 */
	private WordSpotResult doWordspot(File aWavFile)
			throws MalformedURLException {
		WordSpotResult result = new WordSpotResult();
		URL aWavUrl = aWavFile.toURI().toURL();
		Collection<Marker> keywordMarkers = findKeywordSegment(SEARCH_KEY_WORD,
				aWavFile, KEY_WORD_SEQUENCE_ARR);
		SignalSegment keySegment = new SignalSegment(keywordMarkers.iterator().next());
		Assert.assertNotNull("keyword not found", keySegment);
		Long length = AudioManagerFactory.createAudioManager()
				.findLengthInMils(aWavUrl);
		result.setAudioLength(length);
		result.getOriginalMarker().addAll(keywordMarkers);
		result.setFileName(aWavFile.getName());
		result.setExperimentStarted(System.currentTimeMillis());
		final Map<RecognitionResult, SignalSegment> segments = new LinkedHashMap<>();
		if (getSpottingService().getKeySegmentList() != null) {
			getSpottingService().getKeySegmentList().clear();
		}
		getSpottingService().addKeySegment(keySegment);

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
		Collection<Marker> keywordMarkers = findKeywordSegment(SEARCH_KEY_WORD,
				getWavFile(), KEY_WORD_SEQUENCE_ARR);
		SignalSegment keySegment = new SignalSegment(keywordMarkers.iterator().next());
		getSpottingService().setDelta(1);
		getSpottingService().addKeySegment(keySegment);
		AcceptableSyllableThresholdDaoImpl acceptableSyllableThresholdDaoImpl = new AcceptableSyllableThresholdDaoImpl();
		Map<String, Double> test = acceptableSyllableThresholdDaoImpl.read(
				getRepositoryPath().getAbsolutePath(), "word");
		Double keyword_threshold = test.get(SEARCH_KEY_WORD);

		// when
		IExtractorInputReader reader = getSpottingService().createReader(
				aWavUrl);
		SignalSegment recalculatedFeatures = getSpottingService()
				.recalculateFeatures(reader, keySegment.getMarker());
		List<RecognitionResult> matchedResults = getSpottingService().match(
				recalculatedFeatures);

		// then

		assertNotNull(matchedResults);
		assertTrue("Results", matchedResults.size() > 1);
		RecognitionResult matched = matchedResults.get(0);
		assertEquals("Results", SEARCH_KEY_WORD, matched.getInfo().getName());
		assertEquals("Results", keyword_threshold, matched.getDetails()
				.getDistances().get(ExtractorEnum.MFCC_EXTRACTOR.name()),
				keyword_threshold);
	}

	protected Collection<Marker> findKeywordSegment(String keyWordName,
			File aWavFile, String... keyWordSequence) {
		MarkerSetHolder markers = findMarkerSetHolderByWav(aWavFile);
		Collection<Marker> markerList = getMarkerService().findAllByPhrase(
				markers, keyWordSequence);
		long i = 0;
		for (Marker marker : markerList) {
			marker.setLabel(keyWordName);
			marker.setId(i++);
		}
		// Marker marker = findKeyword(aWavFile, keyWord);
		return markerList;
	}
}

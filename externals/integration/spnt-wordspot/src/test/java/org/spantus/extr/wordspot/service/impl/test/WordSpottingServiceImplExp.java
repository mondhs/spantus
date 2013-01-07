/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.spantus.extr.wordspot.service.impl.test;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spantus.core.beans.RecognitionResult;
import org.spantus.core.beans.SignalSegment;
import org.spantus.core.junit.SlowTests;
import org.spantus.core.marker.Marker;
import org.spantus.core.marker.MarkerSetHolder;
import org.spantus.core.wav.AudioManagerFactory;
import org.spantus.extr.wordspot.guava.RecognitionResultSignalSegmentOrder;
import org.spantus.extr.wordspot.service.impl.WordSpottingListenerLogImpl;
import org.spantus.extr.wordspot.service.impl.test.util.ExtNameFilter;
import org.spantus.extr.wordspot.util.dao.WordSpotResult;
import org.spantus.extr.wordspot.util.dao.WspotJdbcDao;

import com.google.common.collect.Ordering;

/**
 * 
 * @author as
 */
public class WordSpottingServiceImplExp extends WordSpottingServiceImplTest {

	private static final String SEARCH_KEY_WORD = "lietuvos";

	private static final Logger log = LoggerFactory
			.getLogger(WordSpottingServiceImplExp.class);

	private WspotJdbcDao wspotDao;

	private static final Ordering<Entry<RecognitionResult, SignalSegment>> order = new RecognitionResultSignalSegmentOrder();

	@Before
	@Override
	public void setUp() throws Exception {
		super.setUp();
		setSearchWord(SEARCH_KEY_WORD);
		setAcceptableSyllables(new String[] { "liet", "tuvos" });
		wspotDao = new WspotJdbcDao();
	}

	@Override
	protected File createRepositoryPathRoot() {
		return 
//				new File("/home/as/tmp/garsynas_2lietuvos/garsynas_wopitch");
//				new File("/home/as/tmp/garsynas_2lietuvos/garsynas_pitch");
				new File("/home/as/tmp/garsynas_2lietuvos/garsynas_dynlen");
	}

	@Override
	protected File createWavFile(File aRepositoryPathRoot) {
		String internalPath = 
				"TEST/";
//				"TRAIN/";
		String fileName = internalPath + 
				"041-30_1.wav"
//				"RZg0819_18_41b-30_1.wav"
//		 "lietuvos_mbr_test-30_1.wav"
		;
		return new File(aRepositoryPathRoot, fileName);
	}
	@Ignore
	@Test
	@Category(SlowTests.class)
	@Override
	public void test_wordSpotting() throws MalformedURLException {
		// given

		WordSpotResult result = doWordspot(getWavFile());
		String resultsStr = extractResultStr(result.getSegments());

		log.error("Marker =>" + result.getOriginalMarker());
		log.error(getWavFile() + "=>"
				+ order.sortedCopy(result.getSegments().entrySet()));

		// then
		// Assert.assertTrue("read time " + length + ">"+(ended-started), length
		// > ended-started);
		Assert.assertEquals("Recognition", SEARCH_KEY_WORD+";"+SEARCH_KEY_WORD, resultsStr);
		SignalSegment firstSegment = result.getSegments().values().iterator()
				.next();
		Marker firstOriginal = result.getOriginalMarker().get(0);
		Assert.assertEquals("Recognition start", firstOriginal
				.getStart(), firstSegment.getMarker().getStart(), 320D);
		Assert.assertEquals("Recognition length", firstOriginal
				.getLength(), firstSegment.getMarker().getLength(), 200);

	}

	@Test
	@Category(SlowTests.class)
	public void bulkTest() throws MalformedURLException {
		wspotDao.setRecreate(true);
		wspotDao.init();
        log.debug("path: {}", getWavFile().getParentFile().getAbsoluteFile());
		File[] files = getWavFile().getParentFile().listFiles(
				new ExtNameFilter("wav"));
        log.debug("fileSize: {}", files.length);
        int index = 0;
		int foundSize = 0;
		for (File file : files) {
			// if(!file.getName().contains(
			// "RZd0706_18_06-30_1.wav"
			// )){
			// continue;
			// }
    		Long start = System.currentTimeMillis();
         	log.debug("start {}: {}",index,  file);
			WordSpotResult result = doWordspot(file);
			wspotDao.save(result);
			foundSize += result.getSegments().size();
			// String resultsStr = extractResultStr(result.getSegments());
            log.debug("Marker => {}", result.getOriginalMarker());
            log.debug("{} => {}",getWavFile(), order.sortedCopy(result.getSegments().entrySet()));
            log.debug("{} => {}",getWavFile(), order.sortedCopy(result.getSegments().entrySet()));
            log.debug("done {} in {} : {}\n", new Object[]{index, System.currentTimeMillis()-start, file});
            index++;
		}
		// log.error("files =>" + files.length);
        log.debug("foundSize =>{}", foundSize);
		// Assert.assertEquals(0, list.size());
		wspotDao.destroy();
		Assert.assertTrue("One element at least", foundSize > 0);

	}

	public WordSpotResult doWordspot(File aWavFile)
			throws MalformedURLException {
		URL aWavUrl = aWavFile.toURI().toURL();

		WordSpotResult result = new WordSpotResult();
		WordSpottingListenerLogImpl listener = new WordSpottingListenerLogImpl(
				getSearchWord(), getAcceptableSyllables(),
				getRepositoryPathWord().getAbsolutePath());
		listener.setServiceConfig(serviceConfig);
		Long length = AudioManagerFactory.createAudioManager()
				.findLengthInMils(aWavUrl);
		// various experiments uses various lietuvos trasnsciption
		Collection<Marker> keywordMarkers = findKeyword(aWavFile);
		result.setAudioLength(length);
		result.getOriginalMarker().addAll(keywordMarkers);
		result.setFileName(aWavFile.getName());

		result.setExperimentStarted(System.currentTimeMillis());
		wordSpottingServiceImpl.wordSpotting(aWavUrl, listener);
		result.setExperimentEnded(System.currentTimeMillis());
		Map<RecognitionResult, SignalSegment> segments = listener
				.getWordSegments();
		result.setSegments(segments);
		return result;

	}

	private Collection<Marker> findKeyword(File aWavFile) {
		MarkerSetHolder markerSetHolder = findMarkerSetHolderByWav(aWavFile);
		Collection<Marker> markerList = getMarkerService().findAllByLabel(markerSetHolder, "-l'-ie-t-|-u-v-oo-s");
		if (markerList == null || markerList.isEmpty()) {
			markerList = getMarkerService().findAllSequenesByLabels(markerSetHolder, "-l-ie-t", "-u-v-o:-s");
		}
		if (markerList == null || markerList.isEmpty()) {
			markerList = getMarkerService().findAllSequenesByLabels(markerSetHolder, "-l-ie-t", "-u-v-oo-s");
		}
		long i = 0;
		for (Marker marker : markerList) {
			marker.setId(i);
			i++;
		}
		return markerList;
	}
}

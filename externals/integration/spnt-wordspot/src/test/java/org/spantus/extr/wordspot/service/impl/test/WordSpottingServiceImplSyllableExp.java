/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.spantus.extr.wordspot.service.impl.test;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
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
import org.spantus.core.junit.SlowTests;
import org.spantus.core.marker.Marker;
import org.spantus.core.marker.MarkerSetHolder;
import org.spantus.core.wav.AudioManagerFactory;
import org.spantus.extr.wordspot.guava.RecognitionResultSignalSegmentOrder;
import org.spantus.extr.wordspot.service.impl.SyllableSpottingListenerLogImpl;
import org.spantus.extr.wordspot.service.impl.WordSpottingListenerLogImpl;
import org.spantus.extr.wordspot.service.impl.test.util.ExtNameFilter;
import org.spantus.extr.wordspot.util.dao.WordSpotResult;
import org.spantus.extr.wordspot.util.dao.WspotJdbcDao;

import com.google.common.collect.Ordering;

/**
 * 
 * @author as
 */
public class WordSpottingServiceImplSyllableExp extends WordSpottingServiceImplTest {

	private static final Logger log = Logger
			.getLogger(WordSpottingServiceImplSyllableExp.class);

	private static final String[] KEY_WORD_SEQUENCE_ARR = new String[]{"liet"};
	private WspotJdbcDao wspotDao;

	private static final Ordering<Entry<RecognitionResult, SignalSegment>> order = new RecognitionResultSignalSegmentOrder();

	@Before
	@Override
	public void setUp() throws Exception {
		super.setUp();
		setAcceptableSyllables(KEY_WORD_SEQUENCE_ARR);
		wspotDao = new WspotJdbcDao();
	}

	@Override
	protected File createRepositoryPathRoot() {
//		String rootPath = "/TEST/"
		// "/tmp/test" //
		// "/home/as/tmp/garsynas.lietuvos-syn-wpitch/TEST/"
		// "/home/as/tmp/garsynas.lietuvos-syn-wopitch//TEST"
		;
		return new File("/home/as/tmp/garsynas.lietuvos-syn-wopitch/");
	}

	@Override
	protected File createWavFile(File aRepositoryPathRoot) {
		String internalPath = "TEST/";
		String fileName = internalPath + "RBg031126_13_31-30_1.wav"
		// "lietuvos_mbr_test-30_1.wav"
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
		Assert.assertEquals("Recognition", KEY_WORD_SEQUENCE_ARR[0], resultsStr);
		SignalSegment firstSegment = result.getSegments().values().iterator()
				.next();
		Assert.assertEquals("Recognition start", result.getOriginalMarker()
				.getStart(), firstSegment.getMarker().getStart(), 320D);
		Assert.assertEquals("Recognition length", result.getOriginalMarker()
				.getLength(), firstSegment.getMarker().getLength(), 150);

	}
	

	@Test
	@Category(SlowTests.class)
	public void bulkTest() throws MalformedURLException {
		wspotDao.setRecreate(true);
		wspotDao.init();

		File[] files = getWavFile().getParentFile().listFiles(
				new ExtNameFilter("wav"));
		int foundSize = 0;
		for (File file : files) {
			// if(!file.getName().contains(
			// "RZd0706_18_06-30_1.wav"
			// )){
			// continue;
			// }
			log.debug("start: " + file);
			WordSpotResult result = doWordspot(file);
			wspotDao.save(result);
			foundSize += result.getSegments().size();
			// String resultsStr = extractResultStr(result.getSegments());
			log.debug("done: " + file);
			log.error("Marker =>" + result.getOriginalMarker());
			log.error(getWavFile() + "=>"
					+ order.sortedCopy(result.getSegments().entrySet()));
		}
		// log.error("files =>" + files.length);
		log.error("foundSize =>" + foundSize);
		// Assert.assertEquals(0, list.size());
		wspotDao.destroy();
		Assert.assertTrue("One element at least", foundSize > 0);

	}

	public WordSpotResult doWordspot(File aWavFile)
			throws MalformedURLException {
		URL aWavUrl = aWavFile.toURI().toURL();

		WordSpotResult result = new WordSpotResult();
		SyllableSpottingListenerLogImpl listener = new SyllableSpottingListenerLogImpl(
				getSearchWord(), getAcceptableSyllables(),
				getRepositoryPathWord().getAbsolutePath());
		listener.setServiceConfig(serviceConfig);
		Long length = AudioManagerFactory.createAudioManager()
				.findLengthInMils(aWavUrl);
		// various experiments uses various lietuvos trasnsciption
		Marker keywordMarker = findKeyword(aWavFile);
		result.setAudioLength(length);
		result.setOriginalMarker(keywordMarker);
		result.setFileName(aWavFile.getName());

		// when
		result.setExperimentStarted(System.currentTimeMillis());
		wordSpottingServiceImpl.wordSpotting(aWavUrl, listener);
		result.setExperimentEnded(System.currentTimeMillis());
		Map<RecognitionResult, SignalSegment> segments = listener
				.getWordSegments();
		result.setSegments(segments);
		return result;

	}

	private Marker findKeyword(File aWavFile) {
		MarkerSetHolder markerSetHolder = findMarkerSetHolderByWav(aWavFile);
		String keyMakerLabel = KEY_WORD_SEQUENCE_ARR[0];
		Marker marker = getMarkerService().findFirstByLabel(markerSetHolder, keyMakerLabel);
		marker.setLabel(keyMakerLabel);
		return marker;
	}
}

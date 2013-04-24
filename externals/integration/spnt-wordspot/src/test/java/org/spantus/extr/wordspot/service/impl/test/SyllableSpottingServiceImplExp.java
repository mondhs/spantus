/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.spantus.extr.wordspot.service.impl.test;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
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
import org.spantus.extr.wordspot.guava.MarkerOrder;
import org.spantus.extr.wordspot.service.impl.SyllableSpottingListenerLogImpl;
import org.spantus.extr.wordspot.service.impl.test.util.ExtNameFilter;
import org.spantus.extr.wordspot.util.dao.WordSpotResult;
import org.spantus.extr.wordspot.util.dao.WspotJdbcDao;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;

/**
 * 
 * @author as
 */
public class SyllableSpottingServiceImplExp extends WordSpottingServiceImplTest {

	private static final Logger log = LoggerFactory
			.getLogger(SyllableSpottingServiceImplExp.class);

	private final static Map<String, String> keyWordMap = ImmutableMap.of(
			"liet", "liet", "uvoos", "tuvos");
	private WspotJdbcDao wspotDao;

	@Before
	@Override
	public void setUp() throws Exception {
		super.setUp();
		setAcceptableSyllables(keyWordMap.values().toArray(
				new String[keyWordMap.size()]));
		wspotDao = new WspotJdbcDao();
	}

	@Override
	protected File createRepositoryPathRoot() {
		return 
//				new File("/home/as/tmp/garsynas.lietuvos");
				new File("/home/as/tmp/garsynas_2lietuvos/garsynas_wopitch");
//				new File("/home/as/tmp/garsynas_2lietuvos/garsynas_dynlen");
//		new File("/home/as/tmp/garsynas_2lietuvos/garsynas_pitch");
		
	}

	@Override
	protected File createWavFile(File aRepositoryPathRoot) {
		String internalPath = 
				"TEST/";
//				"";
		String fileName = internalPath +
//				"RZd0826_18_12c.wav"
				"002-30_1.wav"
//		 "TRAIN/lietuvos_mbr_test-30_1.wav"
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
		Assert.assertEquals("Recognition", "liet;tuvos;", resultsStr);


	}
//	@Ignore
	@Test
	@Category(SlowTests.class)
	public void bulkTest() throws MalformedURLException {
		wspotDao.setRecreate(true);
		wspotDao.init();
		log.debug("path: {}", getWavFile().getParentFile().getAbsoluteFile());
		File[] files = getWavFile().getParentFile().listFiles(
				new ExtNameFilter("wav"));
		log.debug("fileSize: {}", files.length);
		int foundSize = 0;
        int index = 0;
		for (File file : files) {
			// if (!file.getName().contains("RZd0706_18_06-30_1.wav")) {
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
		SyllableSpottingListenerLogImpl listener = new SyllableSpottingListenerLogImpl(
				getSearchWord(), getAcceptableSyllables(),
				getRepositoryPathWord().getAbsolutePath());
		listener.setServiceConfig(serviceConfig);
		Long length = AudioManagerFactory.createAudioManager()
				.findLengthInMils(aWavUrl);
		List<Marker> originalMarker = findOriginal(aWavFile) ;
		result.setOriginalMarker(originalMarker);
		
		//
		result.setAudioLength(length);
		//
		result.setFileName(aWavFile.getName());

		// when
		result.setExperimentStarted(System.currentTimeMillis());
		wordSpottingServiceImpl.wordSpotting(aWavUrl, listener);
		result.setExperimentEnded(System.currentTimeMillis());
		result.setOperationCount(wordSpottingServiceImpl.getOperationCount());
		Map<RecognitionResult, SignalSegment> segments = listener
				.getWordSegments();
		result.setSegments(segments);
		return result;

	}

	
	private List<Marker> findOriginal(File aWavFile) {
		List<Marker> originalMarker = Lists.newArrayList();
		// various experiments uses various lietuvos trasnsciption
		for (Entry<String, String> element : keyWordMap.entrySet()) {
			Collection<Marker> keywordMarkers = findKeywordSegment(
					element.getValue(), aWavFile, element.getKey());
			originalMarker.addAll(keywordMarkers);
		}
		originalMarker = new MarkerOrder().sortedCopy(originalMarker);
		long i = 0;
		for (Marker marker : originalMarker) {
			String markerLabel = marker.getLabel();
			markerLabel = markerLabel.replaceAll("[\'|-]", "");
			markerLabel = markerLabel.replaceAll("(o:)", "oo");
			if(keyWordMap.get(markerLabel) != null){
				marker.setLabel(keyWordMap.get(markerLabel));
			}
			marker.setId(i);
			i++;
		}
		return originalMarker;
	}

	protected Collection<Marker> findKeywordSegment(String keyWordValue,
			File aWavFile, String keyWordCode) {
		MarkerSetHolder markers = findMarkerSetHolderByWav(aWavFile);
		Collection<Marker> markerList = getMarkerService().findAllByLabel(markers,
				keyWordCode);
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

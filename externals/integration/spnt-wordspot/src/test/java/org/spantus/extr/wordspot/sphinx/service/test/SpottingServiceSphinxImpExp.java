package org.spantus.extr.wordspot.sphinx.service.test;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;

import junit.framework.Assert;

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
import org.spantus.core.marker.MarkerSet;
import org.spantus.core.marker.MarkerSetHolder;
import org.spantus.core.marker.MarkerSetHolder.MarkerSetHolderEnum;
import org.spantus.core.wav.AudioManagerFactory;
import org.spantus.externals.recognition.sphinx.impl.SphinxRecognitionServiceImpl;
import org.spantus.extr.wordspot.guava.RecognitionResultSignalSegmentOrder;
import org.spantus.extr.wordspot.service.impl.test.AbstractSegmentExtractorTest;
import org.spantus.extr.wordspot.service.impl.test.util.ExtNameFilter;
import org.spantus.extr.wordspot.util.dao.WordSpotResult;
import org.spantus.extr.wordspot.util.dao.WspotJdbcDao;

import com.google.common.collect.Ordering;

public class SpottingServiceSphinxImpExp extends AbstractSegmentExtractorTest {
	private static final Logger LOG = LoggerFactory.getLogger(SpottingServiceSphinxImpExp.class);
	private SphinxRecognitionServiceImpl shinxRecognitionServiceImpl;
	private WspotJdbcDao wspotDao;
	private static final String SEARCH_KEY_WORD = "LIETUVOS";
	private static final Ordering<Entry<RecognitionResult, SignalSegment>> order = new RecognitionResultSignalSegmentOrder();
	
	@Before
	public void onSetup() {
		shinxRecognitionServiceImpl = new SphinxRecognitionServiceImpl();
		shinxRecognitionServiceImpl.addKeyword(SEARCH_KEY_WORD);
		wspotDao = new WspotJdbcDao();
	}
	
	@Override
	protected File createRepositoryPathRoot() {
		return 
				new File("/home/as/src/garsynai/darbiniai/garsynas_2lietuvos/garsynas_wopitch");
//				new File("/home/as/src/garsynai/darbiniai/garsynas_2lietuvos/garsynas_pitch");
//				new File("/home/as/src/garsynai/darbiniai/garsynas_2lietuvos/garsynas_dynlen");
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
	public void testRecognise() throws UnsupportedAudioFileException, IOException {
		//given
		File aWavFile = new File("/home/as/src/garsynai/darbiniai/garsynas_2lietuvos/samples/000-30_1.wav");
		//when
		WordSpotResult result = doWordspot(aWavFile);
		//then
		
//		MarkerSet phoneMarkerSet = markerSetHolder.getMarkerSets().get(MarkerSetHolderEnum.phone.name());
		LOG.debug("foundSegment {}", result.getSegments());
		Assert.assertEquals("word number",2, result.getSegments().size(),  0);
		
	}
	
	
	@Test
	@Category(SlowTests.class)
	public void bulkTest() throws UnsupportedAudioFileException, IOException {
		wspotDao.setRecreate(true);
		wspotDao.init();
		LOG.debug("path: {}", getWavFile().getParentFile().getAbsoluteFile());
		File[] files = getWavFile().getParentFile().listFiles(
				new ExtNameFilter("wav"));
		LOG.debug("fileSize: {}", files.length);
        int index = 0;
		int foundSize = 0;
		for (File file : files) {
//			 if(!file.getName().contains(
//					 "076-30_1.wav"
//			)){
//				continue;
//			}
    		Long start = System.currentTimeMillis();
    		LOG.debug("start {}: {}",index,  file);
			WordSpotResult result = doWordspot(file);
			wspotDao.save(result);
			foundSize += result.getSegments().size();
			// String resultsStr = extractResultStr(result.getSegments());
			LOG.debug("Marker => {}", result.getOriginalMarker());
			LOG.debug("{} => {}",getWavFile(), order.sortedCopy(result.getSegments().entrySet()));
			LOG.debug("{} => {}",getWavFile(), order.sortedCopy(result.getSegments().entrySet()));
			LOG.debug("done {} in {} : {}\n", new Object[]{index, System.currentTimeMillis()-start, file});
            index++;
		}
		// log.error("files =>" + files.length);
		LOG.debug("foundSize =>{}", foundSize);
		// Assert.assertEquals(0, list.size());
		wspotDao.destroy();
		Assert.assertTrue("One element at least", foundSize > 0);

	}
	/**
	 * 
	 * @param aWavFile
	 * @return
	 * @throws UnsupportedAudioFileException
	 * @throws IOException
	 */
	private WordSpotResult doWordspot(File aWavFile) throws UnsupportedAudioFileException, IOException{
		WordSpotResult result = new WordSpotResult();
		URL aWavUrl = aWavFile.toURI().toURL();
		AudioInputStream ais = AudioSystem.getAudioInputStream(aWavUrl);
		Collection<Marker> keywordMarkers = findKeyword(aWavFile);
		Long length = AudioManagerFactory.createAudioManager()
				.findLengthInMils(aWavUrl);
		result.setAudioLength(length);
		result.getOriginalMarker().addAll(keywordMarkers);
		result.setFileName(aWavFile.getName());
		result.setExperimentStarted(System.currentTimeMillis());

		//actual spotting
		MarkerSetHolder markerSetHolder = shinxRecognitionServiceImpl.recognizeOffline(ais, aWavUrl.getFile());
		final Map<RecognitionResult, SignalSegment> segments = new LinkedHashMap<>();
		MarkerSet wordMarkerSet = markerSetHolder.getMarkerSets().get(MarkerSetHolderEnum.word.name());
		int id = 0;
		for (Marker marker : wordMarkerSet) {
			SignalSegment newSegment = new SignalSegment(marker);
			newSegment.setId(""+(id++));
			newSegment.setName(marker.getLabel());
			RecognitionResult recognitionResult = new RecognitionResult();
			recognitionResult.setInfo(newSegment);
			segments.put(recognitionResult, newSegment);
		}
		result.setExperimentEnded(System.currentTimeMillis());
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

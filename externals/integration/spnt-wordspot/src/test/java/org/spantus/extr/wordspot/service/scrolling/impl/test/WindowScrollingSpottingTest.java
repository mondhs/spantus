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
import java.util.List;

import org.junit.Test;
import org.spantus.core.beans.RecognitionResult;
import org.spantus.core.beans.SignalSegment;
import org.spantus.core.extractor.IExtractorInputReader;
import org.spantus.core.marker.Marker;
import org.spantus.core.marker.MarkerSetHolder;
import org.spantus.core.service.CorpusService;
import org.spantus.externals.recognition.services.RecognitionServiceFactory;
import org.spantus.extr.wordspot.domain.SegmentExtractorServiceConfig;
import org.spantus.extr.wordspot.service.SpottingListener;
import org.spantus.extr.wordspot.service.impl.test.AbstractSegmentExtractorTest;
import org.spantus.extr.wordspot.service.scrolling.impl.ScrollingFactory;
import org.spantus.extr.wordspot.service.scrolling.impl.WindowScrollingSpottingServiceImpl;
import org.spantus.extractor.impl.ExtractorEnum;
import org.spantus.work.services.WorkExtractorReaderService;
import org.spantus.work.services.WorkServiceFactory;

/**
 * 
 * @author as
 */
public class WindowScrollingSpottingTest extends AbstractSegmentExtractorTest {

	private WindowScrollingSpottingServiceImpl spottingService;

	@Override
	public void setUp() throws Exception {
		// setRepositoryPath(new File(getRepositoryPathRoot(),"CORPUS/word"));
		super.setUp();
		SegmentExtractorServiceConfig config = getSegmentExtractorService()
				.getServiceConfig();
		WorkExtractorReaderService extractorReaderService = WorkServiceFactory
				.createExtractorReaderService(config.getWindowLength(),
						config.getOverlapInPerc());
		CorpusService corpusService = RecognitionServiceFactory
				.createCorpusServicePartialSearch(getRepositoryPath()
						.getAbsolutePath(), getSegmentExtractorService()
						.getServiceConfig().getSyllableDtwRadius(),
						ExtractorEnum.MFCC_EXTRACTOR.name());

		spottingService = ScrollingFactory.createWindowScrollingSpottingServiceImpl(getRepositoryPath(), corpusService, extractorReaderService);
	}

	@Test
	public void testWordSpotting() throws MalformedURLException {
		// given
		URL aWavUrl = getWavFile().toURI().toURL();
		spottingService.addKeySegment(findKeywordSegment("skirt", getWavFile(), "skirt"));
		spottingService.addKeySegment(findKeywordSegment("zodz", getWavFile(), "zodz"));
		final List<Marker> foundSegment = new ArrayList<Marker>();
		// when
		spottingService.wordSpotting(aWavUrl, new SpottingListener() {
			@Override
			public String foundSegment(String sourceId,
					SignalSegment newSegment,
					List<RecognitionResult> recognitionResults) {
				foundSegment.add(newSegment.getMarker());
				return newSegment.getMarker().getLabel();
			}
		});
		// then
		assertEquals(2,foundSegment.size(),0);
		for (int i = 0; i < foundSegment.size(); i++) {
			assertEquals("start of found key marker same as matched",
					spottingService.getKeySegmentList().get(i).getMarker().getStart(),
					foundSegment.get(i).getStart(), 150L);
		}

	}

	@Test
	public void testExactPlaceWordSpotting() throws MalformedURLException {
		// given
		URL aWavUrl = getWavFile().toURI().toURL();
		Marker aMarker = new Marker(922L, 201L, "skirt");

		// when
		IExtractorInputReader reader = spottingService.createReader(aWavUrl);
		SignalSegment recalculatedFeatures = spottingService
				.recalculateFeatures(reader, aMarker);
		List<RecognitionResult> matchedResults = spottingService
				.match(recalculatedFeatures);

		// then

		assertNotNull(matchedResults);
		assertEquals("Results", 5, matchedResults.size());
		RecognitionResult matched = matchedResults.get(0);
		assertEquals(
				"Results",
				0,
				matched.getDetails().getDistances()
						.get(ExtractorEnum.MFCC_EXTRACTOR.name()), 1);

	}


	protected SignalSegment findKeywordSegment(String keyWordValue, File aWavFile, String keyWordCode) {
		MarkerSetHolder markers = findMarkerSetHolderByWav(aWavFile);
		Marker keywordMarker = getMarkerService().findFirstByLabel(markers,keyWordCode);
//    		Marker marker = findKeyword(aWavFile, keyWord);
    	 SignalSegment keySegment = new SignalSegment(new Marker(keywordMarker.getStart(),
    			 keywordMarker.getLength(),
    			 keyWordValue));
        return keySegment;
    }

	public WindowScrollingSpottingServiceImpl getSpottingService() {
		return spottingService;
	}

}

package org.spantus.extr.wordspot.sphinx.service.test;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.spantus.core.beans.RecognitionResult;
import org.spantus.core.beans.SignalSegment;
import org.spantus.core.marker.Marker;
import org.spantus.extr.wordspot.service.SpottingListener;
import org.spantus.extr.wordspot.sphinx.service.SpottingServiceSphinxImp;

public class SpottingServiceSphinxImpTest {
	SpottingServiceSphinxImp spottingServiceSphinxImp;
	
	@Before
	public void setUp() throws Exception {
		spottingServiceSphinxImp = new SpottingServiceSphinxImp();
	}

	@Test
	public void testWordSpotting() throws MalformedURLException {
		// given
		URL aWavUrl =  new File("../../../data/varyk_pirmyn_16k.wav").toURI().toURL();
		final List<Marker> foundSegment = new ArrayList<Marker>();
		// when
		spottingServiceSphinxImp.wordSpotting(aWavUrl, new SpottingListener() {
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
//		for (int i = 0; i < foundSegment.size(); i++) {
//			assertEquals("start of found key marker same as matched",
//					spottingService.getKeySegmentList().get(i).getMarker().getStart(),
//					foundSegment.get(i).getStart(), 150L);
//		}

	}
	
}

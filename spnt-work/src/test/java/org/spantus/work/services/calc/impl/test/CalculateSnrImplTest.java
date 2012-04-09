package org.spantus.work.services.calc.impl.test;

import java.util.ArrayList;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.spantus.core.FrameValues;
import org.spantus.core.extractor.IExtractor;
import org.spantus.core.marker.Marker;
import org.spantus.core.marker.MarkerSet;
import org.spantus.core.test.DummyExtractor;
import org.spantus.core.test.DumyExtractorInputReader;
import org.spantus.work.services.calc.impl.CalculateSnrImpl;

public class CalculateSnrImplTest {

	CalculateSnrImpl calculateSnrImpl;
	
	@Before
	public void setup() {
		calculateSnrImpl = new CalculateSnrImpl();
	}
	
	@Test
	public void testSnr() {
		//given
		MarkerSet segments = newMarkerSet();
		IExtractor iExtractor = newExtractor(segments);
		
		//when
		Double snr = calculateSnrImpl.calculate(iExtractor, segments);
		
		//then
		Assert.assertEquals(41.06, snr);
		
	}

	private IExtractor newExtractor(MarkerSet segments) {
		DummyExtractor dummyExtractor = DumyExtractorInputReader.createExtractor("TEST");
		FrameValues vals = dummyExtractor.getOutputValues();
		int current = 1;
		for (Marker marker : segments.getMarkers()) {
//			if(current == null){
//				for (int i = 0; i < marker.getStart(); i++) {
//					vals.set(vals.toIndex((double) i), 0.1);
//				}
//				current = marker.getEnd();
//			}
//			Double index = vals.get(vals.toIndex((double) marker.getStart()/vals.getSampleRate()));
			for (double i = current; i < marker.getStart(); i++) {
				vals.set(vals.toIndex((double) i/vals.getSampleRate()), 0.1+ (0.0001*i));
			}
			current = marker.getEnd().intValue();
		}
		for (int i = current; i <= vals.toIndex(vals.getTime())+1; i++) {
			vals.set(vals.toIndex((double) i/vals.getSampleRate()), 0.1+ (i/1000));
		}
		return dummyExtractor;
	}

	private MarkerSet newMarkerSet() {
		MarkerSet markerSet = new MarkerSet();
		markerSet.setMarkers(new ArrayList<Marker>());
		markerSet.getMarkers().add(newMarker(10,20));
		markerSet.getMarkers().add(newMarker(30,40));
		return markerSet;
	}

	private Marker newMarker(int start, int end) {
		Marker marker = new Marker();
		marker.setStart((long) start);
		marker.setEnd((long) end);
		return marker;
	}

}

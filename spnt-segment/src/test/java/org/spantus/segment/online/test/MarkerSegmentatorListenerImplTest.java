package org.spantus.segment.online.test;

import java.util.Iterator;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.spantus.core.beans.SignalSegment;
import org.spantus.core.marker.Marker;
import org.spantus.core.threshold.SegmentEvent;
import org.spantus.segment.online.MarkerSegmentatorListenerImpl;

public class MarkerSegmentatorListenerImplTest {

	public final static String FEATURE1 = "FEATURE1";
	public final static String FEATURE2 = "FEATURE2";
	public final static String FEATURE3 = "FEATURE3";
	private MarkerSegmentatorListenerImpl listener;
	
	
	@Before
	public void setUp() throws Exception {
		listener = new MarkerSegmentatorListenerImpl();
		
	}

	@Test
	public void testOneFeature() {
		//given
		listener.registered(FEATURE1);
		SegmentEvent eventStarted1 = newSegmentEvent(FEATURE1, 10L);
		SegmentEvent eventEnded1= newSegmentEvent(FEATURE1, 30L);
		SegmentEvent eventStarted2 = newSegmentEvent(FEATURE1, 50L);
		SegmentEvent eventEnded2 = newSegmentEvent(FEATURE1, 90L);
		//when
		listener.onSegmentStarted(eventStarted1);
		listener.onSegmentEnded(eventEnded1);
		listener.onSegmentStarted(eventStarted2);
		listener.onSegmentEnded(eventEnded2);
		Iterator<SignalSegment> iterator = listener.getSignalSegments().iterator();
		//then
		Assert.assertEquals(2, listener.getSignalSegments().size());
		SignalSegment firstSegment = iterator.next();
		Marker firstMarker = firstSegment.getMarker();
		SignalSegment secondSegment = iterator.next();
		Marker secondMarker = secondSegment.getMarker();
		Assert.assertEquals(10, firstMarker.getStart(),0);
		Assert.assertEquals(20, firstMarker.getLength(),0);
		Assert.assertEquals(50, secondMarker.getStart(),0);
		Assert.assertEquals(40, secondMarker.getLength(),0);
	}
	
	@Test
	public void testThreeFeature() {
		//given
		listener.registered(FEATURE1);
		listener.registered(FEATURE2);
		listener.registered(FEATURE3);
		SegmentEvent[] eventStarted1 = new SegmentEvent[]{
				newSegmentEvent(FEATURE2, 9L),
				newSegmentEvent(FEATURE1, 10L), 
				newSegmentEvent(FEATURE3, 11L)};
		
		SegmentEvent[] eventEnded1 = new SegmentEvent[]{
				newSegmentEvent(FEATURE3, 29L),
				newSegmentEvent(FEATURE1, 30L),
				newSegmentEvent(FEATURE2, 31L)};
		
		SegmentEvent[] eventStarted2 = new SegmentEvent[]{
				newSegmentEvent(FEATURE2, 49L),
				newSegmentEvent(FEATURE1, 50L),
				newSegmentEvent(FEATURE3, 51L)};
		
		SegmentEvent[] eventEnded2 = new SegmentEvent[]{
				newSegmentEvent(FEATURE3, 89L),
				newSegmentEvent(FEATURE1, 90L),
				newSegmentEvent(FEATURE2, 91L)};
		
		//when
		for (SegmentEvent segmentEvent : eventStarted1) {
			listener.onSegmentStarted(segmentEvent);
		}
		for (SegmentEvent segmentEvent : eventEnded1) {
			listener.onSegmentEnded(segmentEvent);
		}
		for (SegmentEvent segmentEvent : eventStarted2) {
			listener.onSegmentStarted(segmentEvent);
		}
		for (SegmentEvent segmentEvent : eventEnded2) {
			listener.onSegmentEnded(segmentEvent);
		}
		
		Iterator<SignalSegment> iterator = listener.getSignalSegments().iterator();
		SignalSegment firstSegment = iterator.next();
		Marker firstMarker = firstSegment.getMarker();
		SignalSegment secondSegment = iterator.next();
		Marker secondMarker = secondSegment.getMarker();
		//then
		Assert.assertEquals(2, listener.getSignalSegments().size());
		Assert.assertEquals(10, firstMarker.getStart(),0);
		Assert.assertEquals(20, firstMarker.getLength(),0);
		Assert.assertEquals(50, secondMarker.getStart(),0);
		Assert.assertEquals(40, secondMarker.getLength(),0);
	}
	

	private SegmentEvent newSegmentEvent(String id, Long time) {
		Marker marker = new Marker();
		Long sample = time;
		Double value = time.doubleValue();
		SegmentEvent event = new SegmentEvent(id, time,
				marker, sample, value, true);
		return event;
	}

}

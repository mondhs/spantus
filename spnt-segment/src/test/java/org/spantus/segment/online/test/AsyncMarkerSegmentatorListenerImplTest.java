package org.spantus.segment.online.test;

import java.util.Iterator;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.spantus.core.FrameValues;
import org.spantus.core.beans.SignalSegment;
import org.spantus.core.marker.Marker;
import org.spantus.core.threshold.SegmentEvent;
import org.spantus.segment.online.AsyncMarkerSegmentatorListenerImpl;
import org.spantus.segment.online.MarkerSegmentatorListenerImpl;

public class AsyncMarkerSegmentatorListenerImplTest {

	public final static String FEATURE1 = "FEATURE1";
	public final static String FEATURE2 = "FEATURE2";
	public final static String FEATURE3 = "FEATURE3";
	private AsyncMarkerSegmentatorListenerImpl listener;
	
	
	@Before
	public void setUp() throws Exception {
		listener = new AsyncMarkerSegmentatorListenerImpl(new MarkerSegmentatorListenerImpl());
		
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
	
	@Test
	public void testThreeFeatures() {
		//given
		listener.registered(FEATURE1);
		listener.registered(FEATURE2);
		listener.registered(FEATURE3);
		SegmentEvent[] eventFeature1 =new SegmentEvent[]{
				newSegmentEvent(FEATURE1, 10L), //start
				newSegmentEvent(FEATURE1, 30L),//end
				newSegmentEvent(FEATURE1, 50L),//start
				newSegmentEvent(FEATURE1, 90L),//end
		};
		SegmentEvent[] eventFeature2 =new SegmentEvent[]{
				newSegmentEvent(FEATURE2, 9L), //start
				newSegmentEvent(FEATURE2, 31L),//end
				newSegmentEvent(FEATURE2, 49L),//start
				newSegmentEvent(FEATURE2, 91L),//end
		};
		SegmentEvent[] eventFeature3 =new SegmentEvent[]{
				newSegmentEvent(FEATURE3, 11L), //start
				newSegmentEvent(FEATURE3, 29L),//end
				newSegmentEvent(FEATURE3, 51L),//start
				newSegmentEvent(FEATURE3, 89L),//end
		};
				
		
		//when
		onSegmentStarted(eventFeature1[0]);
		onSegmentEnded(eventFeature1[1]);
		onSegmentStarted(eventFeature1[2]);
		onSegmentEnded(eventFeature1[3]);
		onSegmentStarted(eventFeature2[0]);
		onSegmentEnded(eventFeature2[1]);
		onSegmentStarted(eventFeature2[2]);
		onSegmentEnded(eventFeature2[3]);
		onSegmentStarted(eventFeature3[0]);
		onSegmentEnded(eventFeature3[1]);
		onSegmentStarted(eventFeature3[2]);
		onSegmentEnded(eventFeature3[3]);
		
		//then
		Assert.assertEquals(2, listener.getSignalSegments().size());
		Iterator<SignalSegment> iterator = listener.getSignalSegments().iterator();
		SignalSegment firstSegment = iterator.next();
		Marker firstMarker = firstSegment.getMarker();
		SignalSegment secondSegment = iterator.next();
		Marker secondMarker = secondSegment.getMarker();
		
		
		Assert.assertEquals(10, firstMarker.getStart(),0);
		Assert.assertEquals(21, firstMarker.getLength(),0);
		Assert.assertEquals(50, secondMarker.getStart(),0);
		Assert.assertEquals(41, secondMarker.getLength(),0);
	}
	

	private void onSegmentEnded(SegmentEvent segmentEvent) {
		SegmentEvent processSegmentEvent = createProcessSegmentEvent(segmentEvent);
		listener.onSegmentProcessed(processSegmentEvent);
		listener.onSegmentEnded(segmentEvent);
	}


	private void onSegmentStarted(SegmentEvent segmentEvent) {
		SegmentEvent processSegmentEvent = createProcessSegmentEvent(segmentEvent);
		listener.onSegmentStarted(segmentEvent);
		listener.onSegmentProcessed(processSegmentEvent);
	}
	
	private SegmentEvent createProcessSegmentEvent(SegmentEvent segmentEvent) {
		SegmentEvent processSegmentEvent = segmentEvent.clone();
		processSegmentEvent.setOutputValues(new FrameValues());
		processSegmentEvent.getOutputValues().setSampleRate(100D);
		processSegmentEvent.setWindowValues(new FrameValues());
		processSegmentEvent.getWindowValues().setSampleRate(100D);
		processSegmentEvent.getWindowValues().add(segmentEvent.getValue());
		processSegmentEvent.getWindowValues().add(segmentEvent.getValue());
		processSegmentEvent.getWindowValues().add(segmentEvent.getValue());
		return processSegmentEvent;
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

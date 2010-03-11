package org.spantus.core.threshold.test;

import junit.framework.Assert;

import org.spantus.core.FrameValues;
import org.spantus.core.marker.MarkerSet;
import org.spantus.core.threshold.ExtremeOnlineClassifier;

public class ExtremeOnlineClassifierTest {

	@org.junit.Test
	public void testOnline() throws Exception {
		MarkerSet markerSet = feedData(ExtremeClassifierTest.empty);
		Assert.assertEquals(0, markerSet.getMarkers().size());
		markerSet = feedData(ExtremeClassifierTest.singleMax);
		Assert.assertEquals(1, markerSet.getMarkers().size());

//		assertEquals(3, extremes.size());
//		assertMinState(1, extremes);
//		assertMaxState(4, extremes);
//		assertMinState(7, extremes);
//		extremes = extremeThresholdService
//				.extractExtremes(createExtremeCtx(doubleMax));
//		assertEquals(5, extremes.size());
//		assertMinState(1, extremes);
//		assertMaxState(4, extremes);
//		assertMinState(7, extremes);
//		assertMaxState(11, extremes);
//		assertMinState(14, extremes);

	}

	protected MarkerSet feedData(Float[] data) {
		ExtremeOnlineClassifier classifier = new ExtremeOnlineClassifier();
		FrameValues values = new FrameValues();
		for (Float windowValue : data) {
			values.add(windowValue);
		}
		classifier.afterCalculated(0L, values);
		return classifier.getMarkSet();
	}

}

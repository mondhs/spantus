package org.spantus.core.threshold;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.spantus.core.FrameValues;
import org.spantus.core.marker.Marker;
import org.spantus.core.marker.MarkerSet;

public class ExtremeOfflineClassifier extends AbstractThreshold {

//	 private Logger log = Logger.getLogger(ExtremeOfflineClassifier.class);

	private ExtremeClassifierServiceImpl extremeThresholdService = new ExtremeClassifierServiceImpl();

	@Override
	public void flush() {
		super.flush();
		getThresholdValues().clear();
		ExtremeOfflineCtx extremeCtx = extremeThresholdService.calculateSegments(getOutputValues());
		setMarkSet(extremeCtx.getMarkerSet());
		getThresholdValues().addAll(refreshThreasholdInfo(getMarkSet(), getOutputValues()));

	}
	
	@Override
	public boolean isSignalState(Float value) {
		return false;
	}

	/**
	 * 
	 * @param markerSet
	 */
	public static FrameValues refreshThreasholdInfo(MarkerSet markerSet, FrameValues values) {

		Map<Integer,Float> changePoints = new HashMap<Integer,Float>();
		
		FrameValues threasholds = new FrameValues();
		threasholds.setSampleRate(values.getSampleRate());
		

		for (Marker marker : markerSet.getMarkers()) {
			int start = marker.getExtractionData().getStartSampleNum()
					.intValue();
			int end = start
					+ marker.getExtractionData().getLengthSampleNum()
							.intValue();
			changePoints.put(start,1000F);
			changePoints.put(end,1000F);
//			log.debug("[refreshThreasholdInfo] marker: {0}", marker);

		}

		Iterator<Float> valIter = values.iterator();
		Float minValue= null;
		for (int index = 0; index < values.size(); index++) {
			Float val = valIter.next();
			minValue = minValue == null?val:minValue;
			minValue = Math.min(minValue, val);
			if (changePoints.containsKey(index)) {
				threasholds.add(val);
			} else {
				threasholds.add(minValue);
			}
		}
		return threasholds;
	}

	@Override
	public Float calculateThreshold(Float windowValue) {
		return null;
	}

}

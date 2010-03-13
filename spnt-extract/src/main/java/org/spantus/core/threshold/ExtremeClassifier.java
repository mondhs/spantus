package org.spantus.core.threshold;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.spantus.core.FrameValues;
import org.spantus.core.marker.Marker;
import org.spantus.core.marker.MarkerSet;
import org.spantus.logger.Logger;

public class ExtremeClassifier extends AbstractThreshold {

	 private Logger log = Logger.getLogger(ExtremeClassifier.class);

	private ExtremeClassifierServiceImpl extremeThresholdService = new ExtremeClassifierServiceImpl();

	@Override
	public void flush() {
		super.flush();
		getThresholdValues().clear();
		ExtremeOfflineCtx extremeCtx = extremeThresholdService.calculateSegments(getOutputValues());
		setMarkSet(extremeCtx.getMarkerSet());
		refreshThreasholdInfo(getMarkSet());

	}
	
	@Override
	public boolean isSignalState(Float value) {
		return false;
	}

	/**
	 * 
	 * @param markerSet
	 */
	public void refreshThreasholdInfo(MarkerSet markerSet) {

		Map<Integer,Float> changePoints = new HashMap<Integer,Float>();
		
		FrameValues threashoValues = getThresholdValues();

		for (Marker marker : markerSet.getMarkers()) {
			int start = marker.getExtractionData().getStartSampleNum()
					.intValue();
			int end = start
					+ marker.getExtractionData().getLengthSampleNum()
							.intValue();
			changePoints.put(start,1000F);
			changePoints.put(end,1000F);
			log.debug("[refreshThreasholdInfo] marker: {0}", marker);

		}

		Iterator<Float> valIter = getOutputValues().iterator();
		Float minValue= null;
		for (int index = 0; index < getOutputValues().size(); index++) {
			Float val = valIter.next();
			minValue = minValue == null?val:minValue;
			minValue = Math.min(minValue, val);
			if (changePoints.containsKey(index)) {
				threashoValues.add(val);
			} else {
				threashoValues.add(minValue);
			}
		}
	}

	@Override
	public Float calculateThreshold(Float windowValue) {
		return null;
	}

}

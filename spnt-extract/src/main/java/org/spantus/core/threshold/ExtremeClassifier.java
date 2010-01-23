package org.spantus.core.threshold;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.spantus.core.FrameValues;
import org.spantus.core.marker.Marker;
import org.spantus.core.marker.MarkerSet;

public class ExtremeClassifier extends AbstractClassifier {

	// private Logger log = Logger.getLogger(ExtremeThreshold.class);

	private ExtremeClassifierServiceImpl extremeThresholdService = new ExtremeClassifierServiceImpl();
	private MarkerSet markerSet = null;

	@Override
	public void flush() {
		super.flush();
		getThresholdValues().clear();
		ExtremeCtx extremeCtx = extremeThresholdService.calculateSegments(getOutputValues());
		markerSet = extremeCtx.getMarkerSet();
		refreshThreasholdInfo(markerSet);

	}
	/**
	 * 
	 * @param markerSet
	 */
	public void refreshThreasholdInfo(MarkerSet markerSet) {

		Set<Integer> maximas = new HashSet<Integer>();
		Map<Integer,Float> changePoints = new HashMap<Integer,Float>();
		
		FrameValues threashoValues = getThresholdValues();

		// int entryIndex = 0;
		for (Marker marker : markerSet.getMarkers()) {
			// entryIndex++;
			// if (entryIndex % 2 == 0)
			// continue;
			int start = marker.getExtractionData().getStartSampleNum()
					.intValue();
			int end = start
					+ marker.getExtractionData().getLengthSampleNum()
							.intValue();
			for (int i = start+1; i < end+1; i++) {
				maximas.add(i);
			}
			changePoints.put(start,1000F);
			changePoints.put(end,1000F);

		}

		Iterator<Float> valIter = getOutputValues().iterator();
		for (int index = 0; index < getOutputValues().size(); index++) {
			Float val = valIter.next();
//			if (maximas.contains(index)) {
//				extremesStates.add(1F);
//			} else {
//				extremesStates.add(0F);
//			}
			if (changePoints.containsKey(index)) {
				threashoValues.add(val);
			} else {
				threashoValues.add(0F);
			}
		}
	}

	@Override
	public Float calculateThreshold(Float windowValue) {
		return null;
	}

	public MarkerSet getMarkerSet() {
		return markerSet;
	}

}

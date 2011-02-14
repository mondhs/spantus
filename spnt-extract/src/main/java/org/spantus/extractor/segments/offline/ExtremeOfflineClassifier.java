package org.spantus.extractor.segments.offline;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.spantus.core.FrameValues;
import org.spantus.core.marker.Marker;
import org.spantus.core.marker.MarkerSet;
import org.spantus.core.threshold.AbstractThreshold;
import org.spantus.logger.Logger;

public class ExtremeOfflineClassifier extends AbstractThreshold {

	 @SuppressWarnings("unused")
	private static Logger log = Logger.getLogger(ExtremeOfflineClassifier.class);

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
		

		for (Iterator<Marker> markerIterator = markerSet.getMarkers().iterator(); markerIterator.hasNext();) {
			Marker iMarker = markerIterator.next();
			int start = iMarker.getExtractionData().getStartSampleNum()
					.intValue();
			int end = start
					+ iMarker.getExtractionData().getLengthSampleNum()
							.intValue();
			changePoints.put(start,1000F);
			changePoints.put(end,1000F);
//			log.debug("[refreshThreasholdInfo] marker: {0}", marker);

		}

//		Iterator<Float> valIter = values.iterator();
		for (int index = 0; index < values.size(); index++) {
//			Float val = valIter.next();
//			Float setVal = val * .1F;
//			if (!changePoints.containsKey(index)) {
//				setVal = values.getMinValue();
//				setVal = val * .1F;
//			}
			threasholds.add(values.getMinValue());
		}
		return threasholds;
	}

	@Override
	public Float calculateThreshold(Float windowValue) {
		return null;
	}

}

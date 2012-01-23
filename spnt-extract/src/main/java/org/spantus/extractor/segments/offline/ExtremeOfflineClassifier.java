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
	public boolean isSignalState(Double value) {
		return false;
	}

	/**
	 * 
	 * @param markerSet
	 */
	public static FrameValues refreshThreasholdInfo(MarkerSet markerSet, FrameValues values) {

		Map<Integer,Double> changePoints = new HashMap<Integer,Double>();
		
		FrameValues threasholds = new FrameValues();
		threasholds.setSampleRate(values.getSampleRate());
		

		for (Iterator<Marker> markerIterator = markerSet.getMarkers().iterator(); markerIterator.hasNext();) {
			Marker iMarker = markerIterator.next();
			int start = iMarker.getExtractionData().getStartSampleNum()
					.intValue();
			int end = start
					+ iMarker.getExtractionData().getLengthSampleNum()
							.intValue();
			changePoints.put(start,1000D);
			changePoints.put(end,1000D);
//			log.debug("[refreshThreasholdInfo] marker: {0}", marker);

		}

//		Iterator<Double> valIter = values.iterator();
		for (int index = 0; index < values.size(); index++) {
//			Double val = valIter.next();
//			Double setVal = val * .1F;
//			if (!changePoints.containsKey(index)) {
//				setVal = values.getMinValue();
//				setVal = val * .1F;
//			}
			threasholds.add(values.getMinValue());
		}
		threasholds.setSampleRate(values.getSampleRate());
		return threasholds;
	}

	@Override
	public Double calculateThreshold(Double windowValue) {
		return null;
	}

}

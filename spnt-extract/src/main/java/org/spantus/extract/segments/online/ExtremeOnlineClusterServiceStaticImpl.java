package org.spantus.extract.segments.online;

import org.spantus.core.threshold.ExtremeSegment;
import org.spantus.logger.Logger;

public class ExtremeOnlineClusterServiceStaticImpl 
	extends ExtremeOnlineClusterServiceSimpleImpl {
	Logger log = Logger.getLogger(ExtremeOnlineClusterServiceStaticImpl.class);

	/**
	 * 
	 */
	public String getClassName(ExtremeSegment segment,
			ExtremeSegmentsOnlineCtx ctx) {
		Double area = segment.getCalculatedArea();
		Long length = segment.getCalculatedLength();
		Integer peaks = segment.getPeakEntries().size();

		// if (0 <= area && area < 66000) {
		// return "1";
		// } else if (66000 < area && area < 155000) {
		// return "1";
		// } else if (155000 < area && area < 1650000) {
		// return "2";
		// }
		
		float noiseLimit = 
//			66000
			3900
//			500
			;
		float highLimit = 155000;
		if (0 <= area && area < noiseLimit) {
			return "0";
		} else if (noiseLimit < area && area < highLimit) {
			return "1";
		} else if (highLimit < area && area < 1650000) {
			return "2";
		}

		SegmentInnerData innerData = new SegmentInnerData(peaks, area, length);
		throw new IllegalArgumentException("not impl: " + innerData);
	}


}

package org.spantus.extractor.segments.online.cluster;

import org.spantus.extractor.segments.offline.ExtremeSegment;
import org.spantus.extractor.segments.online.ExtremeSegmentsOnlineCtx;
import org.spantus.logger.Logger;

public class ExtremeOnlineClusterServiceStaticImpl 
	extends ExtremeOnlineClusterServiceSimpleImpl {
	Logger log = Logger.getLogger(ExtremeOnlineClusterServiceStaticImpl.class);

	/**
	 * 
	 */
	@SuppressWarnings("unused")
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
		
		if(length<40){
			return "0";
		}
		
		Double pointAreaZero = -.03*length.doubleValue()+1000;

		if(area<pointAreaZero){
			return "0";
		}
		
		float noiseLimit = 
//			66000
//			3900
//			1500
			50
			;
		float highLimit = 
//			155000
			4000
		;
		float upperLimit = 
			Float.MAX_VALUE
//			1650000
			;
//		if (0 <= area && area < noiseLimit) {
//			return "0";
//		} else if (noiseLimit < area && area < highLimit) {
//			return "1";
//		} else if (highLimit < area && area < upperLimit) {
//			return "2";
//		}
		Double pointArea = -.01*length.doubleValue()+20000;
		if(area<pointArea){
			return "1";
		}else {
			return "2";
		}

//		SegmentInnerData innerData = new SegmentInnerData(peaks, area, length);
//		throw new IllegalArgumentException("not impl: " + innerData);
	}


}

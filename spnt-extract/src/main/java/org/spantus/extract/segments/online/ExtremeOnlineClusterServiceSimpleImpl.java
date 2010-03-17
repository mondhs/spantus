package org.spantus.extract.segments.online;

import org.spantus.core.threshold.ExtremeSegment;
import org.spantus.logger.Logger;

public class ExtremeOnlineClusterServiceSimpleImpl implements ExtremeOnlineClusterService{

	Logger log = Logger.getLogger(ExtremeOnlineClusterServiceSimpleImpl.class);
	
	public String getClassName(ExtremeSegment segment,
			ExtremeSegmentsOnlineCtx ctx) {
		return "1";
	}

	public SegmentInnerData learn(ExtremeSegment segment,
			ExtremeSegmentsOnlineCtx ctx) {
		Double area = segment.getCalculatedArea();
		Long length = segment.getCalculatedLength();
		Integer peaks = segment.getPeakEntries().size();
		SegmentInnerData innerData = new SegmentInnerData(peaks, area, length);
		String className = getClassName(segment, ctx);
		log.debug("[learn]innerData: {0}; className: {1}", innerData,
						className);
		return innerData;
	}

}

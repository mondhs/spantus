package org.spantus.extractor.segments.online.cluster;

import org.spantus.extractor.segments.offline.ExtremeSegment;
import org.spantus.extractor.segments.online.ExtremeSegmentsOnlineCtx;
import org.spantus.extractor.segments.online.SegmentFeatureData;
import org.spantus.logger.Logger;

public class ExtremeOnlineClusterServiceSimpleImpl implements ExtremeOnlineClusterService{

	Logger log = Logger.getLogger(ExtremeOnlineClusterServiceSimpleImpl.class);
	
	public String getClassName(ExtremeSegment segment,
			ExtremeSegmentsOnlineCtx ctx) {
		return "1";
	}
	
	public SegmentFeatureData learn(ExtremeSegment segment,
			ExtremeSegmentsOnlineCtx ctx) {
		Double area = segment.getCalculatedArea();
		Long length = segment.getCalculatedLength();
		Integer peaks = segment.getPeakEntries().size();
		SegmentFeatureData innerData = new SegmentFeatureData(peaks, area, length);
//		String className = getClassName(segment, ctx);
//		log.debug("[learn]innerData: {0}; className: {1}", innerData,
//						className);
		return innerData;
	}

}

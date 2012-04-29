package org.spantus.extractor.segments.online.cluster;

import org.spantus.extractor.segments.ExtremeSegmentServiceImpl;
import org.spantus.extractor.segments.offline.ExtremeSegment;
import org.spantus.extractor.segments.online.ExtremeSegmentsOnlineCtx;
import org.spantus.extractor.segments.online.SegmentFeatureData;

public class ExtremeOnlineClusterServiceSimpleImpl implements ExtremeOnlineClusterService{

//	private static final Logger LOG = Logger.getLogger(ExtremeOnlineClusterServiceSimpleImpl.class);
	private ExtremeSegmentServiceImpl extremeSegmentService;
	
	public String getClassName(ExtremeSegment segment,
			ExtremeSegmentsOnlineCtx ctx) {
		return "1";
	}
	
	public SegmentFeatureData learn(ExtremeSegment segment,
			ExtremeSegmentsOnlineCtx ctx) {
		Double area = getExtremeSegmentService().getCalculatedArea(segment);
		Long length = getExtremeSegmentService().getCalculatedLength(segment);
		Integer peaks = segment.getPeakEntries().size();
		SegmentFeatureData innerData = new SegmentFeatureData(peaks, area, length);
//		String className = getClassName(segment, ctx);
//		log.debug("[learn]innerData: {0}; className: {1}", innerData,
//						className);
		return innerData;
	}
	
	public ExtremeSegmentServiceImpl getExtremeSegmentService() {
		if(extremeSegmentService == null){
			extremeSegmentService = new ExtremeSegmentServiceImpl();
		}
		return extremeSegmentService;
	}
	public void setExtremeSegmentService(
			ExtremeSegmentServiceImpl extremeSegmentService) {
		this.extremeSegmentService = extremeSegmentService;
	}

}

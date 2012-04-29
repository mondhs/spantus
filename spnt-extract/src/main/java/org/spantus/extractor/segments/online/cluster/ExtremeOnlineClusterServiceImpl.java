package org.spantus.extractor.segments.online.cluster;

import org.spantus.extractor.segments.offline.ExtremeSegment;
import org.spantus.extractor.segments.online.ExtremeSegmentsOnlineCtx;
import org.spantus.extractor.segments.online.SegmentFeatureData;
import org.spantus.logger.Logger;

public class ExtremeOnlineClusterServiceImpl extends ExtremeOnlineClusterServiceSimpleImpl{

	private static final Logger log = Logger.getLogger(ExtremeOnlineClusterServiceImpl.class);

	/**
	 * 
	 */
	@SuppressWarnings("unused")
	@Override
	public String getClassName(ExtremeSegment segment,
			ExtremeSegmentsOnlineCtx ctx) {
		Double area = getExtremeSegmentService().getCalculatedArea(segment);
		Long length = getExtremeSegmentService().getCalculatedLength(segment);
		Integer peaks =  segment.getPeakEntries().size();
		Integer argNum = 1;
		ExtremeSegment maxSegment = null;
		ExtremeSegment minSegment = null;
		if(ctx.getExtremeSegments().size() <2){
			return "" + argNum;
		}
		for (ExtremeSegment iSegment : ctx.getExtremeSegments()) {
			if(maxSegment == null){
				maxSegment = iSegment;
			}else {
				maxSegment = getExtremeSegmentService().getCalculatedArea(iSegment)>getExtremeSegmentService().getCalculatedArea(maxSegment)?iSegment:maxSegment;
			}
			if(minSegment == null){
				minSegment = iSegment;
			}else {
				minSegment = getExtremeSegmentService().getCalculatedArea(iSegment)<getExtremeSegmentService().getCalculatedArea(minSegment)?iSegment:minSegment;
			}
		}
		if(minSegment.getStart().compareTo(maxSegment.getStart()) != 0 ){
			double coef = (getExtremeSegmentService().getCalculatedArea(segment)-getExtremeSegmentService().getCalculatedArea(minSegment))/
					(getExtremeSegmentService().getCalculatedArea(maxSegment)-getExtremeSegmentService().getCalculatedArea(minSegment));
			if(coef>0.5){
				argNum = 2;
			}else if(coef<0.25){
				argNum = 0;
				log.debug("[getClassName] mark for delete: {0} [{1}>{2}];coef: {3} ", segment, getExtremeSegmentService().getCalculatedArea(segment) ,
						getExtremeSegmentService().getCalculatedArea(maxSegment),coef);
			}
		}

		
		return "" + argNum;
	}
	/**
	 * 
	 */
	@Override
	public SegmentFeatureData learn(ExtremeSegment segment, ExtremeSegmentsOnlineCtx ctx){
//		Double area = segment.getCalculatedArea();
//		Long length = segment.getCalculatedLength();
//		Integer peaks =  segment.getPeakEntries().size();
		SegmentFeatureData innerData = super.learn(segment, ctx);
		if(innerData.getIsNull() || Boolean.TRUE.equals(ctx.getSkipLearn())){
			return innerData;
		}
		//picku stats
		if(ctx.segmentStats.size()==0){
			ctx.segmentStats.add(innerData.clone());
			ctx.segmentStats.add(innerData.clone());
		}else{ 
			if(ctx.segmentStats.get(0).getArea()>innerData.getArea()){
				ctx.segmentStats.set(0, innerData.clone());
			}
			if(ctx.segmentStats.get(1).getArea()<innerData.getArea()){
				ctx.segmentStats.set(1, innerData.clone());
			}
		}
		SegmentFeatureData normData = ctx.normalizeArea(innerData);

		if(innerData.getArea() > 0D){
			ctx.semgnetFeatures.add(normData);
		}
		
		SegmentFeatureData min = ctx.normalizeArea(ctx.segmentStats.get(0));
		SegmentFeatureData max = ctx.normalizeArea(ctx.segmentStats.get(1));
		SegmentFeatureData avg = ctx.normalizeArea(ctx.segmentStats.get(1));
//		Double maxDistance = null;
//		SegmentFeatureData minData = null;
////		Double maxDistance2 = null;
//		SegmentFeatureData maxData= null;
		Double minArea = min.getArea();
		Double maxArea = max.getArea();
		
		for (SegmentFeatureData iData : ctx.semgnetFeatures) {
			Double distanceToMin = iData.distance(min)*1.8D;
			Double distanceToMax = iData.distance(max)/2;
			if(distanceToMin<distanceToMax){
				minArea = (minArea+iData.getArea())/2;
			}else{
				maxArea = (maxArea+iData.getArea())/2;
			}
		}
		min.setArea(minArea);
		avg.setArea((minArea+maxArea)/2);
		max.setArea(maxArea);
		
		if(ctx.segmentCenters.size()==0){
			ctx.segmentCenters.add(min);
			ctx.segmentCenters.add(avg);
			ctx.segmentCenters.add(max);
		}else{
			ctx.segmentCenters.set(0, min);
			ctx.segmentCenters.set(1, avg);
			ctx.segmentCenters.set(2, max);
		}
		
		
//		for (SegmentFeatureData iData : ctx.semgnetFeatures) {
//			for (SegmentFeatureData jData : ctx.semgnetFeatures) {
//			if(iData.getArea() == 0D || jData.getArea() == 0D){
//				continue;
//			}
//			Double distance = iData.distance(jData);
////			if(minDistance == null || minDistance>distance){
////				minDistance = distance;
////				minData = iData;
//			
////			}
//				if(maxDistance == null || maxDistance<distance){
//					maxDistance = distance;
//					maxData1 = iData;
//					maxData2 = jData;
//				}
//			}
//		}
		
		
		
//		if(maxData1.compareTo(maxData2)>0){
//			ctx.segmentStats.set(0, maxData1);
//			ctx.segmentStats.set(1, maxData2);
//		}else {
//			ctx.segmentStats.set(0, maxData2);
//			ctx.segmentStats.set(1, maxData1);
//		}
		log.debug("[learn]innerData: {0};", normData);

//		ctx.maxDistance = maxDistance;
		log.debug("[learn]  minArea: {0}; maxArea: {1}",  
				minArea, maxArea);
		return innerData;
		
	}

}

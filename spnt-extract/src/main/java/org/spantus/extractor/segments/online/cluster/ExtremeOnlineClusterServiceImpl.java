package org.spantus.extractor.segments.online.cluster;

import org.spantus.extractor.segments.offline.ExtremeSegment;
import org.spantus.extractor.segments.online.ExtremeSegmentsOnlineCtx;
import org.spantus.extractor.segments.online.SegmentFeatureData;
import org.spantus.logger.Logger;
import org.spantus.math.VectorUtils;

public class ExtremeOnlineClusterServiceImpl extends ExtremeOnlineClusterServiceSimpleImpl{

	Logger log = Logger.getLogger(ExtremeOnlineClusterServiceImpl.class);
	/**
	 * 
	 */
	@Override
	public String getClassName(ExtremeSegment segment,
			ExtremeSegmentsOnlineCtx ctx) {
		Double area = segment.getCalculatedArea();
		Long length = segment.getCalculatedLength();
		Integer peaks =  segment.getPeakEntries().size();
		log.debug("[getClassName]get class for segment {0}",segment);
//		if(length<30){
//			return "0";
//		}
		if(ctx.segmentStats == null || ctx.segmentStats.size() == 0){
			return "1";
		}
		if(ctx.segmentStats.get(1).getArea() == ctx.segmentStats.get(0).getArea()){
			return "1";
		}

		Double delta = ctx.segmentStats.get(1).getArea() - ctx.segmentStats.get(0).getArea();
		area = (area-ctx.segmentStats.get(0).getArea())/delta;
		if(area <= 0 ){
			return "1";
		}
//		area = delta == 0?0:area;

//		SegmentFeatureData min = ctx.normalizeArea(ctx.segmentStats.get(0));
//		SegmentFeatureData max = ctx.normalizeArea(ctx.segmentStats.get(1));
		
		SegmentFeatureData data = new SegmentFeatureData(peaks,area,length);
		
		log.debug("[getClassName] data: {0}", data, ctx.semgnetFeatures);

		Double distanceToMin = data.distance(ctx.segmentCenters.get(0));
		Double avgDistance = data.distance(ctx.segmentCenters.get(1));
		Double distanceToMax = data.distance(ctx.segmentCenters.get(2));
//		Double distanceToMax = data.distance(getOnlineCtx().segmentStats.get(2));
		if(distanceToMin.equals(distanceToMax)){
			return "1";
		}
		
//		StringBuilder sb = new StringBuilder();
//		for (SegmentFeatureData idat : ctx.semgnetFeatures) {
//			sb.append(MessageFormat.format("{0}\n", ""+idat.getArea()));
//		}
//		log.debug(sb.toString());
		
		Double toOneClass = avgDistance;
		Double toTwoClass = distanceToMax;
		Integer argNum = VectorUtils.minArg(distanceToMin, toOneClass, toTwoClass);

		
//		if(0 != argNum){
//			return argNum + "[" +distanceToMin+":"+distanceToMax+"]";
//		}
		
		log.debug("[getClassName]  to0: {0}, to1: {1}; to2: {2}; toMax:{3}; index {4};",  
				distanceToMin, toOneClass, toTwoClass, distanceToMax, argNum);
		if(argNum == 0 && peaks<3 && length > 150){
			log.debug("index {1}, but has peaks {0}. say is 1", argNum);
			return "1";
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

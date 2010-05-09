package org.spantus.extract.segments.online.cluster;

import org.spantus.extract.segments.offline.ExtremeSegment;
import org.spantus.extract.segments.online.ExtremeSegmentsOnlineCtx;
import org.spantus.extract.segments.online.SegmentInnerData;
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
			return "0";
		}
		if(ctx.segmentStats.get(1).getArea() == ctx.segmentStats.get(0).getArea()){
			log.error("same min max");
			return "1";
		}

		Double delta = ctx.segmentStats.get(1).getArea() - ctx.segmentStats.get(0).getArea();
		area = (area-ctx.segmentStats.get(0).getArea())/delta;
		if(area <= 0 ){
			log.error("area neagative or 0");
			return "1";
		}
//		area = delta == 0?0:area;

//		SegmentInnerData min = ctx.normalizeArea(ctx.segmentStats.get(0));
//		SegmentInnerData max = ctx.normalizeArea(ctx.segmentStats.get(1));
		
		SegmentInnerData data = new SegmentInnerData(peaks,area,length);
		
		log.debug("[getClassName] data: {0}", data, ctx.semgnetFeatures);

		Float distanceToMin = data.distance(ctx.segmentCenters.get(0));
		Float avgDistance = data.distance(ctx.segmentCenters.get(1));
		Float distanceToMax = data.distance(ctx.segmentCenters.get(2));
//		Float distanceToMax = data.distance(getOnlineCtx().segmentStats.get(2));
		if(distanceToMin.equals(distanceToMax)){
			return "1";
		}
		
//		StringBuilder sb = new StringBuilder();
//		for (SegmentInnerData idat : ctx.semgnetFeatures) {
//			sb.append(MessageFormat.format("{0}\n", ""+idat.getArea()));
//		}
//		log.debug(sb.toString());
		
		Float toOneClass = avgDistance;
		Float toTwoClass = distanceToMax;
		Integer argNum = VectorUtils.minArg(distanceToMin, toOneClass, toTwoClass);

		
//		if(0 != argNum){
//			return argNum + "[" +distanceToMin+":"+distanceToMax+"]";
//		}
		
		log.debug("[getClassName]  to0: {0}, to1: {1}; to2: {2}; toMax:{3}; index {4};",  
				distanceToMin, toOneClass, toTwoClass, distanceToMax, argNum);
//		if(peaks>2){
//			log.debug("index 0, but has peaks {0}. say is 1", peaks);
//			return "1";
//		}
		
		return "" + argNum;
	}
	/**
	 * 
	 */
	@Override
	public SegmentInnerData learn(ExtremeSegment segment, ExtremeSegmentsOnlineCtx ctx){
//		Double area = segment.getCalculatedArea();
//		Long length = segment.getCalculatedLength();
//		Integer peaks =  segment.getPeakEntries().size();
		SegmentInnerData innerData = super.learn(segment, ctx);
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
		SegmentInnerData normData = ctx.normalizeArea(innerData);

		if(innerData.getArea() > 0D){
			ctx.semgnetFeatures.add(normData);
		}
		
		SegmentInnerData min = ctx.normalizeArea(ctx.segmentStats.get(0));
		SegmentInnerData max = ctx.normalizeArea(ctx.segmentStats.get(1));
		SegmentInnerData avg = ctx.normalizeArea(ctx.segmentStats.get(1));
//		Float maxDistance = null;
//		SegmentInnerData minData = null;
////		Float maxDistance2 = null;
//		SegmentInnerData maxData= null;
		Double minArea = min.getArea();
		Double maxArea = max.getArea();
		
		for (SegmentInnerData iData : ctx.semgnetFeatures) {
			Float distanceToMin = iData.distance(min)*1.8F;
			Float distanceToMax = iData.distance(max)/2;
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
		
		
//		for (SegmentInnerData iData : ctx.semgnetFeatures) {
//			for (SegmentInnerData jData : ctx.semgnetFeatures) {
//			if(iData.getArea() == 0D || jData.getArea() == 0D){
//				continue;
//			}
//			Float distance = iData.distance(jData);
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

package org.spantus.extract.segments.online.cluster;

import org.spantus.core.threshold.ExtremeSegment;
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
//		if(length<30){
//			return "0";
//		}
		
		if(ctx.segmentStats == null || ctx.segmentStats.size() == 0){
			return "0";
		}

		Double delta = ctx.segmentStats.get(1).getArea() - ctx.segmentStats.get(0).getArea();
		area = (area-ctx.segmentStats.get(0).getArea())/delta;
		
		SegmentInnerData data = new SegmentInnerData(peaks,area,length);

		Float distanceToMin = data.distance(ctx.segmentStats.get(0));
		Float distanceToMax = data.distance(ctx.segmentStats.get(1));
		Float avgDistance = (distanceToMax+distanceToMin)/2;
//		Float distanceToMax = data.distance(getOnlineCtx().segmentStats.get(2));
		if(distanceToMin.equals(distanceToMax)){
			return "1";
		}
		Float toOneClass = avgDistance;
		Float toTwoClass = distanceToMax;
		Integer argNum = VectorUtils.minArg(distanceToMin, toOneClass, toTwoClass);
		
//		if(0 != argNum){
//			return argNum + "[" +distanceToMin+":"+distanceToMax+"]";
//		}
		
		log.debug("[getClassName]  to0: {0}, to1: {1}; to2: {2}; toMax:{3}; index {4};",  
				distanceToMin, toOneClass, toTwoClass, distanceToMax, argNum);
		
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
		if(innerData.getIsNull()){
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
		Double delta = ctx.segmentStats.get(1).getArea() - ctx.segmentStats.get(0).getArea();
		innerData.setArea((innerData.getArea()-ctx.segmentStats.get(0).getArea())/delta);
		ctx.semgnetFeatures.add(innerData);
		
		if(ctx.segmentStats.size()==0){
			ctx.segmentStats.add(innerData.clone());
			ctx.segmentStats.add(innerData.clone());
//			onlineCtx.segmentStats.add(new SegmentInnerData(peaks,area,length));
		}
		Float maxDistance = null;
		SegmentInnerData maxData1 = null;
//		Float maxDistance2 = null;
		SegmentInnerData maxData2= null;
		
		for (SegmentInnerData iData : ctx.semgnetFeatures) {
			for (SegmentInnerData jData : ctx.semgnetFeatures) {
			Float distance = iData.distance(jData);
//			if(minDistance == null || minDistance>distance){
//				minDistance = distance;
//				minData = iData;
//			}
				if(maxDistance == null || maxDistance<distance){
					maxDistance = distance;
					maxData1 = iData;
					maxData2 = jData;
				}
			}
		}
		
		
		
//		if(maxData1.compareTo(maxData2)>0){
//			ctx.segmentStats.set(0, maxData1);
//			ctx.segmentStats.set(1, maxData2);
//		}else {
//			ctx.segmentStats.set(0, maxData2);
//			ctx.segmentStats.set(1, maxData1);
//		}
		log.debug("[learn]innerData: {0};", innerData);

		ctx.maxDistance = maxDistance;
		log.debug("[learn]  maxDistance: {0};",  
				maxDistance);
		return innerData;
		
	}

}

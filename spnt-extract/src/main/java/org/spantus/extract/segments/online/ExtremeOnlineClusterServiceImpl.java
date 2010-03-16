package org.spantus.extract.segments.online;

import org.spantus.core.threshold.ExtremeSegment;
import org.spantus.logger.Logger;
import org.spantus.math.VectorUtils;

public class ExtremeOnlineClusterServiceImpl implements ExtremeOnlineClusterService{

	Logger log = Logger.getLogger(ExtremeOnlineClusterServiceImpl.class);
	
	public String getClassName(ExtremeSegment segment,
			ExtremeSegmentsOnlineCtx ctx) {
		if(ctx.segmentStats == null || ctx.segmentStats.size()==0){
			return "0";
		}
		Double area = segment.getCalculatedArea();
		Long length = segment.getCalculatedLength();
		Integer peaks =  segment.getPeakEntries().size();

		
		SegmentInnerData data = new SegmentInnerData(peaks,area,length);
		Float distanceToMin = data.distance(ctx.segmentStats.get(0));
		Float distanceToMax = data.distance(ctx.segmentStats.get(1));
		Float avgDistance = (distanceToMax+distanceToMin)/2;
//		Float distanceToMax = data.distance(getOnlineCtx().segmentStats.get(2));
		if(distanceToMin.equals(distanceToMax)){
			return "1";
		}
		Integer argNum = VectorUtils.minArg(distanceToMin, avgDistance/20, avgDistance/10);
		
//		if(0 != argNum){
//			return argNum + "[" +distanceToMin+":"+distanceToMax+"]";
//		}
		
//		log.debug("[getClassName]  toMin {0}, toMax:{1}; index {2};maxmax {3}",  
//				distanceToMin, distanceToMax/2, argNum, this.maxDistance);
		
		return "" + argNum;
	}

	public SegmentInnerData learn(ExtremeSegment segment, ExtremeSegmentsOnlineCtx ctx){
		Double area = segment.getCalculatedArea();
		Long length = segment.getCalculatedLength();
		Integer peaks =  segment.getPeakEntries().size();
		SegmentInnerData innerData = new SegmentInnerData(peaks,area,length);
		if(area == 0D && length == 0 && peaks == 0){
			return innerData;
		}
		
		log.debug("[learn]  area {0}, length:{1}, peaks: {2}",  
				""+area, ""+length, peaks);
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
		if(maxData1.compareTo(maxData2)>0){
			ctx.segmentStats.set(0, maxData1);
			ctx.segmentStats.set(1, maxData2);
		}else {
			ctx.segmentStats.set(0, maxData2);
			ctx.segmentStats.set(1, maxData1);
		}
		ctx.maxDistance = maxDistance;
		return innerData;
		
	}

}

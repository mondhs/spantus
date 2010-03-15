package org.spantus.extract.segments.online;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.spantus.core.threshold.ExtremeSegment;
import org.spantus.extract.segments.online.rule.ClassifierRuleBaseEnum;
import org.spantus.math.VectorUtils;

public class ExtremeSegmentsOnlineCtx {
	private LinkedList<ExtremeSegment> extremeSegments;
	private ExtremeSegment currentSegment;
	private Integer index=0;
	private ClassifierRuleBaseEnum.state markerState;
	
	public LinkedList<SegmentInnerData> semgnetFeatures = new LinkedList<SegmentInnerData>();
	public List<SegmentInnerData> segmentStats = new ArrayList<SegmentInnerData>(3);
	public Float maxDistance;
	
	public Integer increase(){
		return ++index;
	}
	public Boolean getFoundStartSegment() {
		if(getCurrentSegment()!=null){
			return (getIndex()-1) == getCurrentSegment().getStartEntry().getIndex();
		}
		
		return false;
	}
	public Boolean getFoundEndSegment() {
		if(getCurrentSegment()!=null && getCurrentSegment().getEndEntry() != null){
			return getIndex().equals(getCurrentSegment().getEndEntry().getIndex());
		}
		
		return false;
	}
	public Boolean getFoundPeakSegment() {
		if(getCurrentSegment()!=null && getCurrentSegment().getPeakEntry() != null){
			return getIndex().equals(getCurrentSegment().getPeakEntry().getIndex());
		}
		
		return false;
	}
	public boolean isIn(ClassifierRuleBaseEnum.state givenState){
		if(givenState == null){
			return getMarkerState() == null;
		}
		return givenState.equals(getMarkerState());
	}
	
	
	public void learn(ExtremeSegment segment){
		Double area = segment.getCalculatedArea();
		Long length = segment.getCalculatedLength();
		Integer peaks =  segment.getPeakEntries().size();
		SegmentInnerData innerData = new SegmentInnerData(peaks,area,length);
		if(area == 0D && length == 0 && peaks == 0){
			return;
		}
		
//		log.debug("[learn]  area {0}, length:{1}, peaks: {2}",  
//				""+area, ""+length, peaks);
		semgnetFeatures.add(innerData);
		
		if(segmentStats.size()==0){
			segmentStats.add(innerData.clone());
			segmentStats.add(innerData.clone());
//			onlineCtx.segmentStats.add(new SegmentInnerData(peaks,area,length));
		}
		Float maxDistance = null;
		SegmentInnerData maxData1 = null;
//		Float maxDistance2 = null;
		SegmentInnerData maxData2= null;
		
		for (SegmentInnerData iData : semgnetFeatures) {
			for (SegmentInnerData jData : semgnetFeatures) {
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
			segmentStats.set(0, maxData1);
			segmentStats.set(1, maxData2);
		}else {
			segmentStats.set(0, maxData2);
			segmentStats.set(1, maxData1);
		}
		this.maxDistance = maxDistance;
		
		
	}
	
	public String getClassName(ExtremeSegment segment){
		if(segmentStats == null || segmentStats.size()==0){
			return "0";
		}
		Double area = segment.getCalculatedArea();
		Long length = segment.getCalculatedLength();
		Integer peaks =  segment.getPeakEntries().size();
/*
		if(0<=area && area< 34000){
			return "0";
		}else if(34000<area && area< 55000){
			return "1";
		}else if(55000<area && area< 450000){
			return "2";
		}		
*/	
		
		SegmentInnerData data = new SegmentInnerData(peaks,area,length);
		Float distanceToMin = data.distance(segmentStats.get(0));
		Float distanceToMax = data.distance(segmentStats.get(1));
		Float avgDistance = (distanceToMax+distanceToMin)/2;
//		Float distanceToMax = data.distance(getOnlineCtx().segmentStats.get(2));
		if(distanceToMin.equals(distanceToMax)){
			return "1";
		}
		Integer argNum = VectorUtils.minArg(distanceToMin, avgDistance/10, avgDistance*2);
		
//		if(0 != argNum){
//			return argNum + "[" +distanceToMin+":"+distanceToMax+"]";
//		}
		
//		log.debug("[getClassName]  toMin {0}, toMax:{1}; index {2};maxmax {3}",  
//				distanceToMin, distanceToMax/2, argNum, this.maxDistance);
		
		return "" + argNum;
	}
	
	
	public ExtremeSegmentsOnlineCtx() {
		extremeSegments = new LinkedList<ExtremeSegment>();
	}
	
	
	public LinkedList<ExtremeSegment> getExtremeSegments() {
		return extremeSegments;
	}

	public void setExtremeSegments(LinkedList<ExtremeSegment> extremeSegments) {
		this.extremeSegments = extremeSegments;
	}


	public ExtremeSegment getCurrentSegment() {
		return currentSegment;
	}


	public void setCurrentSegment(ExtremeSegment lastSegment) {
		this.currentSegment = lastSegment;
	}

	public Integer getIndex() {
		return index;
	}

	public ClassifierRuleBaseEnum.state getMarkerState() {
		return markerState;
	}

	public void setMarkerState(ClassifierRuleBaseEnum.state markerState) {
		this.markerState = markerState;
	}

}

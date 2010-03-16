package org.spantus.extract.segments.online;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.spantus.core.threshold.ExtremeSegment;
import org.spantus.extract.segments.online.rule.ClassifierRuleBaseEnum;

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

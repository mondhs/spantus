package org.spantus.extractor.segments.online;

import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import org.spantus.core.FrameValues;
import org.spantus.extractor.segments.offline.ExtremeEntry;
import org.spantus.extractor.segments.offline.ExtremeEntry.FeatureStates;
import org.spantus.extractor.segments.offline.ExtremeSegment;
import org.spantus.extractor.segments.online.rule.ClassifierRuleBaseEnum;
import org.spantus.utils.Assert;

public class ExtremeSegmentsOnlineCtx {
	private Deque<ExtremeSegment> extremeSegments;
	private Deque<Long> mins = new LinkedList<Long>();
	private ExtremeSegment currentSegment;
	private Integer index=0;
	private ClassifierRuleBaseEnum.state markerState;
	private Double previousValue;
	private ExtremeEntry prevSegmentEntry = null;
	private ExtremeEntry segmentEntry  = null;
    private int stableCount = 0;
	
	private Boolean skipLearn = Boolean.FALSE;
	
	
	public Queue<SegmentFeatureData> semgnetFeatures = new LinkedList<SegmentFeatureData>();
	public List<SegmentFeatureData> segmentStats = new ArrayList<SegmentFeatureData>(3);
	public List<SegmentFeatureData> segmentCenters = new ArrayList<SegmentFeatureData>(3);
	public Double maxDistance;
	private Queue<FrameValues> windowValuesQueue = new LinkedList<FrameValues>();
	

	public SegmentFeatureData normalizeArea(SegmentFeatureData data){
		SegmentFeatureData cloned = data.clone();
		Double delta = segmentStats.get(1).getArea() - segmentStats.get(0).getArea();
		if(delta == 0D ){
			delta = 1D;
		}
		Double area = (cloned.getArea()-segmentStats.get(0).getArea())/delta;
		cloned.setArea(area);
		return cloned;
	}
	
		public void reset() {
		
		getExtremeSegments().clear();
		setIndex(0);
		setCurrentSegment(null);
		setPrevSegmentEntry(null);
		this.segmentEntry = null;
		setPreviousValue(null);
	}
	
	public Integer increase(){
		index++;
		return getIndex();
	}
//	public Boolean getFoundStartSegment() {
//		if(getCurrentSegment()!=null && getCurrentSegment().getStartEntry()!=null){
//			return (getIndex()) == getCurrentSegment().getStartEntry().getIndex();
//		}
//		
//		return false;
//	}
	public Boolean getFoundChangePoint() {
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
	/**
	 * is Feature Increase
	 * @return
	 */
	public Boolean getFeatureStable(){
		return getFeatureState(FeatureStates.stable);
	}
	/**
	 * is Feature Increase
	 * @return
	 */
	public Boolean getFeatureIncrease(){
		return getFeatureState(FeatureStates.min);
	}
	/**
	 * 
	 * @return
	 */
	public  Boolean getFeatureInMin(){
		if(getSegmentEntry() == null){
			return false;
		}
		return FeatureStates.min.equals(getSegmentEntry().getSignalState());
	}
	/**
	 * 
	 * @return
	 */
	public Boolean getFeatureInMax(){
		ExtremeEntry lastEntry = getSegmentEntry();
		if(lastEntry == null){
			return false;
		}
		
		return FeatureStates.max.equals(lastEntry.getSignalState());
	}
	/**
	 * is Feature Decrease
	 * @return
	 */
	public Boolean getFeatureDecrease(){
		return getFeatureState(FeatureStates.max);
	}
	/**
	 * 
	 * @param givenState
	 * @return
	 */
	public Boolean getFeatureState(FeatureStates givenState){
		if(segmentEntry == null && prevSegmentEntry == null){
			return true;
		}
		if(segmentEntry == null){
			return givenState.equals(prevSegmentEntry.getSignalState()) || prevSegmentEntry == null;
		}
		boolean inState = givenState.equals(segmentEntry.getSignalState()) || segmentEntry == null;
		return inState;
	}
	
	
	public ExtremeSegmentsOnlineCtx() {
		extremeSegments = new LinkedList<ExtremeSegment>();
		mins = new LinkedList<Long>();
	}
	
	
	public Deque<ExtremeSegment> getExtremeSegments() {
		return extremeSegments;
	}

	public void setExtremeSegments(Deque<ExtremeSegment> extremeSegments) {
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
	public void setIndex(Integer index) {
		this.index = index;
	}
	public Double getPreviousValue() {
		return previousValue;
	}
	public void setPreviousValue(Double previous) {
		this.previousValue = previous;
	}

	public Boolean getSkipLearn() {
		return skipLearn;
	}

	public void setSkipLearn(Boolean skipLearn) {
		this.skipLearn = skipLearn;
	}

	public ExtremeEntry getSegmentEntry() {
		return segmentEntry;
	}

	public void setSegmentEntry(ExtremeEntry segmentEntry) {
		Assert.isTrue(segmentEntry!=null,"Segment entry cannot be null");
		prevSegmentEntry= this.segmentEntry;
		this.segmentEntry = segmentEntry;
	}

	public Deque<Long> getMins() {
		return mins;
	}

	public void setMins(Deque<Long> mins) {
		this.mins = mins;
	}

	public ExtremeEntry getPrevSegmentEntry() {
		return prevSegmentEntry;
	}

	public void setPrevSegmentEntry(ExtremeEntry prevSegmentEntry) {
		this.prevSegmentEntry = prevSegmentEntry;
	}

	public int getStableCount() {
		return stableCount;
	}
	
	public void resetStableCount() {
		this.stableCount = 0;
	}

	public int incStableCount() {
		return this.stableCount++;
	}

	public void popWindowValues(FrameValues windowValues) {
		
	}

	public void pushWindowValues(FrameValues windowValues) {
		windowValuesQueue.add(windowValues);
	}

	
//
//	public void setFeatureState(FeatureStates featureState) {
//		if(featureState != this.prevFeatureState && featureState != null){
//			prevFeatureState = featureState;
//		}
//		this.featureState = featureState;
//	}

}

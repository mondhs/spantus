package org.spantus.extract.segments.online;

import java.util.List;

import org.spantus.core.FrameValues;
import org.spantus.core.marker.Marker;
import org.spantus.core.threshold.AbstractClassifier;
import org.spantus.core.threshold.ExtremeEntry;
import org.spantus.core.threshold.ExtremeOfflineClassifier;
import org.spantus.core.threshold.ExtremeSegment;
import org.spantus.core.threshold.IClassificationListener;
import org.spantus.core.threshold.SegmentEvent;
import org.spantus.core.threshold.ExtremeEntry.SignalStates;
import org.spantus.extract.segments.online.rule.ClassifierRuleBaseEnum;
import org.spantus.extract.segments.online.rule.ClassifierRuleBaseService;
import org.spantus.extract.segments.online.rule.ClassifierRuleBaseEnum.state;
import org.spantus.logger.Logger;
import org.spantus.utils.StringUtils;

public class ExtremeOnlineClassifier extends AbstractClassifier{

	private Logger log = Logger.getLogger(ExtremeOnlineClassifier.class); 
	private Float previous;
	private ExtremeSegmentsOnlineCtx onlineCtx;
//	private ExtremeSegment extremeSegment;
	private SignalStates lastState = null;
	private ClassifierRuleBaseService ruleBaseService;
	
	public ExtremeOnlineClassifier() {
		onlineCtx = new ExtremeSegmentsOnlineCtx();
	}

	public void afterCalculated(Long sample, FrameValues result) {
//		SegmentInnerData[peaks: 1; area: 1688.3544311523438; length: 20], SegmentInnerData[peaks: 3; area: 304871.712890625; length: 203]
		//SegmentInnerData inner = null;
		//inner = new SegmentInnerData(1, 1688.35D, 20L);
		//getOnlineCtx().segmentStats.add(inner);
		//inner = new SegmentInnerData(3, 304871.71D, 203L);
		//getOnlineCtx().segmentStats.add(inner);

		for (Float value : result) {
			getThresholdValues().updateMinMax(value);
			processValue(value);
		}
	}
	
	@Override
	public void flush() {
		super.flush();
		log.debug("[flush]");
//		processResult(onlineCtx.getCurrentSegment());
		if(onlineCtx.getExtremeSegments().size()>0 &&
				onlineCtx.getExtremeSegments().getLast() != null){
			
//			ExtremeEntry entry = new ExtremeEntry(onlineCtx.getIndex().intValue(),
//				previous, SignalStates.min);
//			onlineCtx.getCurrentSegment().setEndEntry(entry);
//			onlineCtx.getExtremeSegments().add(onlineCtx.getCurrentSegment());
			if(getMarker() == null){
				startMarker(onlineCtx);
			}
			endMarkerApproved(onlineCtx);
		}
		getThresholdValues().addAll(ExtremeOfflineClassifier.refreshThreasholdInfo(getMarkSet(), getOutputValues()));
//		log.debug("[flush] stats: {0};",getOnlineCtx().segmentStats);
//		log.debug("[flush] features: {0}", getOnlineCtx().semgnetFeatures);
//		for (SegmentInnerData data : getOnlineCtx().semgnetFeatures) {
//			log.debug("[flush] disance to min: {0};", data.distance(getOnlineCtx().segmentStats.get(0)));
//			log.debug("[flush] disance to avg: {0};", data.distance(getOnlineCtx().segmentStats.get(1)));
//			log.debug("[flush] disance to max: {0};", data.distance(getOnlineCtx().segmentStats.get(2)));
//
//		}
	}
	/**
	 * 
	 * @param sample
	 * @param value
	 */
	private void processValue(Float value) {
		Integer index = onlineCtx.getIndex();
		if (previous == null) {
			previous = value;
			log.debug("[processValue]first: {0} on {1}",previous,index);
			index = onlineCtx.increase();
//			getThresholdValues().add(getThresholdValues().getMinValue());
			return;
		}
		if(lastState == null){
			if (value > previous) {
				log.debug("[processValue]found 1st min");
				lastState = SignalStates.min;
				onMinFound(index, previous, value);
			}else if(value < previous){
				log.debug("[processValue]found 1st max");
				onMaxFound(index, previous, value);
				lastState = SignalStates.max;
			}
		}else if(SignalStates.min.equals(lastState)){
			if(value <= previous){
				lastState = SignalStates.max;
				onMaxFound(index, previous, value);
			}
		}else if(SignalStates.max.equals(lastState)){
			if(value >= previous){
				lastState = SignalStates.min;
				onMinFound(index, previous, value);
			}
		}
		if(onlineCtx.getCurrentSegment() != null){
			onlineCtx.getCurrentSegment().getValues().add(value);
		}
		//process data
		processResult(onlineCtx.getCurrentSegment());
		
		updateLastExtremeSegment(onlineCtx, onlineCtx.getCurrentSegment());
		
		//updated iterative data
		previous = value;
		 onlineCtx.increase();
//		 if(getMarker()==null){
//			 getThresholdValues().add(value);
//		 }else {
//			 getThresholdValues().add(getThresholdValues().getMinValue());
//		}
	}
	
	protected void onMinFound(Integer index, float previous, float value){
		log.debug("[onMinFound]found min on {0} value {1}->{2}",
				index - 1, previous, value);
		
		if(onlineCtx.getCurrentSegment() == null){
			//first min
			onlineCtx.setCurrentSegment(createExtremeSegment());
			ExtremeEntry startEntry = new ExtremeEntry(index.intValue(),
					previous, SignalStates.min);
			onlineCtx.getCurrentSegment().setStartEntry(startEntry);
//			log.debug("[onMinFound]+starting {0}",
//					extremeSegment);
		}else{
			//last min
			ExtremeEntry entry = new ExtremeEntry(index.intValue(),
					previous, SignalStates.min);
			onlineCtx.getCurrentSegment().setEndEntry(entry);
//			log.debug("[onMinFound]+starting {0}",
//					extremeSegment);
		}
	}

	protected void onMaxFound(Integer index, float previous, float value){
		log.debug("[onMaxFound]found max on {0} value {1}->{2}",
				index - 1, previous, value);
//		if extreme segment not created skip it
		if(onlineCtx.getCurrentSegment() != null){
			ExtremeEntry middleEntry = new ExtremeEntry(index.intValue(),
					previous, SignalStates.max);
			onlineCtx.getCurrentSegment().setPeakEntry(middleEntry);
			onlineCtx.getCurrentSegment().getPeakEntries().add(middleEntry);
		}
	}
	protected void processResult(ExtremeSegment extremeSegment){
		if(getRuleBaseService() == null) return;
		ExtremeSegment last = null;
		if(onlineCtx.getExtremeSegments().size()>0){
			last = onlineCtx.getExtremeSegments().getLast();
		}
//		log.debug("[processResult]+++");
		log.debug("[processResult] on {2} [{3}]; current: {1}; segments: {0}; ", 
				last, 
				onlineCtx.getCurrentSegment(), 
				onlineCtx.getIndex(),
				onlineCtx.getMarkerState());
		
		String actionStr = getRuleBaseService().testOnRuleBase(onlineCtx);
		ClassifierRuleBaseEnum.action anAction = null;
		if(StringUtils.hasText(actionStr)){
			anAction = ClassifierRuleBaseEnum.action.valueOf(actionStr);			
		}
		log.debug("[processResult]>>>action {0}", anAction);
		switch (anAction) {
		case startMarker:
			startMarker(onlineCtx);
			break;
		case startMarkerApproved:
			startMarkerApproved(onlineCtx);
			break;
		case endMarker:
			endMarker(onlineCtx);
			break;
		case endMarkerApproved:
			endMarkerApproved(onlineCtx);
			break;
		case processSignal:
			processSegment(onlineCtx);
			break;
		case processNoise:
			break;
		case join:
			join(onlineCtx);
			break;
		case delete:
			learn(last, onlineCtx);
			break;
		default:
			log.error("Not impl: " + anAction);
			throw new IllegalArgumentException("Not impl: " + anAction);
		}
		log.debug("[processResult]---");
	}
	
	public void startMarkerApproved(ExtremeSegmentsOnlineCtx ctx){
		if(getMarker()==null){
			startMarker(ctx);
		}
		onlineCtx.setMarkerState(state.segment);
	}
	
	public void startMarker(ExtremeSegmentsOnlineCtx ctx){
		ExtremeSegment segment = ctx.getCurrentSegment();
		Integer startSample = segment.getStartEntry().getIndex();
		Long time = getOutputValues().indextoMils(startSample);
		Marker marker = new Marker();
		marker.setLabel("" + (ctx.getIndex()));
		marker.setStart(time);
		marker.getExtractionData().setStartSampleNum(startSample.longValue()-1);
		setMarker(marker);
		onlineCtx.setMarkerState(state.start);
		for (IClassificationListener listener : getClassificationListeners()) {
			listener.onSegmentStarted(
					new SegmentEvent(getName(),time,getMarker(),ctx.getIndex().longValue()));
		}
	}
	public void endMarker(ExtremeSegmentsOnlineCtx ctx){
		log.debug("[endMarker]!");
	}
	
	public void endMarkerApproved(ExtremeSegmentsOnlineCtx ctx){
		Marker marker = getMarker();
		if(marker == null){
			log.debug("Marker not initialized");
			return;
		}
		ExtremeSegment last = null;
		if(ctx.getExtremeSegments().size()>0){
			 last = ctx.getExtremeSegments().getLast();
		}else{
//			onlineCtx.learn(ctx.getCurrentSegment());
//			return;
			 last = ctx.getCurrentSegment();
		}
		
		learn(last,ctx);
		
		Integer startSample = last.getStartEntry().getIndex();
		Integer endSample = last.getEndEntry().getIndex();
		
		Long startTime = getOutputValues().indextoMils(startSample);
		Long endTime = getOutputValues().indextoMils(endSample);
		
		String className = onlineCtx.getClassName(last);
		
		marker.setLabel(marker.getLabel()+":" + className);
		marker.setStart(startTime);
		marker.setEnd(endTime);
		marker.getExtractionData().setStartSampleNum(startSample.longValue()-1);
		marker.getExtractionData().setEndSampleNum(endSample.longValue()-1);
		
		boolean valid = true;
		
		for (Marker iMarker : getMarkSet().getMarkers()) {
			if(iMarker.getEnd()>startTime){
				log.error("conflicts " + iMarker + " with " + marker);
				valid = false;
			}
		}
		
//		if(last.getCalculatedLength()>90){
//		if(last.getCalculatedArea()>90000){
		if(valid){
			className = onlineCtx.getClassName(last);
			getMarkSet().getMarkers().add(marker);
			log.debug("[endMarkerApproved] adding marker {0}", marker );
		}
		setMarker(null);
		onlineCtx.setMarkerState(null);
		for (IClassificationListener listener : getClassificationListeners()) {
			listener.onSegmentEnded(
					new SegmentEvent(getName(),startTime,getMarker(),ctx.getIndex().longValue()));
		}
	}
	
	
	public void join(ExtremeSegmentsOnlineCtx ctx){
		ExtremeSegment current = ctx.getCurrentSegment();
		ExtremeSegment last = ctx.getExtremeSegments().getLast();
		last.setEndEntry(current.getEndEntry());
		last.getValues().addAll(last.getValues());
		last.getPeakEntries().add(current.getPeakEntry());
		
		Float maxValue = last.getPeakEntry().getValue();
		for (ExtremeEntry extremeEntry : last.getPeakEntries()) {
			if(maxValue<extremeEntry.getValue()){
				maxValue = extremeEntry.getValue();
				last.setPeakEntry(extremeEntry);
			}
		}

		
		ctx.setCurrentSegment(createExtremeSegment());
		onlineCtx.getCurrentSegment().setStartEntry(current.getEndEntry().clone());
		
	}
	public void processSegment(ExtremeSegmentsOnlineCtx ctx){
		onlineCtx.setMarkerState(state.segment);
		Long time = getOutputValues().indextoMils(ctx.getIndex());
		getMarker().setEnd(time);
	}
	
	protected void updateLastExtremeSegment(ExtremeSegmentsOnlineCtx ctx, ExtremeSegment lastSegment){
		//if this not change point or first element found
		if( lastSegment == null || !Boolean.TRUE.equals(ctx.getFoundEndSegment())){
			return;
		}
		//end is not initialized for first segment
		if(lastSegment.getEndEntry()==null){
			return;
		}

		//create new segment
//		ExtremeSegment lastSegment = onlineCtx.getCurrentSegment();
		onlineCtx.getExtremeSegments().add(lastSegment);
		log.debug("[updateLastExtremeSegment]created {0}",
				onlineCtx.getCurrentSegment());
		onlineCtx.setCurrentSegment(createExtremeSegment());
		onlineCtx.getCurrentSegment().setStartEntry(lastSegment.getEndEntry().clone());
//		return newExtremeSegment;
	}
	
	
	public void learn(ExtremeSegment segment, ExtremeSegmentsOnlineCtx ctx){
		Double area = segment.getCalculatedArea();
		Long length = segment.getCalculatedLength();
		Integer peaks =  segment.getPeakEntries().size();
		SegmentInnerData innerData = new SegmentInnerData(peaks,area,length);
		if(area == 0D && length == 0 && peaks == 0){
			return;
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
		
		
	}
	
	
	protected ExtremeSegment createExtremeSegment(){
		ExtremeSegment newExtremeSegment = new ExtremeSegment();
		newExtremeSegment.setValues(new FrameValues());
		newExtremeSegment.getValues().setSampleRate(getOutputValues().getSampleRate());
		return newExtremeSegment;
	}
	
	public List<ExtremeSegment> getExtremeSegments() {
		return onlineCtx.getExtremeSegments();
	}

	public ClassifierRuleBaseService getRuleBaseService() {
		return ruleBaseService;
	}

	public void setRuleBaseService(ClassifierRuleBaseService ruleBaseService) {
		this.ruleBaseService = ruleBaseService;
	}

	public ExtremeSegmentsOnlineCtx getOnlineCtx() {
		return onlineCtx;
	}


}

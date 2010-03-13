package org.spantus.extract.segments.online;

import java.util.List;

import org.spantus.core.FrameValues;
import org.spantus.core.marker.Marker;
import org.spantus.core.threshold.AbstractClassifier;
import org.spantus.core.threshold.ExtremeEntry;
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
		for (Float value : result) {
			processValue(value);
		}
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
			index = onlineCtx.increase();
			log.debug("[processValue]first: {0} on {1}",previous,index);
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
			if(value < previous){
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
	}
	
	protected void onMinFound(Integer index, float previous, float value){
//		log.debug("[onMinFound]found min on {0} value {1}->{2}",
//				index - 1, previous, value);
		
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
//		log.debug("[onMaxFound]found max on {0} value {1}->{2}",
//				index - 1, previous, value);
//		if extreme segment not created skip it
		if(onlineCtx.getCurrentSegment() != null){
			ExtremeEntry middleEntry = new ExtremeEntry(index.intValue(),
					previous, SignalStates.max);
			onlineCtx.getCurrentSegment().setMiddleEntry(middleEntry);
			onlineCtx.getCurrentSegment().getPeakEntries().add(middleEntry);
		}
	}
	protected void processResult(ExtremeSegment extremeSegment){
		if(getRuleBaseService() == null) return;
//		log.debug("[processResult]+++");
		log.debug("[processResult] on {2}; current: {1}; segments: {0}; ", 
				onlineCtx.getExtremeSegments(), 
				onlineCtx.getCurrentSegment(), onlineCtx.getIndex());
		
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
		Long time = getOutputValues().indextoMils(segment.getStartEntry().getIndex());
		Marker marker = new Marker();
		marker.setLabel("" + (ctx.getIndex()));
		marker.setStart(time);
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
		ExtremeSegment last = null;
		if(ctx.getExtremeSegments().size()>0){
			 last = ctx.getExtremeSegments().getLast();
		}else{
			 last = ctx.getCurrentSegment();
		}
		Long time = getOutputValues().indextoMils(last.getEndEntry().getIndex());
//		ExtremeSegment segment = ctx.getExtremeSegments().getLast();
		marker.setEnd(time);
		getMarkSet().getMarkers().add(marker);
		log.debug("[endMarkerApproved] adding marker {0}", marker);
		setMarker(null);
		onlineCtx.setMarkerState(null);
		for (IClassificationListener listener : getClassificationListeners()) {
			listener.onSegmentEnded(
					new SegmentEvent(getName(),time,getMarker(),ctx.getIndex().longValue()));
		}
	}
	
	public void join(ExtremeSegmentsOnlineCtx ctx){
		ExtremeSegment current = ctx.getCurrentSegment();
		ExtremeSegment last = ctx.getExtremeSegments().getLast();
		last.setEndEntry(current.getEndEntry());
		last.getValues().addAll(last.getValues());
		last.getPeakEntries().add(current.getMiddleEntry());
		
		Float maxValue = last.getMiddleEntry().getValue();
		for (ExtremeEntry extremeEntry : last.getPeakEntries()) {
			if(maxValue<extremeEntry.getValue()){
				maxValue = extremeEntry.getValue();
				last.setMiddleEntry(extremeEntry);
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


}

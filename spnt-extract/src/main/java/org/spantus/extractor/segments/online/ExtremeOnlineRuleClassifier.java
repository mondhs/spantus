package org.spantus.extractor.segments.online;

import java.text.MessageFormat;
import java.util.List;

import org.spantus.core.FrameValues;
import org.spantus.core.marker.Marker;
import org.spantus.core.threshold.AbstractClassifier;
import org.spantus.extractor.segments.offline.ExtremeEntry;
import org.spantus.extractor.segments.offline.ExtremeOfflineClassifier;
import org.spantus.extractor.segments.offline.ExtremeSegment;
import org.spantus.extractor.segments.offline.ExtremeEntry.FeatureStates;
import org.spantus.extractor.segments.online.cluster.ExtremeOnlineClusterService;
import org.spantus.extractor.segments.online.rule.ClassifierRuleBaseEnum;
import org.spantus.extractor.segments.online.rule.ClassifierRuleBaseService;
import org.spantus.extractor.segments.online.rule.ClassifierRuleBaseEnum.state;
import org.spantus.logger.Logger;
import org.spantus.utils.StringUtils;

public class ExtremeOnlineRuleClassifier extends AbstractClassifier{

	private Logger log = Logger.getLogger(ExtremeOnlineRuleClassifier.class); 
	private ExtremeSegmentsOnlineCtx onlineCtx;

	private ClassifierRuleBaseService ruleBaseService;
	private ExtremeOnlineClusterService clusterService;
	
	public ExtremeOnlineRuleClassifier() {
		onlineCtx = new ExtremeSegmentsOnlineCtx();
	}
	/*
	 * (non-Javadoc)
	 * @see org.spantus.core.extractor.IExtractorListener#afterCalculated(java.lang.Long, org.spantus.core.FrameValues)
	 */
	public void afterCalculated(Long sample, FrameValues result) {
		//entry class point
		for (Float value : result) {
			getThresholdValues().updateMinMax(value);
			processValue(value);
		}
	}
	/*
	 * (non-Javadoc)
	 * @see org.spantus.core.threshold.AbstractClassifier#flush()
	 */
	@Override
	public void flush() {
		super.flush();
		log.debug("[flush]");
		
		endupPendingSegments(getOnlineCtx());
		
		getThresholdValues().addAll(ExtremeOfflineClassifier.refreshThreasholdInfo(getMarkSet(), getOutputValues()));
	}
	/**
	 * invoced from flush
	 * @param ctx
	 */
	protected void endupPendingSegments(ExtremeSegmentsOnlineCtx ctx){
		log.debug("[endupPendingSegments] markers {0} before end up", getMarkSet().getMarkers());
		ExtremeSegment current = onlineCtx.getCurrentSegment();
		
		if(current!= null &&
				current.getPeakEntry() != null){
			changePointCurrentApproved(ctx);
		}else if(ctx.getExtremeSegments().size()>0){
			ExtremeSegment last = ctx.getExtremeSegments().getLast();
			if(last.getEndEntry() == null){
				ExtremeEntry endEntry = new ExtremeEntry(ctx.getIndex(), 
						ctx.getPreviousValue(), FeatureStates.min);
				last.setEndEntry(endEntry);
			}
			if( last != null
				&& last.getPeakEntry() != null
				&& !last.getApproved()){
			changePointLastApproved(ctx);
			}
		}
		log.debug("[endupPendingSegments] markers {0} after end up", getMarkSet().getMarkers());
	}
	
	/**
	 * 
	 * @param sample
	 * @param value
	 */
	protected void processValue(Float value) {
		Integer index = onlineCtx.getIndex();
		Float previous = onlineCtx.getPreviousValue();
		log.debug("[processValue] {0} value {1}->{2}",
				index , onlineCtx.getPreviousValue(), value);
		//starting point, previous not found
		if (onlineCtx.getPreviousValue() == null) {
			//first value
			onlineCtx.setPreviousValue(value);
			log.debug("[processFirstValue]first: {0} on {1}",onlineCtx.getPreviousValue(), index);
			index = onlineCtx.increase();
			return;
		}
		//regualar porcessing
			if (value >= onlineCtx.getPreviousValue() && onlineCtx.isFeatureDecrease()) {
				log.debug("[processValue]found min");
				ExtremeEntry entry = new ExtremeEntry(index, previous, FeatureStates.min);
				onlineCtx.setSegmentEntry(entry);
			}else if(value < onlineCtx.getPreviousValue() &&  onlineCtx.isFeatureIncrease()){
				log.debug("[processValue]found max");
				ExtremeEntry entry = new ExtremeEntry(index, previous, FeatureStates.max);
				onlineCtx.setSegmentEntry(entry);
				onMaxFound(index, onlineCtx.getPreviousValue(), value);
			}else {
				onlineCtx.setSegmentEntry(null);

			}
			log.debug("[processValue]found prev: {0}; now: {1}", onlineCtx.prevSegmentEntry, onlineCtx.getSegmentEntry());
		if(onlineCtx.getCurrentSegment() != null){
			onlineCtx.getCurrentSegment().getValues().add(value);
		}
		
		onlineCtx.setPreviousValue(value);
		//process data. This is the place where rules engine starts control
		///////////////////////////////////////
		processResult(onlineCtx.getCurrentSegment());		
		///////////////////////////////////////
		//updated iterative data
		 onlineCtx.increase();

	}
	/**
	 * 
	 * @param value
	 * @param index
	 */
	protected void processFirstSegmentValue(Float value, Integer index) {
		Float previous = onlineCtx.getPreviousValue();
		if (value > onlineCtx.getPreviousValue() && onlineCtx.isFeatureDecrease()) {
			log.debug("[processFirstSegmentValue]found 1st min");
			ExtremeEntry entry = new ExtremeEntry(index, previous, FeatureStates.min);
			onlineCtx.setSegmentEntry(entry);
		}else if(value < previous &&  onlineCtx.isFeatureIncrease()){
			log.debug("[processFirstSegmentValue]found 1st max");
			ExtremeEntry entry = new ExtremeEntry(index, previous, FeatureStates.max);
			onlineCtx.setSegmentEntry(entry);
		}
	}
	
	/**
	 * 
	 * @param index
	 * @param previous
	 * @param value
	 */
	protected void onMaxFound(Integer index, float previous, float value){
		log.debug("[onMaxFound]found max on {0} value {1}->{2}",
				index , previous, value);
//		if extreme segment not created skip it
		if(onlineCtx.getCurrentSegment() != null){
			ExtremeEntry middleEntry = new ExtremeEntry(index.intValue(),
					previous, FeatureStates.max);
			onlineCtx.getCurrentSegment().setPeakEntry(middleEntry);
			onlineCtx.getCurrentSegment().getPeakEntries().add(middleEntry);
		}
	}
	/**
	 * 
	 * @param extremeSegment
	 */
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
		case changePointCurrentApproved:
			changePointCurrentApproved(onlineCtx);
			break;
		case changePointLastApproved:
			changePointLastApproved(onlineCtx);
			break;
		case changePoint:
			changePoint(onlineCtx);
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
			deleteSegment(onlineCtx);
			break;
		default:
			log.error("[processResult]Not impl: " + anAction);
			throw new IllegalArgumentException("Not impl: " + anAction);
		}
		log.debug("[processResult]---");
	}
	/**
	 * 
	 * @param ctx
	 */
	public void changePointCurrentApproved(ExtremeSegmentsOnlineCtx ctx){
		ExtremeEntry changeEntry = ctx.prevSegmentEntry;
		if(changeEntry == null){
			return;
		}
		Marker currentMarker = getMarker();

		//record history
		ExtremeSegment currentSegment = onlineCtx.getCurrentSegment();
		if(currentSegment != null && currentSegment.getPeakEntry() != null){
			currentSegment.setEndEntry(changeEntry);
			log.debug("[changePointCurrentApproved] ending {0}",
					currentSegment);
			currentSegment.setApproved(true);
			ctx.getExtremeSegments().add(currentSegment);
			//learn and get class
			getClusterService().learn(currentSegment, ctx);
			String className = "";//getClusterService().getClassName(currentSegment, ctx);
			//clreate marker info
			if(currentMarker == null){
				currentMarker = createMarker(currentSegment, className);
			}else {
				syncMarker(currentMarker, currentSegment, className);
			}
//			if(!"0".equals(className)){
				appendMarker(currentMarker);
//			}else{
//				log.debug("[changePointLastApproved]not appended {0}", currentMarker);
//			}
		}
		
		//new segment and marker
		ExtremeSegment newSegment = createExtremeSegment(ctx.getPreviousValue());
		newSegment.setStartEntry(changeEntry.clone());
		log.debug("[changePointCurrentApproved] starting {0}",
				newSegment);
		
		Marker newMarker = createMarker(newSegment, "0");

		setMarker(newMarker);
		onlineCtx.setCurrentSegment(newSegment);
	}
	/**
	 * 
	 * @param ctx
	 */
	public void changePointLastApproved(ExtremeSegmentsOnlineCtx ctx){
		ExtremeEntry changeEntry = ctx.prevSegmentEntry;
		if(changeEntry == null){
			return;
		}
		Marker currentMarker = getMarker();
		
		//record history
		ExtremeSegment lastSegment = onlineCtx.getExtremeSegments().getLast();
		ExtremeSegment currentSegment = ctx.getCurrentSegment();
		
		//is apporved but not added to markers
		if(lastSegment != null && (!lastSegment.getApproved() || getMarkSet().getMarkers().size()== 0)){
//			lastSegment.setEndEntry(ctx.getCurrentSegment().getStartEntry());
			log.debug("[changePointLastApproved] ending {0}",
					lastSegment);
			if(lastSegment.getEndEntry()==null){
				return;
			}
			lastSegment.setApproved(true);
			getClusterService().learn(lastSegment, ctx);
//			String className = getClusterService().getClassName(lastSegment, ctx);
			syncMarker(currentMarker, lastSegment, "");
//			if(!"0".equals(className)){
				appendMarker(currentMarker);
//			}else{
//				log.debug("[changePointLastApproved]not appended {0}", currentMarker);
//			}
			Marker newMarker = createMarker(currentSegment, "0");
			setMarker(newMarker);
		}else if(lastSegment != null && lastSegment.getApproved() && getMarkSet().getMarkers().size()>0){
			Marker lastMarker = getMarkSet().getMarkers().get(getMarkSet().getMarkers().size()-1); 
			getClusterService().learn(lastSegment, ctx);
//			String className = getClusterService().getClassName(lastSegment, ctx);
			syncMarker(lastMarker, lastSegment, "");
		}else{
			log.debug("[changePointLastApproved] already approved {0}. adding current {1}",
					lastSegment,
					currentSegment);
			
			log.debug("[changePointLastApproved] adding {0}", currentSegment);
			ctx.getExtremeSegments().add(currentSegment);
			ctx.setCurrentSegment(createExtremeSegment(ctx.getPreviousValue()));
			return;
		}
		

	}
	
	/**
	 * 
	 * @param ctx
	 */
	public void changePoint(ExtremeSegmentsOnlineCtx ctx){

		ExtremeEntry changeEntry = ctx.prevSegmentEntry;
		
		//record history
		ExtremeSegment currentSegment = onlineCtx.getCurrentSegment();
		ExtremeSegment lastSegment = onlineCtx.getExtremeSegments().getLast();
		if(lastSegment!=null){
			if(lastSegment.getEndEntry() == null){
				lastSegment.setEndEntry(changeEntry);
				log.debug("[changePoint] lastSegment end: {0}",
						changeEntry);
			}
		}
		if(currentSegment != null){
			if(currentSegment.getStartEntry()==null){
				currentSegment.setStartEntry(changeEntry);
				log.debug("[changePoint] setting start: {0}",
						currentSegment);
			}else{
				currentSegment.setEndEntry(changeEntry);
				log.debug("[changePoint] adding {0}",
						currentSegment);
				ctx.getExtremeSegments().add(currentSegment);
				currentSegment = createExtremeSegment(ctx.getPreviousValue());
				ctx.setCurrentSegment(currentSegment);
				currentSegment.setStartEntry(changeEntry);

			}
			log.debug("[changePoint] updated currentSegment: {0}",
					currentSegment);
		}
	}
	/**
	 * 
	 * @param ctx
	 */
	public void join(ExtremeSegmentsOnlineCtx ctx){
//		ExtremeEntry changeEntry = ctx.prevSegmentEntry;
		ExtremeSegment currentSegment = onlineCtx.getCurrentSegment();

		ExtremeSegment lastSegment = onlineCtx.getExtremeSegments().removeLast();
		if(currentSegment != null){
			log.debug("[join]++++ on {2} [{3}]; current: {1}; last: {0}; ", 
					lastSegment, 
					currentSegment, 
					onlineCtx.getIndex(),
					onlineCtx.getMarkerState());
			
			lastSegment.getValues().addAll(currentSegment.getValues());
			lastSegment.getPeakEntries().add(currentSegment.getPeakEntry());
			
			Float maxValue = lastSegment.getPeakEntry().getValue();
			for (ExtremeEntry extremeEntry : lastSegment.getPeakEntries()) {
				if(maxValue<extremeEntry.getValue()){
					maxValue = extremeEntry.getValue();
					lastSegment.setPeakEntry(extremeEntry);
				}
			}
			lastSegment.setEndEntry(null);
//			currentSegment = createExtremeSegment(ctx.getPreviousValue());
			ctx.setCurrentSegment(lastSegment);
			log.debug("[join]--- on {2} [{3}]; current: {1}; last: {0}; ", 
					lastSegment, 
					currentSegment, 
					onlineCtx.getIndex(),
					onlineCtx.getMarkerState());
		}
	}
	/**
	 * 
	 * @param marker
	 * @return
	 */
	public void appendMarker(Marker appendMarker){
		if(validateMarker(appendMarker)){
			log.debug("[appendMarker] appendMarker: {0}", appendMarker);
                        appendMarker.setLength(appendMarker.getLength()-5);
			getMarkSet().getMarkers().add(appendMarker);
                        log.debug("[appendMarker]markers: {0}", getMarkSet().getMarkers());
		}
	}
	/**
	 * 
	 * @param segment
	 * @param className
	 * @return
	 */
	protected Marker createMarker(ExtremeSegment segment, String className){
		Marker newMarker = new Marker();
		syncMarker(newMarker, segment, className);
		return newMarker;
	}
	/**
	 * 
	 * @param descyncedMarker
	 * @param segment
	 * @param className
	 */
	protected void syncMarker(Marker descyncedMarker, ExtremeSegment segment, String className){
		if(segment.getStartEntry() == null){
			return;
		}
		
//		int end = 0;
//		if(segment.getEndEntry() != null){
//			end = segment.getEndEntry().getIndex();	
//		}
		log.debug("[syncMarker]size of segment {0}", segment.getValues().size());

		descyncedMarker.setLabel(MessageFormat.format("{0};{1}:{2}",
					segment.getStartEntry().getIndex() ,
					segment.getValues().size(), className));

		
		Integer startSample = segment.getStartEntry().getIndex()-1;
		Long startTime = getOutputValues().indextoMils(startSample);
		descyncedMarker.setStart(startTime);
		descyncedMarker.getExtractionData().setStartSampleNum(startSample.longValue());

		if(segment.getEndEntry() !=null){
		
			Integer endSample = segment.getEndEntry().getIndex()-1;
			Long endTime = getOutputValues().indextoMils(endSample);
			descyncedMarker.setEnd(endTime);
			descyncedMarker.getExtractionData().setEndSampleNum(endSample.longValue());
		}
	}
	/**
	 * 
	 * @param valitateMarker
	 * @return
	 */
	public boolean validateMarker(Marker valitateMarker){
		for (Marker iMarker : getMarkSet().getMarkers()) {
			if(iMarker.getStart().equals(valitateMarker.getStart())){
				log.error("[approveMarker]conflicts " + iMarker + " with " + valitateMarker);
//				log.error("[approveMarker]join conflicted " + iMarker + " with " + marker);
				return false;
			}else if(iMarker.getEnd()>valitateMarker.getStart()){
				log.error("[approveMarker]conflicts " + iMarker + " with " + valitateMarker);
				return false;
			}
		}
		return true;
	}
	

	/**
	 * process segment
	 * @param ctx
	 */
	public void processSegment(ExtremeSegmentsOnlineCtx ctx){
		if(ctx.getCurrentSegment()==null){
			return;
		}
		onlineCtx.setMarkerState(state.segment);
		syncMarker(getMarker(), ctx.getCurrentSegment(), "0");
	}
	
	public void deleteSegment(ExtremeSegmentsOnlineCtx ctx){
		ExtremeSegment currentSegment = ctx.getCurrentSegment();
		ExtremeSegment lastSegment = onlineCtx.getExtremeSegments().removeLast();
		if(lastSegment==null){
			return;
		}
//		getClusterService().learn(lastSegment, ctx);
		log.debug("[deleteSegment] not adding {0}", getMarker());
		Marker newMarker = createMarker(currentSegment, "0");
		log.debug("[deleteSegment] created {0}", newMarker);
		setMarker(newMarker);
		onlineCtx.setMarkerState(state.segment);
	}

	
	
	protected ExtremeSegment createExtremeSegment(Float startValue){
		ExtremeSegment newExtremeSegment = new ExtremeSegment();
		newExtremeSegment.setValues(new FrameValues());
		newExtremeSegment.getValues().setSampleRate(getOutputValues().getSampleRate());
		newExtremeSegment.getValues().add(startValue);
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

	public ExtremeOnlineClusterService getClusterService() {
		if(clusterService == null){
			clusterService = 
				ExtremeOnClassifierServiceFactory.createClusterService();
		}
		return clusterService;
	}

	public void setClusterService(ExtremeOnlineClusterService clusterService) {
		this.clusterService = clusterService;
	}



}

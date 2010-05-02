package org.spantus.extract.segments.online;

import java.text.MessageFormat;
import java.util.List;

import org.spantus.core.FrameValues;
import org.spantus.core.marker.Marker;
import org.spantus.core.threshold.AbstractClassifier;
import org.spantus.extract.segments.offline.ExtremeEntry;
import org.spantus.extract.segments.offline.ExtremeOfflineClassifier;
import org.spantus.extract.segments.offline.ExtremeSegment;
import org.spantus.extract.segments.offline.ExtremeEntry.FeatureStates;
import org.spantus.extract.segments.online.cluster.ExtremeOnlineClusterService;
import org.spantus.extract.segments.online.rule.ClassifierRuleBaseEnum;
import org.spantus.extract.segments.online.rule.ClassifierRuleBaseService;
import org.spantus.extract.segments.online.rule.ClassifierRuleBaseEnum.state;
import org.spantus.logger.Logger;
import org.spantus.utils.StringUtils;

public class ExtremeOnlineClassifier extends AbstractClassifier{

	private Logger log = Logger.getLogger(ExtremeOnlineClassifier.class); 
	private ExtremeSegmentsOnlineCtx onlineCtx;
//	private ExtremeSegment extremeSegment;
	private ClassifierRuleBaseService ruleBaseService;
	private ExtremeOnlineClusterService clusterService;
	
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
	/**
	 * 
	 */
	@Override
	public void flush() {
		super.flush();
		log.debug("[flush]");
		log.debug("[flush] markers {0}", getMarkSet().getMarkers());
		
		ExtremeSegment current = onlineCtx.getCurrentSegment();
		
		if(current!= null &&
				current.getPeakEntry() != null){
			changePointCurrentApproved(onlineCtx);
		}else if(onlineCtx.getExtremeSegments().size()>0 &&
				onlineCtx.getExtremeSegments().getLast() != null
				&& onlineCtx.getExtremeSegments().getLast().getPeakEntry() != null
				&& !onlineCtx.getExtremeSegments().getLast().approved){
			changePointLastApproved(onlineCtx);
			
//			ExtremeSegment current = onlineCtx.getCurrentSegment();
//			if(getMarker() == null){
//				startMarker(onlineCtx);
//			}
//			//endMarkerApproved(onlineCtx);
//			String className = getClusterService().getClassName(current, onlineCtx);
//			log.debug("[flush] current {0}", current);
//			if(current != null && current.getPeakEntry() != null && current.getEndEntry() == null){
//				ExtremeEntry entry = new ExtremeEntry(onlineCtx.getIndex().intValue(),
//						onlineCtx.getPrevious(), SignalStates.min);
//				current.setEndEntry(entry);
//				approveMarker(onlineCtx.getIndex(), current, className);
//				log.debug("[flush] current {0}", current);
//			}
//			
//			
//		}
//		if(log.isDebugMode()){
//			StringBuilder sb = new StringBuilder();
//			for (SegmentInnerData innerData : onlineCtx.semgnetFeatures) {
//				sb.append(innerData.getArea()).append(",").append(innerData.getLength())
//				.append(",").append(innerData.getPeaks())
//				.append("\n");
//			}
//			log.error("\n" + sb);
		}
		log.debug("[flush] markers {0}", getMarkSet().getMarkers());
		getThresholdValues().addAll(ExtremeOfflineClassifier.refreshThreasholdInfo(getMarkSet(), getOutputValues()));
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
		//process data
		processResult(onlineCtx.getCurrentSegment());
		
//		updateLastExtremeSegment(onlineCtx, onlineCtx.getCurrentSegment());
		
		//updated iterative data
		onlineCtx.setPreviousValue(value);
		 onlineCtx.increase();
//		 if(getMarker()==null){
//			 getThresholdValues().add(value);
//		 }else {
//			 getThresholdValues().add(getThresholdValues().getMinValue());
//		}
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
	
//	protected void onMinFound(Integer index, float previous, float value){
//		log.debug("[onMinFound]found min on {0} value {1}->{2}",
//				index, previous, value);
//		
//		if(onlineCtx.getCurrentSegment() == null){
//			//first min
//			onlineCtx.setCurrentSegment(createExtremeSegment());
//			ExtremeEntry startEntry = new ExtremeEntry(index.intValue(),
//					previous, SignalStates.min);
//			onlineCtx.getCurrentSegment().setStartEntry(startEntry);
////			log.debug("[onMinFound]+starting {0}",
////					extremeSegment);
//		}else{
//			//last min
//			ExtremeEntry entry = new ExtremeEntry(index.intValue(),
//					previous, SignalStates.min);
//			onlineCtx.getCurrentSegment().setEndEntry(entry);
////			log.debug("[onMinFound]+starting {0}",
////					extremeSegment);
//		}
//	}

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
			getClusterService().learn(last, onlineCtx);
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
			currentSegment.approved = true;
			ctx.getExtremeSegments().add(currentSegment);
			//learn and get class
			getClusterService().learn(currentSegment, ctx);
			String className = getClusterService().getClassName(currentSegment, ctx);
			//clreate marker info
			if(currentMarker == null){
				currentMarker = createMarker(currentSegment, className);
			}else {
				syncMarker(currentMarker, currentSegment, className);
			}
			if(!"0".equals(className)){
				appendMarker(currentMarker);
			}else{
				log.debug("[changePointLastApproved]not appended {0}", currentMarker);
			}
		}
		
		//new segment and marker
		ExtremeSegment newSegment = createExtremeSegment();
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
		
		if(lastSegment != null && !lastSegment.approved ){
//			lastSegment.setEndEntry(ctx.getCurrentSegment().getStartEntry());
			log.debug("[changePointLastApproved] ending {0}",
					lastSegment);
			if(ctx.getCurrentSegment().getEndEntry()==null){
				return;
			}
			lastSegment.approved = true;
			getClusterService().learn(lastSegment, ctx);
			String className = getClusterService().getClassName(lastSegment, ctx);
			syncMarker(currentMarker, lastSegment, className);
			if(!"0".equals(className)){
				appendMarker(currentMarker);
			}else{
				log.debug("[changePointLastApproved]not appended {0}", currentMarker);
			}
			Marker newMarker = createMarker(ctx.getCurrentSegment(), "0");
			setMarker(newMarker);
		}else{
			log.debug("[changePointLastApproved] already approved {0}. adding current {1}",
					lastSegment,
					ctx.getCurrentSegment());
			
			log.debug("[changePointLastApproved] addeding {0}",
					ctx.getCurrentSegment());
			ctx.getExtremeSegments().add(ctx.getCurrentSegment());
			ctx.setCurrentSegment(createExtremeSegment());
			return;
		}


		
		//new segment and marker
//		ExtremeSegment newSegment = createExtremeSegment();
//		newSegment.setStartEntry(changeEntry.clone());
//		log.debug("[changePointApproved] starting {0}",
//					newSegment);
//		Marker newMarker = createMarker(newSegment, "1");
//
//		setMarker(newMarker);
//		onlineCtx.setCurrentSegment(newSegment);
		

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
			}else{
				currentSegment.setEndEntry(changeEntry);
				log.debug("[changePointLastApproved] addeding {0}",
						ctx.getCurrentSegment());
				ctx.getExtremeSegments().add(currentSegment);
				currentSegment = createExtremeSegment();
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
		ExtremeEntry changeEntry = ctx.prevSegmentEntry;
		ExtremeSegment currentSegment = onlineCtx.getCurrentSegment();
		ExtremeSegment lastSegment = onlineCtx.getExtremeSegments().getLast();
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
			currentSegment = createExtremeSegment();
			ctx.setCurrentSegment(currentSegment);
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
			log.debug("[appendMarker] {0}", appendMarker);
			getMarkSet().getMarkers().add(appendMarker);
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
	protected void syncMarker(Marker descyncedMarker, ExtremeSegment segment, String className){
		if(segment.getStartEntry() == null){
			return;
		}
		
		descyncedMarker.setLabel(MessageFormat.format("{0}:{1}",
					segment.getStartEntry().getIndex() , className));

		
		Integer startSample = segment.getStartEntry().getIndex();
		Long startTime = getOutputValues().indextoMils(startSample);
		descyncedMarker.setStart(startTime);
		descyncedMarker.getExtractionData().setStartSampleNum(startSample.longValue()-1);

		if(segment.getEndEntry() !=null){
		
			Integer endSample = segment.getEndEntry().getIndex();
			Long endTime = getOutputValues().indextoMils(endSample);
			descyncedMarker.setEnd(endTime);
			descyncedMarker.getExtractionData().setEndSampleNum(endSample.longValue()-1);
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
//				iMarker.setLength(marker.getLength());
//				iMarker.setLabel(marker.getLabel());
//				iMarker.getExtractionData().setLengthSampleNum(marker.getExtractionData().getLengthSampleNum());
				return false;
			}else if(iMarker.getEnd()>valitateMarker.getStart()){
				log.error("[approveMarker]conflicts " + iMarker + " with " + valitateMarker);
				return false;
			}
		}
		return true;
//		if("0".equals(className)){
//			log.debug("[approveMarker] marker {3}is not valid class: {0} on [{1};{2}] ", 
//					className,
//					marker.getExtractionData().getStartSampleNum(),
//					(marker.getExtractionData().getStartSampleNum() + marker.getExtractionData().getLengthSampleNum()),
//					marker
//					);
//			
//			valid = false;
//		}
//		
//		if(valid){
//			log.debug("[endMarkerApproved] !!!!!! adding marker {0}", marker );
//			
//			
	}
	
//	public void startMarkerApproved(ExtremeSegmentsOnlineCtx ctx){
//		endMarkerApproved(ctx);
//		if(getMarker()==null){
//			startMarker(ctx);
//		}
//		ExtremeSegment segment = ctx.getCurrentSegment();
//		Integer startSample = segment.getStartEntry().getIndex();
//		Long time = getOutputValues().indextoMils(startSample);
//		setMarker(getFreshMarker());
//		setFreshMarker(null);
//		log.debug("[startMarkerApproved]started new marker" + getMarker());
//		
//		for (IClassificationListener listener : getClassificationListeners()) {
//			listener.onSegmentStarted(
//					new SegmentEvent(getName(),time,getMarker(), ctx.getIndex().longValue()));
//		}
//		
//		onlineCtx.setMarkerState(state.segment);
//	}
//	
//	public void startMarker(ExtremeSegmentsOnlineCtx ctx){
//		ExtremeSegment segment = ctx.getCurrentSegment();
//		Integer startSample = segment.getStartEntry().getIndex();
//		Long time = getOutputValues().indextoMils(startSample);
//		Marker freshMarker = new Marker();
//		freshMarker.setLabel("" + (segment.getStartEntry().getIndex()));
//		freshMarker.setStart(time);
//		freshMarker.getExtractionData().setStartSampleNum(startSample.longValue()-1);
//		setFreshMarker(freshMarker);
//		onlineCtx.setMarkerState(state.start);
//	}
//	/**
//	 * end marker found. prepare for approve {@link #endMarkerApproved(ExtremeSegmentsOnlineCtx)}
//	 * @param ctx
//	 */
//	public void endMarker(ExtremeSegmentsOnlineCtx ctx){
//		log.debug("[endMarker]!");
//		Marker marker = getMarker();
//		if(marker == null){
//			log.debug("Marker not initialized");
//			return;
//		}
//		ExtremeSegment last = null;
//		if(ctx.getExtremeSegments().size()>0){
//			 last = ctx.getExtremeSegments().getLast();
//		}else{
////			onlineCtx.learn(ctx.getCurrentSegment());
////			return;
//			 last = ctx.getCurrentSegment();
//			 
//		}
//		Integer startSample = last.getStartEntry().getIndex();
//		
//		//after join lost will lose its end index
//		if(last.getEndEntry() == null){
//			last.setEndEntry(ctx.getCurrentSegment().getEndEntry().clone());
//			onlineCtx.setCurrentSegment(createExtremeSegment());
//		}
//		Integer endSample = last.getEndEntry().getIndex();
//		
//		Long startTime = getOutputValues().indextoMils(startSample);
//		Long endTime = getOutputValues().indextoMils(endSample);
//		
//		String className = getClusterService().getClassName(last, ctx);
//		
//		marker.setLabel(MessageFormat.format("{0}:{1}",last.getStartEntry().getIndex() , className)
//				);
//		marker.setStart(startTime);
//		marker.setEnd(endTime);
//		marker.getExtractionData().setStartSampleNum(startSample.longValue()-1);
//		marker.getExtractionData().setEndSampleNum(endSample.longValue()-1);
//		onlineCtx.setMarkerState(state.end);
//	}
//	/**
//	 * if marker end found just update data end add to {@link #getMarkSet()}
//	 * 
//	 * @param ctx
//	 */
//	public void endMarkerApproved(ExtremeSegmentsOnlineCtx ctx){
//		ExtremeSegment last = null;
//		if(ctx.getExtremeSegments().size()>0){
//			 last = ctx.getExtremeSegments().getLast();
//		}else{
////			onlineCtx.learn(ctx.getCurrentSegment());
////			return;
//			 last = ctx.getCurrentSegment();
//		}
//		getClusterService().learn(last,ctx);
//		String className = getClusterService().getClassName(last, ctx);
//		approveMarker(ctx.getIndex(), last, className);
//		
//	}
//	/**
//	 * 
//	 * @param index
//	 * @param last
//	 * @param className
//	 */
//	public void approveMarker(Integer index, ExtremeSegment last, String className){
//		Marker marker = getMarker();
//		if(last.joined){
//			log.debug("[approveMarker]Already joined {0}", last);
//			return;
//		}
//		if(marker == null){
//			startMarker(onlineCtx);
//			marker = getFreshMarker();
//		}
//		if(marker == null || last.getEndEntry() == null){
//			log.debug("[approveMarker]Marker not initialized marker:{0}; lastEnd: {1}", marker, last.getEndEntry());
//			return;
//		}
//		
//		Integer startSample = last.getStartEntry().getIndex();
//		Integer endSample = last.getEndEntry().getIndex();
//		
//		Long startTime = getOutputValues().indextoMils(startSample);
//		Long endTime = getOutputValues().indextoMils(endSample);
//		
//		marker.setLabel(MessageFormat.format("{0}:{1}",last.getStartEntry().getIndex() , className)
//				);
//		marker.setStart(startTime);
//		marker.setEnd(endTime);
//		marker.getExtractionData().setStartSampleNum(startSample.longValue()-1);
//		marker.getExtractionData().setEndSampleNum(endSample.longValue()-1);
//		
//		boolean valid = true;
//		
//		for (Marker iMarker : getMarkSet().getMarkers()) {
//			if(iMarker.getStart().equals(marker.getStart())){
//				log.error("[approveMarker]conflicts " + iMarker + " with " + marker);
////				log.error("[approveMarker]join conflicted " + iMarker + " with " + marker);
////				iMarker.setLength(marker.getLength());
////				iMarker.setLabel(marker.getLabel());
////				iMarker.getExtractionData().setLengthSampleNum(marker.getExtractionData().getLengthSampleNum());
//				valid = false;
//			}else if(iMarker.getEnd()>startTime){
//				log.error("[approveMarker]conflicts " + iMarker + " with " + marker);
//				valid = false;
//			}
//		}
//		if("0".equals(className)){
//			log.debug("[approveMarker] marker {3}is not valid class: {0} on [{1};{2}] ", 
//					className,
//					marker.getExtractionData().getStartSampleNum(),
//					(marker.getExtractionData().getStartSampleNum() + marker.getExtractionData().getLengthSampleNum()),
//					marker
//					);
//			
//			valid = false;
//		}
//		
//		if(valid){
//			log.debug("[endMarkerApproved] !!!!!! adding marker {0}", marker );
//			getMarkSet().getMarkers().add(marker);
//			
//		}
//		setMarker(null);
//		onlineCtx.setMarkerState(null);
//		for (IClassificationListener listener : getClassificationListeners()) {
//			listener.onSegmentEnded(
//					new SegmentEvent(getName(),startTime,getMarker(),index.longValue()));
//		}
//	}
	
//	/**
//	 * Join to segments
//	 * @param ctx
//	 */
//	public void join(ExtremeSegmentsOnlineCtx ctx){
//		ExtremeSegment current = ctx.getCurrentSegment();
//		ExtremeSegment last = ctx.getExtremeSegments().getLast();
//		
//		log.debug("[join]++++ on {2} [{3}]; current: {1}; last: {0}; ", 
//				last, 
//				current, 
//				onlineCtx.getIndex(),
//				onlineCtx.getMarkerState());
//		
//		last.getValues().addAll(current.getValues());
//		last.getPeakEntries().add(current.getPeakEntry());
//		
//		Float maxValue = last.getPeakEntry().getValue();
//		for (ExtremeEntry extremeEntry : last.getPeakEntries()) {
//			if(maxValue<extremeEntry.getValue()){
//				maxValue = extremeEntry.getValue();
//				last.setPeakEntry(extremeEntry);
//			}
//		}
//		
//		
//		//segment not finished yet
//		if(current.getEndEntry() == null){
////			current.getEndEntry();
////			last.setEndEntry(null);
//			current = last.clone();
//			current.setEndEntry(null);
//			ctx.setCurrentSegment(current);
//			last.joined = true;
//			onlineCtx.setMarkerState(state.segment);
//			
//		}else{
//			
//			last.setEndEntry(current.getEndEntry());	
//			current=createExtremeSegment();
//			ctx.setCurrentSegment(current);
//			if(current.getEndEntry() !=null){
//				onlineCtx.getCurrentSegment().setStartEntry(current.getEndEntry().clone());
//			}else {
//				onlineCtx.getCurrentSegment().setStartEntry(last.getStartEntry().clone());
//			}
//			startMarker(onlineCtx);
//			onlineCtx.setMarkerState(ClassifierRuleBaseEnum.state.segment);
//		}
//		log.debug("[join]---- on {2} [{3}]; current: {1}; last: {0}; ", 
//				last, 
//				current, 
//				onlineCtx.getIndex(),
//				onlineCtx.getMarkerState());
//		
//	}
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
	
//	protected void updateLastExtremeSegment(ExtremeSegmentsOnlineCtx ctx, ExtremeSegment lastSegment){
//		//if this not change point or first element found
//		if( lastSegment == null || !Boolean.TRUE.equals(ctx.getFoundEndSegment())){
//			return;
//		}
//		//end is not initialized for first segment
//		if(lastSegment.getEndEntry()==null){
//			return;
//		}
//
//		//create new segment
////		ExtremeSegment lastSegment = onlineCtx.getCurrentSegment();
//		onlineCtx.getExtremeSegments().add(lastSegment);
//		log.debug("[updateLastExtremeSegment]created {0}",
//				onlineCtx.getCurrentSegment());
//		onlineCtx.setCurrentSegment(createExtremeSegment());
//		onlineCtx.getCurrentSegment().setStartEntry(lastSegment.getEndEntry().clone());
////		return newExtremeSegment;
//	}
	
	
	
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

//	public Marker getFreshMarker() {
//		return freshMarker;
//	}
//
//	public void setFreshMarker(Marker freshMarker) {
//		this.freshMarker = freshMarker;
//	}


}

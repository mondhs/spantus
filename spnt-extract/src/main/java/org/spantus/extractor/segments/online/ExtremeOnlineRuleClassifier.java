package org.spantus.extractor.segments.online;

import java.text.MessageFormat;
import java.util.List;

import org.spantus.core.FrameValues;
import org.spantus.core.marker.Marker;
import org.spantus.core.threshold.AbstractClassifier;
import org.spantus.extractor.segments.offline.ExtremeEntry;
import org.spantus.extractor.segments.offline.ExtremeEntry.FeatureStates;
import org.spantus.extractor.segments.offline.ExtremeOfflineClassifier;
import org.spantus.extractor.segments.offline.ExtremeSegment;
import org.spantus.extractor.segments.online.rule.ClassifierRuleBaseEnum;
import org.spantus.extractor.segments.online.rule.ClassifierRuleBaseEnum.state;
import org.spantus.extractor.segments.online.rule.ClassifierRuleBaseService;
import org.spantus.logger.Logger;
import org.spantus.utils.Assert;
import org.spantus.utils.StringUtils;

public class ExtremeOnlineRuleClassifier extends AbstractClassifier {

	private Logger log = Logger.getLogger(ExtremeOnlineRuleClassifier.class);
	private ExtremeSegmentsOnlineCtx onlineCtx;

	private ClassifierRuleBaseService ruleBaseService;

	// private ExtremeOnlineClusterService clusterService;

	public ExtremeOnlineRuleClassifier() {
		onlineCtx = new ExtremeSegmentsOnlineCtx();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.spantus.core.extractor.IExtractorListener#afterCalculated(java.lang
	 * .Long, org.spantus.core.FrameValues)
	 */
	public void afterCalculated(Long sample, FrameValues result) {
		// entry class point
		for (Double value : result) {
			getThresholdValues().updateMinMax(value);
			processValue(onlineCtx, value);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.spantus.core.threshold.AbstractClassifier#flush()
	 */
	@Override
	public void flush() {
		super.flush();
		log.debug("[flush]");

		endupPendingSegments(getOnlineCtx());

		getThresholdValues().addAll(
				ExtremeOfflineClassifier.refreshThreasholdInfo(getMarkSet(),
						getOutputValues()));
	}

	/**
	 * invoced from flush
	 * 
	 * @param ctx
	 */
	protected void endupPendingSegments(ExtremeSegmentsOnlineCtx ctx) {
		log.debug("[endupPendingSegments] markers {0} before end up",
				getMarkSet().getMarkers().size());
		ExtremeSegment current = ctx.getCurrentSegment();

		if (current != null && current.getPeakEntry() != null) {
			// initSegment(ctx);
			if (current.getEndEntry() == null) {
				updateEndSegment(current, new ExtremeEntry(ctx.getIndex(), ctx.getPreviousValue(), FeatureStates.min));
			}
			appendSegment(ctx, current, ctx.getSegmentEntry());
		} else if (ctx.getExtremeSegments().size() > 0) {
			ExtremeSegment last = ctx.getExtremeSegments().getLast();
			if (last.getEndEntry() == null) {
				ExtremeEntry endEntry = new ExtremeEntry(ctx.getIndex(),
						ctx.getPreviousValue(), FeatureStates.min);
				updateEndSegment(last, endEntry);
			}
			if (last != null && last.getPeakEntry() != null
					&& !last.getApproved()) {
				changePointLastApproved(ctx);
			}
		}
		log.debug("[endupPendingSegments] markers {0} after end up",
				getMarkSet().getMarkers().size());
	}

	/**
	 * 
	 * @param ctx
	 * @param sample
	 * @param value
	 */
	protected void processValue(ExtremeSegmentsOnlineCtx ctx, Double value) {
		Integer index = ctx.getIndex();
		log.debug("[processValue] {0} value {1}->{2}", index,
				ctx.getPreviousValue(), value);
		// starting point, previous not found

		ExtremeEntry entry = null;
		if (ctx.getPreviousValue() == null) {
			// first value
			log.debug("[processFirstValue]first: {0} on {1}",
					ctx.getPreviousValue(), index);
		}else if (value.compareTo(ctx.getPreviousValue()) == 0) {
			// signal stable, should be only for models only
			log.debug("[processValue]found stable");
			entry = onStableFound(index, ctx.getPreviousValue(), value);
		} else if (value.compareTo(ctx.getPreviousValue()) > 0
				&& (ctx.getFeatureDecrease() || ctx.getFeatureStable())) {
			// min processing
			entry = onMinFound(index, ctx.getPreviousValue(), value);
		} else if (value.compareTo(ctx.getPreviousValue()) < 0
				&& ctx.getFeatureIncrease()) {
			// max processing
			entry = onMaxFound(index, ctx.getPreviousValue(), value,
					ctx.getCurrentSegment());
		}

		if (entry != null) {
			ctx.setSegmentEntry(entry);
			
			log.debug("[processValue]found prev: {0}; now: {1}",
					ctx.getPrevSegmentEntry(), ctx.getSegmentEntry());

			if(Boolean.TRUE.equals( ctx.getFeatureStable())){
				ctx.incStableCount();
			}else{
				ctx.resetStableCount();
			}
			
			// process data. This is the place where rules engine starts control
			// /////////////////////////////////////
			processResult(ctx, ctx.getCurrentSegment());
			// /////////////////////////////////////
		} else {
			log.debug(
					"[processValue]entry null;  do not process value: {0}=>{1}; ",
					index, value);
		}
		if(ctx.getCurrentSegment() != null){
			ctx.getCurrentSegment().getValues().add(value);
			log.debug("[processValue] values: {0} ", ctx.getCurrentSegment().getValues());
		}
		ctx.setPreviousValue(value);
		// updated iterative data
		ctx.increase();

	}

	/**
	 * 
	 * @param index
	 * @param previous
	 * @param value
	 * @return
	 */
	protected ExtremeEntry onStableFound(Integer index, Double previous,
			Double value) {
		ExtremeEntry entry = new ExtremeEntry(index, previous,
				FeatureStates.stable);
		log.debug("[onStableFound]found stable on {0} value {1}->{2}", index,
				previous, value);
		return entry;
	}

	/**
	 * 
	 * @param index
	 * @param previous
	 * @param value
	 * @return
	 */
	protected ExtremeEntry onMinFound(Integer index, Double previous,
			Double value) {
		ExtremeEntry entry = new ExtremeEntry(index, previous,
				FeatureStates.min);
		log.debug("[onMinFound]found min on {0} value {1}->{2}", index,
				previous, value);
		return entry;
	}

	/**
	 * 
	 * @param index
	 * @param previous
	 * @param value
	 * @return
	 */
	protected ExtremeEntry onMaxFound(Integer index, Double previous,
			Double value, ExtremeSegment currentSegment) {
		ExtremeEntry entry = new ExtremeEntry(index, previous,
				FeatureStates.max);
		log.debug("[onMaxFound]found max on {0} value {1}->{2}", index,
				previous, value);
		// if extreme segment not created skip it
		if (currentSegment != null) {
			ExtremeEntry inEntry = new ExtremeEntry(index.intValue(), previous,
					FeatureStates.max);
			// if current peak is less than was before replace it
			updatePeakSegment(currentSegment, inEntry);
		}
		return entry;
	}

	/**
	 * 
	 * @param extremeSegment
	 */
	protected void processResult(ExtremeSegmentsOnlineCtx ctx,
			ExtremeSegment extremeSegment) {
		if (getRuleBaseService() == null)
			return;
		ExtremeSegment last = null;
		if (ctx.getExtremeSegments().size() > 0) {
			last = ctx.getExtremeSegments().getLast();
		}
		// log.debug("[processResult]+++");
		log.debug(
				"[processResult] on {2} [{3}]; current: {1}; segments: {0}; ",
				last, ctx.getCurrentSegment(), ctx.getIndex(),
				ctx.getMarkerState());

		String actionStr = getRuleBaseService().testOnRuleBase(ctx);
		ClassifierRuleBaseEnum.action anAction = null;
		if (StringUtils.hasText(actionStr)) {
			anAction = ClassifierRuleBaseEnum.action.valueOf(actionStr);
		}
		log.debug("[processResult]>>>action {0}", anAction);
		switch (anAction) {
		case initSegment:
			initSegment(ctx);
			break;
		case changePointLastApproved:
			changePointLastApproved(ctx);
			break;
		case changePoint:
			changePoint(ctx);
			break;
		case processSignal:
			processSegment(ctx);
			break;
		case processNoise:
			break;
		case join:
			join(ctx);
			break;
		case delete:
			deleteSegment(ctx);
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
	public void initSegment(ExtremeSegmentsOnlineCtx ctx) {
		ExtremeEntry changeEntry = ctx.getPrevSegmentEntry();
		if (changeEntry == null) {
			return;
		}
		// Marker currentMarker = getMarker();

		// record history
		ExtremeSegment currentSegment = onlineCtx.getCurrentSegment();
//		if (currentSegment != null && currentSegment.getPeakEntry() != null) {
//			appendSegment(ctx, currentSegment, changeEntry);
//		}

		// new segment and marker
		ExtremeSegment newSegment = createExtremeSegment(ctx
				.getPrevSegmentEntry());
		updatePeakSegment(newSegment, ctx.getSegmentEntry());
		log.debug("[initSegment] starting {0} [{1}]", newSegment, newSegment.getValues());
		newSegment.getValues().add(ctx.getSegmentEntry().getValue());
		// Marker newMarker = createMarker(newSegment, "0");

		// setMarker(newMarker);
		onlineCtx.setCurrentSegment(newSegment);
	}

	/**
	 * 
	 * @param ctx
	 */
	public void changePointLastApproved(ExtremeSegmentsOnlineCtx ctx) {
		ExtremeEntry changeEntry = ctx.getPrevSegmentEntry();
		if (changeEntry == null) {
			return;
		}
		// Marker currentMarker = getMarker();

		// record history
		ExtremeSegment lastSegment = onlineCtx.getExtremeSegments().size() > 0 ? onlineCtx
				.getExtremeSegments().getLast() : null;
		ExtremeSegment currentSegment = ctx.getCurrentSegment();
		if (lastSegment == null) {
			log.debug(
					"[changePointLastApproved] last segment is null. skip processing. current:  {0}",
					currentSegment);
			// is apporved but not added to markers
		} else if (lastSegment != null
				&& (!lastSegment.getApproved() || getMarkSet().getMarkers()
						.size() == 0)) {
			log.debug("[changePointLastApproved] ending {0} {1}", lastSegment, lastSegment.getValues());
			lastSegment.setApproved(true);
			getRuleBaseService().learn(lastSegment, ctx);
			if(!lastSegment.getLabel().contains("DELETED")){
				appendMarker(lastSegment);
			}
		} else if (lastSegment != null && lastSegment.getApproved()
				&& getMarkSet().getMarkers().size() > 0) {
			throw new IllegalArgumentException("Not impl");
//			getRuleBaseService().learn(lastSegment, ctx);
		} else {
			throw new IllegalArgumentException("Not impl");
//			log.debug(
//					"[changePointLastApproved] already approved {0}. adding current {1}",
//					lastSegment, currentSegment);
//
//			log.debug("[changePointLastApproved] adding {0} [{1}]", currentSegment, currentSegment.getValues());
//			ctx.getExtremeSegments().add(currentSegment);
//			ctx.setCurrentSegment(createExtremeSegment(ctx.getSegmentEntry()));
//			return;
		}

	}

	/**
	 * 
	 * @param ctx
	 */
	public void changePoint(ExtremeSegmentsOnlineCtx ctx) {

		ExtremeEntry changeEntry = ctx.getPrevSegmentEntry();

		// record history
		ExtremeSegment currentSegment = onlineCtx.getCurrentSegment();
		ExtremeSegment lastSegment = onlineCtx.getExtremeSegments().size() > 0 ? onlineCtx
				.getExtremeSegments().getLast() : null;
				//currentSegment.getStartEntry(): if start found before
		if (currentSegment != null && currentSegment.getStartEntry() != null) {
			Assert.isTrue(currentSegment.getStartEntry() != null,
					"Start sould be setuped");
			updateEndSegment(currentSegment, ctx.getSegmentEntry());
			log.debug("[changePoint] adding {0} [{1}]", currentSegment, currentSegment.getValues());
			ctx.getExtremeSegments().add(currentSegment);
			currentSegment = createExtremeSegment(ctx.getSegmentEntry());
			ctx.setCurrentSegment(currentSegment);
			log.debug("[changePoint] updated currentSegment: {0}",
					currentSegment);
		} else if (lastSegment != null) {
			if (lastSegment.getEndEntry() == null) {
				updateEndSegment(lastSegment, changeEntry);
			}
		}
	}

	/**
	 * 
	 * @param segment
	 * @param entry
	 */
	private void updateEndSegment(ExtremeSegment segment, ExtremeEntry entry) {
		Assert.isTrue(segment.getPeakEntry() != null, "peak should not be null");
		Assert.isTrue(segment.getPeakEntry().getIndex() != entry.getIndex(),
				"peak should not same as end");
//		segment.getValues().add(entry.getValue());
		segment.setEndEntry(entry);
		segment.getExtractionData().setEndSampleNum(
				entry.getIndex().longValue());
		segment.setLength(segment.getCalculatedLength());
		segment.getExtractionData().setEndSampleNum(
				entry.getIndex().longValue());

		String newLabel = MessageFormat.format("{0}:{1}", segment.getStartEntry().getIndex(), segment.getEndEntry().getIndex());
		if(segment.getLabel()!=null){
			newLabel = segment.getLabel()+newLabel;
		}
		segment.setLabel(newLabel);
		
		log.debug("[endSegment] lastSegment end: {0}  [{1}:{2}]", entry,
				segment.getStart(), segment.getEnd());
		
	}

	/**
	 * 
	 * @param segment
	 * @param entry
	 */
	private void updatePeakSegment(ExtremeSegment segment, ExtremeEntry entry) {
		Assert.isTrue(segment.getStartEntry() != null, "start should not be null");
		Assert.isTrue(segment.getStartEntry().getIndex() != entry.getIndex(),
				"start should not same as peak");
		segment.setPeakEntry(entry);
		if (segment.getPeakEntry() != null) {
			segment.setPeakEntry(entry);
		} else {
			if (segment.getPeakEntry().ltV(entry)) {
				segment.setPeakEntry(entry);
			}
		}
		segment.getPeakEntries().add(entry);
	}

	/**
	 * 
	 * @param ctx
	 */
	public void join(ExtremeSegmentsOnlineCtx ctx) {
		// ExtremeEntry changeEntry = ctx.prevSegmentEntry;
		ExtremeSegment currentSegment = onlineCtx.getCurrentSegment();

		ExtremeSegment lastSegment = onlineCtx.getExtremeSegments()
				.removeLast();
		if (currentSegment != null) {
			log.debug("[join]++++ on {2} [{3}]; current: {1}; last: {0}; ",
					lastSegment, currentSegment, onlineCtx.getIndex(),
					onlineCtx.getMarkerState());

			lastSegment.getValues().addAll(currentSegment.getValues());
			for (ExtremeEntry entry : currentSegment.getPeakEntries()) {
				updatePeakSegment(lastSegment, entry);
			}
		
			lastSegment.setEndEntry(null);
//			updateEndSegment(lastSegment, null);
			// currentSegment = createExtremeSegment(ctx.getPreviousValue());
			ctx.setCurrentSegment(lastSegment);
			log.debug("[join]--- on {2} [{3}]; current: {1}; last: {0}; ",
					lastSegment, currentSegment, onlineCtx.getIndex(),
					onlineCtx.getMarkerState());
		}
	}

	public void appendSegment(ExtremeSegmentsOnlineCtx ctx,
			ExtremeSegment currentSegment, ExtremeEntry entry) {

		log.debug("[changePointCurrentApproved] ending {0} [{1}]", currentSegment, currentSegment.getValues());
		currentSegment.setApproved(true);
		ctx.getExtremeSegments().add(currentSegment);
		// learn and get class
		getRuleBaseService().learn(currentSegment, ctx);
		appendMarker(currentSegment);
	}

	/**
	 * 
	 * @param marker
	 * @return
	 */
	public void appendMarker(ExtremeSegment appendMarker) {
		Assert.isTrue(appendMarker.getEndEntry() != null, "End should be set");
		log.error(MessageFormat.format(
				"[appendMarker]append segment  [{0}] ",
				appendMarker.getValues() ));
		
		if (validateMarker(appendMarker)) {
			log.debug("[appendMarker] appendMarker: {0} [{1}:{2}]",
					appendMarker, appendMarker.getStart(),
					appendMarker.getLength());
			getMarkSet().getMarkers().add(appendMarker);
			log.debug("[appendMarker]markers: {0}", getMarkSet().getMarkers());
		} else {
			throw new IllegalArgumentException("Segments Conflicts");
		}
	}

	/**
	 * 
	 * @param valitateMarker
	 * @return
	 */
	public boolean validateMarker(Marker valitateMarker) {
//		log.error(MessageFormat.format(
//				"[validateMarker]valitateMarker   [{0}:{1}]",
//				valitateMarker.getStart(), valitateMarker.getEnd()));
		for (Marker iMarker : getMarkSet().getMarkers()) {
//			log.error(MessageFormat.format(
//					"[validateMarker]iMarker   [{0}:{1}]", iMarker.getStart(),
//					iMarker.getEnd()));
			if (iMarker.getStart().equals(valitateMarker.getStart())) {
				log.error("[validateMarker]conflicts " + iMarker + " with "
						+ valitateMarker);
				return false;
			} else if (iMarker.getEnd() > valitateMarker.getStart()) {
				log.error(MessageFormat.format(
						"[validateMarker]conflicts {0} with {1}: {2}>{3}",
						iMarker, valitateMarker, iMarker.getEnd(),
						valitateMarker.getStart()));
				return false;
			}
		}
		return true;
	}

	/**
	 * process segment
	 * 
	 * @param ctx
	 */
	public void processSegment(ExtremeSegmentsOnlineCtx ctx) {
		if (ctx.getCurrentSegment() == null) {
			return;
		}
		onlineCtx.setMarkerState(state.segment);
		// syncMarker(getMarker(), ctx.getCurrentSegment(), "0");
	}

	public void deleteSegment(ExtremeSegmentsOnlineCtx ctx) {
		ExtremeSegment currentSegment = ctx.getCurrentSegment();
		ExtremeSegment lastSegment = onlineCtx.getExtremeSegments()
				.getLast();
		if (lastSegment == null) {
			return;
		}
		lastSegment.setLabel("DELETED");
		// getClusterService().learn(lastSegment, ctx);
		log.debug("[deleteSegment] not adding {0}", currentSegment);
		changePointLastApproved(ctx);
		// Marker newMarker = createMarker(currentSegment, "0");
		// log.debug("[deleteSegment] created {0}", newMarker);
		// setMarker(newMarker);
//		onlineCtx.setMarkerState(state.segment);
	}

	protected ExtremeSegment createExtremeSegment(ExtremeEntry extremeEntry) {
		ExtremeSegment newExtremeSegment = new ExtremeSegment();
		newExtremeSegment.setStartEntry(extremeEntry);
		newExtremeSegment.getExtractionData().setStartSampleNum(
				extremeEntry.getIndex().longValue());
		newExtremeSegment.setValues(new FrameValues());
		newExtremeSegment.getValues().setSampleRate(
				getOutputValues().getSampleRate());
//		newExtremeSegment.getValues().add(extremeEntry.getValue());
		long start = newExtremeSegment.getValues().indextoMils(
				extremeEntry.getIndex()-1);
		newExtremeSegment.setStart(start);
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

	// public ExtremeOnlineClusterService getClusterService() {
	// if(clusterService == null){
	// clusterService =
	// ExtremeOnClassifierServiceFactory.createClusterService();
	// }
	// return clusterService;
	// }

	// public void setClusterService(ExtremeOnlineClusterService clusterService)
	// {
	// this.clusterService = clusterService;
	// }
	@Deprecated
	public Marker getMarker() {
		throw new IllegalArgumentException("Do not use this");
	}

}

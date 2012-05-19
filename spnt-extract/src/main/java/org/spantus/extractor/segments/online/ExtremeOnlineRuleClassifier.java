package org.spantus.extractor.segments.online;

import java.text.MessageFormat;
import java.util.Deque;
import java.util.Iterator;
import java.util.LinkedList;

import org.spantus.core.FrameValues;
import org.spantus.core.marker.Marker;
import org.spantus.core.threshold.AbstractClassifier;
import org.spantus.core.threshold.IClassificationListener;
import org.spantus.core.threshold.SegmentEvent;
import org.spantus.extractor.segments.ExtremeSegmentServiceImpl;
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

/**
 * 
 * @author mondhs
 * @since 0.3
 * 
 */
public class ExtremeOnlineRuleClassifier extends AbstractClassifier {

	private static final Logger LOG = Logger
			.getLogger(ExtremeOnlineRuleClassifier.class);
	private ExtremeSegmentsOnlineCtx onlineCtx;

	private ClassifierRuleBaseService ruleBaseService;
	private ExtremeSegmentServiceImpl extremeSegmentService;

	private Deque<Boolean> states = new LinkedList<Boolean>();

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
	@Override
	public void afterCalculated(Long sample, FrameValues windowValues,
			FrameValues result) {
		if (result == null) {
			return;
		}
		Assert.isTrue(result.size() == 1);
		Assert.isTrue(windowValues != null);
		Assert.isTrue(windowValues.getFrameIndex() != null);

		// entry class point
		for (Double value : result) {
			processValue(onlineCtx, windowValues, value);
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
		LOG.debug("[flush]");

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
		LOG.debug("[endupPendingSegments] markers {0} before end up",
				getMarkSet().getMarkers().size());
		ExtremeSegment current = ctx.getCurrentSegment();

		if(current== null){
			return;
		}
			
		
		
		
		if (current.getPeakEntry() != null ) {
			//no end of segment
			if( current.getPeakEntry().getIndex().equals(ctx.getSegmentEntry().getIndex())){
				return;
			}
			ctx.getExtremeSegments().add(current);
			current.getValues().removeLast();			
			updateEndSegment(current, ctx.getSegmentEntry());
			
			ctx.getSegmentEntry().setSignalState(FeatureStates.flush);
			processResult(ctx, ctx.getCurrentSegment());
		} else if (ctx.getExtremeSegments().size() > 0) {
			ctx.getSegmentEntry().setSignalState(FeatureStates.flush);
			processResult(ctx, ctx.getCurrentSegment());
		}
		LOG.debug("[endupPendingSegments] markers {0} after end up",
				getMarkSet().getMarkers().size());
	}

	/**
	 * 
	 * @param ctx
	 * @param window
	 * @param sample
	 * @param value
	 */
	protected void processValue(ExtremeSegmentsOnlineCtx ctx,
			FrameValues windowValues, Double value) {
		Integer index = ctx.getIndex() - 1;
		onlineCtx.pushWindowValues(windowValues);
		states.add(false);
		LOG.debug("[processValue] {0} value {1}->{2}", index,
				ctx.getPreviousValue(), value);
		// starting point, previous not found

		ExtremeEntry entry = null;
		if (ctx.getPreviousValue() == null) {
			// first value
			LOG.debug("[processFirstValue]first: {0} on {1}",
					ctx.getPreviousValue(), index);
		} else if (value.compareTo(ctx.getPreviousValue()) == 0) {
			// signal stable, should be only for models only
			LOG.debug("[processValue]found stable");
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

			LOG.debug("[processValue]found prev: {0}; now: {1}",
					ctx.getPrevSegmentEntry(), ctx.getSegmentEntry());

			if (Boolean.TRUE.equals(ctx.getFeatureStable())) {
				ctx.incStableCount();
			} else {
				ctx.resetStableCount();
			}

			// process data. This is the place where rules engine starts control
			// /////////////////////////////////////
			processResult(ctx, ctx.getCurrentSegment());
			// /////////////////////////////////////
		} else {
			LOG.debug(
					"[processValue]entry null;  do not process value: {0}=>{1}; ",
					index, value);
		}
		if (ctx.getCurrentSegment() != null) {
			ctx.getCurrentSegment().getValues().add(value);
			LOG.debug("[processValue] values: {0} ", ctx.getCurrentSegment()
					.getValues());
		} else {
			LOG.debug("[processValue] not adding values: {0}->{1} ", index,
					value);
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
		LOG.debug("[onStableFound]found stable on {0} value {1}->{2}", index,
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
		LOG.debug("[onMinFound]found min on {0} value {1}->{2}", index,
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
		LOG.debug("[onMaxFound]found max on {0} value {1}->{2}", index,
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
		if (getRuleBaseService() == null) {
			return;
		}
		ExtremeSegment last = null;
		if (ctx.getExtremeSegments().size() > 0) {
			last = ctx.getExtremeSegments().getLast();
		}
		LOG.debug("[processResult]+++ {0}", ctx.getIndex());
		LOG.debug(
				"[processResult] on {2} [{3}]; current: {1}; segments: {0}; ",
				last, ctx.getCurrentSegment(), ctx.getIndex(),
				ctx.getMarkerState());

		String actionStr = getRuleBaseService().testOnRuleBase(ctx);
		ClassifierRuleBaseEnum.action anAction = null;
		if (StringUtils.hasText(actionStr)) {
			anAction = ClassifierRuleBaseEnum.action.valueOf(actionStr);
		}
		LOG.debug("[processResult]>>>action {0}", anAction);
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
			processNoise(ctx);
			break;
		case join:
			join(ctx);
			break;
		case delete:
			deleteSegment(ctx);
			break;
		default:
			LOG.error("[processResult]Not impl: " + anAction);
			throw new IllegalArgumentException("Not impl: " + anAction);
		}
		LOG.debug("[processResult]--- {0}", ctx.getIndex());
	}

	/**
	 * 
	 * @param ctx
	 */
	public void processNoise(ExtremeSegmentsOnlineCtx ctx) {
		// ExtremeEntry changeEntry = ctx.getPrevSegmentEntry();

		// record history
		ExtremeSegment currentSegment = onlineCtx.getCurrentSegment();
		// ExtremeSegment lastSegment = onlineCtx.getExtremeSegments().size() >
		// 0 ? onlineCtx
		// .getExtremeSegments().getLast() : null;
		if (currentSegment != null && currentSegment.getPeakEntry() != null
				&& currentSegment.getEndEntry() == null) {
			updateEndSegment(currentSegment, ctx.getSegmentEntry());
		}
	}

	/**
	 * 
	 * @param ctx
	 */
	public void initSegment(ExtremeSegmentsOnlineCtx ctx) {
		ExtremeEntry changeEntry = ctx.getSegmentEntry();
		if (changeEntry == null) {
			return;
		}

		// new segment and marker
		ExtremeSegment newSegment = createExtremeSegment(changeEntry);
		LOG.debug("[initSegment] starting {0} [{1}]", newSegment,
				newSegment.getValues());
		onlineCtx.setCurrentSegment(newSegment);
	}

	/**
	 * 
	 * @param ctx
	 */
	public void changePointLastApproved(ExtremeSegmentsOnlineCtx ctx) {
		ExtremeEntry changeEntry = ctx.getPrevSegmentEntry();
		Assert.isTrue(changeEntry != null);
		// if (changeEntry == null) {
		// return;
		// }

		ExtremeSegment lastSegment = onlineCtx.getExtremeSegments().size() > 0 ? onlineCtx
				.getExtremeSegments().getLast() : null;
		ExtremeSegment currentSegment = ctx.getCurrentSegment();
		
		
		if (lastSegment == null) {
			LOG.debug(
					"[changePointLastApproved] last segment is null. skip processing. current:  {0}",
					currentSegment);
			// is apporved but not added to markers
		} else if (!lastSegment.getApproved() || getMarkSet().getMarkers()
						.size() == 0) {
			LOG.debug("[changePointLastApproved] ending {0} {1}", lastSegment,
					lastSegment.getValues());
			if (!lastSegment.getLabel().contains("DELETED")) {
				lastSegment.setApproved(true);
				getRuleBaseService().learn(lastSegment, ctx);
				appendMarker(lastSegment, ctx);
			}
		} else if (lastSegment.getApproved()
				&& getMarkSet().getMarkers().size() > 0) {
			throw new IllegalArgumentException("Not impl");
		} else {
			throw new IllegalArgumentException("Not impl");
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
		if (FeatureStates.stable.equals(changeEntry.getSignalState())
				&& currentSegment.getPeakEntry() == null) {
			currentSegment = createExtremeSegment(ctx.getSegmentEntry());
			ctx.setCurrentSegment(currentSegment);
			LOG.debug("[changePoint] updated currentSegment: {0}",
					currentSegment);
		} else if (currentSegment != null
				&& currentSegment.getStartEntry() != null) {
			updateEndSegment(currentSegment, ctx.getSegmentEntry());
			LOG.debug("[changePoint] adding {0} [{1}]", currentSegment,
					currentSegment.getValues());
			ctx.getExtremeSegments().add(currentSegment);
			currentSegment = createExtremeSegment(ctx.getSegmentEntry());
			ctx.setCurrentSegment(currentSegment);
			LOG.debug("[changePoint] updated currentSegment: {0}",
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
		segment.setEndEntry(entry);
		segment.setLength(getExtremeSegmentService().getCalculatedLength(
				segment));

		String newLabel = MessageFormat.format("{0}:{1}", segment
				.getStartEntry().getIndex(), segment.getEndEntry().getIndex());
		if (segment.getLabel() != null) {
			newLabel = segment.getLabel() + "+" + newLabel + ";";
		}
		segment.setLabel(newLabel);

		LOG.debug("[endSegment] lastSegment end: {0}  [{1}:{2}]", entry,
				segment.getStart(), segment.getEnd());

	}

	/**
	 * 
	 * @param segment
	 * @param entry
	 */
	private void updatePeakSegment(ExtremeSegment segment, ExtremeEntry entry) {
		Assert.isTrue(segment.getStartEntry() != null,
				"start should not be null");
		Assert.isTrue(segment.getStartEntry().getIndex() != entry.getIndex(),
				"start should not same as peak");
		if (segment.getPeakEntry() == null) {
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
		ExtremeSegment currentSegment = onlineCtx.getCurrentSegment();

		ExtremeSegment lastSegment = onlineCtx.getExtremeSegments()
				.removeLast();
		Assert.isTrue(lastSegment.getEndEntry() != null, "end not set");
		if (currentSegment != null) {
			LOG.debug("[join]++++ on {2} [{3}]; last: {0}; current: {1}; ",
					lastSegment, currentSegment, onlineCtx.getIndex(),
					onlineCtx.getMarkerState());

			lastSegment.getValues().addAll(currentSegment.getValues());
			for (ExtremeEntry entry : currentSegment.getPeakEntries()) {
				updatePeakSegment(lastSegment, entry);
			}

			lastSegment.setEndEntry(null);
			ctx.setCurrentSegment(lastSegment);

			currentSegment = onlineCtx.getCurrentSegment();
			lastSegment = onlineCtx.getExtremeSegments().size() > 0 ? onlineCtx
					.getExtremeSegments().getLast() : null;
			LOG.debug("[join]--- on {2} [{3}]; last: {0}; current: {1} ",
					lastSegment, currentSegment, onlineCtx.getIndex(),
					onlineCtx.getMarkerState());
		}
	}

	/**
	 * 
	 * @param ctx
	 * @param marker
	 * @return
	 */
	public void appendMarker(ExtremeSegment appendESegment,
			ExtremeSegmentsOnlineCtx ctx) {
		Assert.isTrue(appendESegment.getEndEntry() != null, "End should be set");
		Integer start = appendESegment.getStartEntry().getIndex();
		Integer end = appendESegment.getEndEntry().getIndex();
		Long lentgh = appendESegment.getValues().indextoMils(end - start);
		Assert.isTrue(
				getExtremeSegmentService().getCalculatedLength(appendESegment)
						.equals(lentgh),
				"some values are lost: "
						+ getExtremeSegmentService().getCalculatedLength(
								appendESegment) + "!=" + lentgh);
		LOG.debug(MessageFormat.format("[appendMarker]append segment  [{0}] ",
				appendESegment.getValues()));

		if (validateMarker(appendESegment)) {
			LOG.debug("[appendMarker] appendMarker: {0} [{1}:{2}]",
					appendESegment, appendESegment.getStart(),
					appendESegment.getLength());
			getMarkSet().getMarkers().add(appendESegment);
			updateListeners(appendESegment);
			LOG.debug("[appendMarker]markers: {0}", getMarkSet().getMarkers());
		} else {
			throw new IllegalArgumentException("Segments Conflicts");
		}
	}

	private void updateListeners(ExtremeSegment appendESegment) {
		Long time = appendESegment.getStart();
		Long sample = appendESegment.getStartEntry().getIndex().longValue();
		Long stepInTime = appendESegment.getValues().toTime(1);

		Iterator<Double> iValue = appendESegment.getValues().iterator();

		Double firstValue = iValue.next();
		for (IClassificationListener listener : getClassificationListeners()) {
			listener.onSegmentStarted(new SegmentEvent(getName(), time,
					appendESegment, sample, firstValue, true));
		}
		time += stepInTime;
		sample++;
		Double previousValue = iValue.next();
		for (; iValue.hasNext();) {
			SegmentEvent event = new SegmentEvent(getName(), time,
					appendESegment, sample, previousValue, true);
			event.setOutputValues(appendESegment.getValues());
			event.setWindowValues(getOnlineCtx().popWindowValues());
			for (IClassificationListener listener : getClassificationListeners()) {
				listener.onSegmentProcessed(event);
			}
			time += stepInTime;
			sample++;
			previousValue = iValue.next();
		}

		for (IClassificationListener listener : getClassificationListeners()) {
			listener.onSegmentEnded(new SegmentEvent(getName(), time,
					appendESegment, sample, previousValue, false));
		}
		time += stepInTime;
		sample++;
	}

	/**
	 * 
	 * @param valitateMarker
	 * @return
	 */
	public boolean validateMarker(Marker valitateMarker) {
		// log.error(MessageFormat.format(
		// "[validateMarker]valitateMarker   [{0}:{1}]",
		// valitateMarker.getStart(), valitateMarker.getEnd()));
		for (Marker iMarker : getMarkSet().getMarkers()) {
			// log.error(MessageFormat.format(
			// "[validateMarker]iMarker   [{0}:{1}]", iMarker.getStart(),
			// iMarker.getEnd()));
			if (iMarker.getStart().equals(valitateMarker.getStart())) {
				LOG.error("[validateMarker]conflicts " + iMarker + " with "
						+ valitateMarker);
				return false;
			} else if (iMarker.getEnd() > valitateMarker.getStart()) {
				LOG.error(MessageFormat.format(
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
	}

	public void deleteSegment(ExtremeSegmentsOnlineCtx ctx) {
		ExtremeSegment lastSegment = onlineCtx.getExtremeSegments().size() == 0 ? null
				: onlineCtx.getExtremeSegments().getLast();
		if (lastSegment == null) {
			return;
		}
		lastSegment.setLabel("DELETED");
		LOG.debug("[deleteSegment] not adding {0}", lastSegment);
		changePointLastApproved(ctx);
	}

	protected ExtremeSegment createExtremeSegment(ExtremeEntry extremeEntry) {
		ExtremeSegment newExtremeSegment = new ExtremeSegment();
		newExtremeSegment.setStartEntry(extremeEntry);
		newExtremeSegment.setValues(new FrameValues());
		newExtremeSegment.getValues().setSampleRate(
				getOutputValues().getSampleRate());
		long start = newExtremeSegment.getValues().indextoMils(
				extremeEntry.getIndex());
		newExtremeSegment.setStart(start);
		return newExtremeSegment;
	}

	public Deque<ExtremeSegment> getExtremeSegments() {
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

	public ExtremeSegmentServiceImpl getExtremeSegmentService() {
		if (extremeSegmentService == null) {
			extremeSegmentService = new ExtremeSegmentServiceImpl();
		}
		return extremeSegmentService;
	}

	public void setExtremeSegmentService(
			ExtremeSegmentServiceImpl extremeSegmentService) {
		this.extremeSegmentService = extremeSegmentService;
	}

}

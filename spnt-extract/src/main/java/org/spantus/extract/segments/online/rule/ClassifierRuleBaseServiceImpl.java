package org.spantus.extract.segments.online.rule;

import org.spantus.core.threshold.ExtremeSegment;
import org.spantus.extract.segments.online.ExtremeSegmentsOnlineCtx;
import org.spantus.extract.segments.online.rule.ClassifierRuleBaseEnum.state;
import org.spantus.logger.Logger;

/**
 * 
 * @author mondhs
 * 
 */
public class ClassifierRuleBaseServiceImpl implements ClassifierRuleBaseService {

	Logger log = Logger.getLogger(getClass());

	public String testOnRuleBase(ExtremeSegmentsOnlineCtx ctx) {
		ClassifierRuleBaseEnum.action actionVal = testOnRuleBase(ctx,
				Boolean.TRUE);
		return actionVal.name();
	}

	public ClassifierRuleBaseEnum.action testOnRuleBase(
			ExtremeSegmentsOnlineCtx ctx, Boolean isEnum) {

		ExtremeSegment lastSegment = null;
		boolean segmentEnd = ctx.getFoundEndSegment();
		boolean segmentStart = ctx.getFoundStartSegment();
		boolean segmentPeak = ctx.getFoundPeakSegment();

		if (ctx.getExtremeSegments().size() > 0) {
			lastSegment = ctx.getExtremeSegments().getLast();
			// return ClassifierRuleBaseEnum.action.processNoise.name();
		}
		ExtremeSegment currentSegment = ctx.getCurrentSegment();

		if (currentSegment == null) {
			return ClassifierRuleBaseEnum.action.processNoise;
		}
		if (lastSegment == null) {
			if (segmentStart) {
				return ClassifierRuleBaseEnum.action.startMarker;
			}else if (ctx.isIn(state.start)) {
				return ClassifierRuleBaseEnum.action.startMarkerApproved;
			} else if (segmentEnd && ctx.isIn(state.segment)) {
				return ClassifierRuleBaseEnum.action.endMarkerApproved;
			} else if (ctx.isIn(state.segment)) {
				return ClassifierRuleBaseEnum.action.processSignal;
			}

		}
		if (lastSegment != null) {
			String className = ctx.getClassName(lastSegment);
			if (segmentStart && ctx.isIn(null)) {
				return ClassifierRuleBaseEnum.action.startMarker;
			} else if ((ctx.isIn(state.start) || ctx.isIn(null)) && segmentPeak) {
				return ClassifierRuleBaseEnum.action.startMarkerApproved;
			} else if (segmentEnd && currentSegment.isIncrease()
					&& lastSegment.isIncrease()) {
				return ClassifierRuleBaseEnum.action.join;
			} else if (segmentEnd && currentSegment.isDecrease()
					&& lastSegment.isDecrease()) {
				return ClassifierRuleBaseEnum.action.join;
			} else if (!segmentEnd && !segmentStart && ctx.isIn(state.segment)) {
				return ClassifierRuleBaseEnum.action.processSignal;
			} else if (segmentEnd
					&& !"0".equals(className)
					 ) {//&& lastSegment.getCalculatedLength() > 40
				return ClassifierRuleBaseEnum.action.endMarkerApproved;
			}else if(segmentEnd && ctx.isIn(state.segment)){
				return ClassifierRuleBaseEnum.action.delete;
			}else if(ctx.isIn(state.segment)){
				return ClassifierRuleBaseEnum.action.processSignal;
			}
			log.debug("[testOnRuleBase] NC not handled area and length {0}, {1}",
						currentSegment.getCalculatedArea(), currentSegment
								.getCalculatedLength());
			return ClassifierRuleBaseEnum.action.processNoise;
		}

		// log.debug("[testOnRuleBase] area and length {0}, {1}",
		// segment.getCalculatedArea(), segment.getCalculatedLength());

		log.debug("[testOnRuleBase] UC not handled area and length {0}, {1}",
				currentSegment.getCalculatedArea(), currentSegment
						.getCalculatedLength());

		return ClassifierRuleBaseEnum.action.processNoise;
	}
}

package org.spantus.extract.segments.online.rule;

import org.spantus.extract.segments.offline.ExtremeSegment;
import org.spantus.extract.segments.online.ExtremeOnClassifierServiceFactory;
import org.spantus.extract.segments.online.ExtremeSegmentsOnlineCtx;
import org.spantus.extract.segments.online.cluster.ExtremeOnlineClusterService;
import org.spantus.extract.segments.online.rule.ClassifierRuleBaseEnum.state;
import org.spantus.logger.Logger;

/**
 * 
 * @author Mindaugas Greibus
 * 
 * @since 0.2
 * Created Mar 16, 2010
 *
 */
public class ClassifierRuleBaseServiceImpl implements ClassifierRuleBaseService {

	Logger log = Logger.getLogger(getClass());

	private ExtremeOnlineClusterService clusterService;
	/**
	 * test on rule base
	 */
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
		boolean noiseClass = true;
		

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
			} else if (ctx.isIn(state.start)) {
				return ClassifierRuleBaseEnum.action.startMarkerApproved;
			} else if (segmentEnd && ctx.isIn(state.segment)) {
				return ClassifierRuleBaseEnum.action.endMarkerApproved;
			} else if (ctx.isIn(state.segment)) {
				return ClassifierRuleBaseEnum.action.processSignal;
			}

		}
		if (lastSegment != null) {
//			String className = getClusterService().getClassName(lastSegment,
//					ctx);
			Double lastArea = lastSegment.getCalculatedArea();
			Double currentArea = currentSegment.getCalculatedArea();
			Integer lastPeak = lastSegment.getPeakEntries().size();
			Integer currentPeak = currentSegment.getPeakEntries().size();
			Long lastLength = lastSegment.getCalculatedLength();
			Long currentLength = currentSegment.getCalculatedLength();
			
//			noiseClass = "0".equals(className);

			
			if (segmentStart && ctx.isIn(null)) {
				return ClassifierRuleBaseEnum.action.startMarker;
//			} else if (ctx.isIn(state.start) && segmentPeak && currentLength <=20) {
//				log.debug("too short for segment");
//				return ClassifierRuleBaseEnum.action.join;
			} else if ((ctx.isIn(state.start) || ctx.isIn(null)) && segmentPeak) {
				return ClassifierRuleBaseEnum.action.startMarkerApproved;
			} else if (segmentEnd && currentSegment.isIncrease(lastSegment)
//					&& !currentSegment.isSimilar(lastSegment)
//					&& currentArea>lastArea*.9 && currentArea<lastArea
//					&& !noiseClass
					) {
				log.debug("increase");
				return ClassifierRuleBaseEnum.action.join;
			} else if (segmentEnd && currentSegment.isDecrease(lastSegment)
//					&& !currentSegment.isSimilar(lastSegment)
//					&& lastArea>currentArea*.9 && lastArea<currentArea
//					&& !noiseClass
					) {
				log.debug("decrease");
				return ClassifierRuleBaseEnum.action.join;
			} else if (segmentEnd && currentSegment.isSimilar(lastSegment)
					) {
				log.debug("similar" + currentSegment.isSimilar(lastSegment));
				return ClassifierRuleBaseEnum.action.join;
//			} else if (segmentEnd && currentSegment.isDecrease()
//					&& lastSegment.isIncrease() 
//					&& lastArea>currentArea/2
//					) {
//				return ClassifierRuleBaseEnum.action.join;				
//			} else if (segmentEnd && currentSegment.isDecrease()
//					&& lastSegment.isIncrease()
//					&& lastLength >20 && currentLength > 20) {
//				return ClassifierRuleBaseEnum.action.join;
//			} else if (segmentEnd && currentPeak == lastPeak 
//					&& (lastArea>currentArea*.9 && lastArea<currentArea)
//			) {
//				return ClassifierRuleBaseEnum.action.join;	

//			} else if (segmentEnd && lastLength == currentLength
//					) {
//				return ClassifierRuleBaseEnum.action.join;	
//			} else if (segmentEnd && lastLength<50 && currentLength < 50
//					&& !noiseClass
//					) {
//				return ClassifierRuleBaseEnum.action.join;	
			} else if (!segmentEnd && !segmentStart && ctx.isIn(state.segment)) {
				return ClassifierRuleBaseEnum.action.processSignal;
			} else if (segmentEnd) {
				return ClassifierRuleBaseEnum.action.endMarker;
			} else if (segmentEnd && ctx.isIn(state.segment)) {
				return ClassifierRuleBaseEnum.action.delete;
			} else if (ctx.isIn(state.segment)) {
				return ClassifierRuleBaseEnum.action.processSignal;
			}else if (segmentStart) { 
				return ClassifierRuleBaseEnum.action.processNoise;
			}else {
				
			}
			log.debug(
					"[testOnRuleBase] NC not handled area and length {0}, {1}",
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

	public ExtremeOnlineClusterService getClusterService() {
		if (clusterService == null) {
			clusterService = ExtremeOnClassifierServiceFactory
					.createClusterService();

		}
		return clusterService;
	}

	public void setClusterService(ExtremeOnlineClusterService clusterService) {
		this.clusterService = clusterService;
	}
}

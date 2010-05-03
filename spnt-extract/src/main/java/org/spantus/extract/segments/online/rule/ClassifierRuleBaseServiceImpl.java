package org.spantus.extract.segments.online.rule;

import org.spantus.extract.segments.offline.ExtremeSegment;
import org.spantus.extract.segments.online.ExtremeOnClassifierServiceFactory;
import org.spantus.extract.segments.online.ExtremeSegmentsOnlineCtx;
import org.spantus.extract.segments.online.cluster.ExtremeOnlineClusterService;
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
		log.debug("[testOnRuleBase] {0}", actionVal.name());
		return actionVal.name();
	}

	public ClassifierRuleBaseEnum.action testOnRuleBase(
			ExtremeSegmentsOnlineCtx ctx, Boolean isEnum) {

		ExtremeSegment currentSegment = null;
		ExtremeSegment lastSegment = null;
//		boolean segmentEnd = ctx.getFoundEndSegment();
//		boolean segmentStart = ctx.getFoundStartSegment();
		boolean segmentPeak = ctx.getFoundPeakSegment();
		boolean noiseClass = true;

		Double currentArea = null;
		Integer currentPeak = null;
		Long currentLength = null;
		Double lastArea = null;
		Integer lastPeak = null;
		Long lastLength = null;
		boolean isIncrease = false;
		boolean isDecrease = false;
		
		if(ctx.getCurrentSegment() != null){
			currentSegment = ctx.getCurrentSegment();
			currentArea = currentSegment.getCalculatedArea();
			currentPeak = currentSegment.getPeakEntries().size();
			currentLength = currentSegment.getCalculatedLength();
		}
		
		if (ctx.getExtremeSegments().size() > 0) {
			lastSegment = ctx.getExtremeSegments().getLast();
			lastArea = lastSegment.getCalculatedArea();
			lastPeak = lastSegment.getPeakEntries().size();
			lastLength = lastSegment.getCalculatedLength();
			
			if(currentSegment.getPeakEntry() != null){
				isIncrease = currentSegment.isIncrease(lastSegment);
				isDecrease = currentSegment.isDecrease(lastSegment);
				
				log.debug("[testOnRuleBase] Similar: {0}, Increase: {1}; Decrease: {2};",
						currentSegment.isSimilar(lastSegment),
						isIncrease,
						isDecrease
						);
			}
		}
		
		if(currentSegment == null &&  ctx.isFeatureInMin()){
			log.debug("Current not initialized");
			return ClassifierRuleBaseEnum.action.changePointCurrentApproved;	
		} else if(lastSegment == null &&  ctx.isFeatureInMin()){
			log.debug("Previous not initialized");
			return ClassifierRuleBaseEnum.action.changePointCurrentApproved;	
		} else if(lastSegment == null &&  ctx.isFeatureInMax()){
			//do nothing
			log.debug("Previous not initialized");
			return ClassifierRuleBaseEnum.action.processSignal;	
		}else if(ctx.isFeatureInMin()){
			log.debug("Found min. Possible change point");
			return ClassifierRuleBaseEnum.action.changePoint;	
		}else 	if(ctx.isFeatureInMax() && isIncrease){
			log.debug("Found max. join as increase");
			return ClassifierRuleBaseEnum.action.join;
		}else if(ctx.isFeatureInMax() && currentLength<40 && lastLength > 100){
			return ClassifierRuleBaseEnum.action.join;
//		}else 	if(ctx.isFeatureInMax() && isDecrease){
//			log.debug("Found max. join as decrease");
//			return ClassifierRuleBaseEnum.action.join;
		}else if(ctx.isFeatureInMax()){
			log.debug("Found max. approve previous change point");
			return ClassifierRuleBaseEnum.action.changePointLastApproved;	
		}


//		if (currentSegment == null) {
//			return ClassifierRuleBaseEnum.action.processNoise;
//		}
//		if (lastSegment == null) {
//			if (segmentStart) {
//				return ClassifierRuleBaseEnum.action.changePoint;
//			} else if (ctx.isIn(state.start)) {
//				return ClassifierRuleBaseEnum.action.startMarkerApproved;
//			} else if (segmentEnd && ctx.isIn(state.segment)) {
//				return ClassifierRuleBaseEnum.action.endMarker;
//			} else if (ctx.isIn(state.segment)) {
//				return ClassifierRuleBaseEnum.action.processSignal;
//			}
//
//		}
//		if (lastSegment != null) {
//			String className = getClusterService().getClassName(lastSegment,
//					ctx);
			
			
//			noiseClass = "0".equals(className);
//			log.debug(
//					"[testOnRuleBase] processing Area: ({0}<>{1}), length: ({2}<>{3}), state {4}, isEnd: {5}, isPeak: {6}",
//						currentArea, lastArea,
//						currentLength, lastLength
//							, ctx.getMarkerState(),
//							segmentEnd, segmentPeak);
//			
//			if (currentSegment == null) {
//				return ClassifierRuleBaseEnum.action.processNoise;
//			}else if (ctx.isIn(null) && !segmentPeak) {
//				log.debug("[testOnRuleBase] state not defined");
//				return ClassifierRuleBaseEnum.action.startMarkerApproved;
//			} else if ( segmentPeak) {
//				return ClassifierRuleBaseEnum.action.startMarkerApproved;
//			} else if (segmentEnd && currentSegment.isIncrease(lastSegment)
////					&& currentArea>lastArea*.9 && currentArea<lastArea
////					&& !noiseClass
//					) {
//				log.debug("[testOnRuleBase] increase");
//				return ClassifierRuleBaseEnum.action.join;
//			} else if (segmentEnd && currentSegment.isDecrease(lastSegment)
////					&& lastArea>currentArea*.9 && lastArea<currentArea
////					&& !noiseClass
//					) {
//				log.debug("[testOnRuleBase] decrease");
//				return ClassifierRuleBaseEnum.action.join;
//			} else if (segmentEnd && currentSegment.isSimilar(lastSegment)
//					) {
//				log.debug("[testOnRuleBase] similar");
//				return ClassifierRuleBaseEnum.action.join;
//			} else if (!segmentEnd && !segmentStart && ctx.isIn(state.segment)) {
// 				return ClassifierRuleBaseEnum.action.processSignal;
////			} else if (segmentEnd && lastLength < 30 && currentLength > 30 ) {
////				log.debug("[testOnRuleBase] too short for start new currentLength: {0}, lastLength:{1}", currentLength, lastLength  );
////				return ClassifierRuleBaseEnum.action.join;
//			} else if (segmentEnd) {
//				return ClassifierRuleBaseEnum.action.endMarkerApproved;
//			} else if (segmentEnd && ctx.isIn(state.segment)) {
//				return ClassifierRuleBaseEnum.action.delete;
//			} else if (ctx.isIn(state.segment)) {
//				return ClassifierRuleBaseEnum.action.processSignal;
////			} else if (ctx.isIn(state.end) && currentLength<30) {
////				return ClassifierRuleBaseEnum.action.processSignal;
//			}else if (segmentStart) { 
//				return ClassifierRuleBaseEnum.action.processNoise;
//			}else {
//				
//			}
//			log.debug(
//					"[testOnRuleBase] NC not handled area: {0}, length: {1}, state {2}, isEnd: {3}",
//					currentSegment.getCalculatedArea(), currentSegment
//							.getCalculatedLength(), ctx.getMarkerState(),
//							segmentEnd);
//			return ClassifierRuleBaseEnum.action.processNoise;
//		}

		// log.debug("[testOnRuleBase] area and length {0}, {1}",
		// segment.getCalculatedArea(), segment.getCalculatedLength());

		log.debug("[testOnRuleBase]  not handled area and length {0}, {1}",
				currentArea, currentLength);

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

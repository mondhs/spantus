package org.spantus.extractor.segments.online.rule;

import java.util.HashMap;
import java.util.Map;

import org.spantus.extractor.segments.ExtremeSegmentServiceImpl;
import org.spantus.extractor.segments.offline.ExtremeSegment;
import org.spantus.extractor.segments.online.ExtremeOnClassifierServiceFactory;
import org.spantus.extractor.segments.online.ExtremeSegmentsOnlineCtx;
import org.spantus.extractor.segments.online.cluster.ExtremeOnlineClusterService;
import org.spantus.logger.Logger;

/**
 * Basic rule implementation for short signals.
 * 
 * @author Mindaugas Greibus
 * 
 * @since 0.2 Created Mar 16, 2010
 * 
 */
public abstract class AbstractClassifierRuleBaseServiceImpl implements ClassifierRuleBaseService {
	public static final String RULE_DATA_CTX = "ruleDataCtx";
	public static final String STABLE_LENGTH = "stableLength";
	public static final String CURRENT_ANGLE = "currentAngle";
	public static final String CURRENT_PEAK_VALUE = "currentPeakValue";
	public static final String CURRENT_PEAK_COUNT = "currentPeakCount";
	public static final String LAST_ANGLE = "lastAngle";
	public static final String LAST_PEAK_VALUE = "lastPeakValue";
	public static final String LAST_PEAK_COUNT = "lastPeakCount";
	public static final String CLASS_NAME = "className";
	public static final String IS_DECREASE = "isDecrease";
	public static final String IS_INCREASE = "isIncrease";
	public static final String CURRENT_LENGTH = "currentLength";
	public static final String LAST_LENGTH = "lastLength";
	public static final String DISTANCE_BETWEEN_PAEKS = "distanceBetweenPaeks";
	public static final String LAST_SEGMENT = "lastSegment";
	public static final String CURRENT_SEGMENT = "currentSegment";
	public static final String CTX = "ctx";

	private final static Logger log = Logger
			.getLogger(AbstractClassifierRuleBaseServiceImpl.class);
	
	private ExtremeOnlineClusterService clusterService;
	private ExtremeSegmentServiceImpl extremeSegmentService;


	/**
	 * 
	 * @param ctx
	 * @return
	 */
	protected Map<String, Object> prepareCtx(ExtremeSegmentsOnlineCtx ctx) {
		Map<String, Object> params = new HashMap<String, Object>();

		ExtremeSegment currentSegment = null;
		ExtremeSegment lastSegment = null;
		Integer currentPeakCount = null;
		Double currentPeakValue = null;
		Long currentLength = null;
		Double currentAngle = null;
		Long stableLength = null;
		Double lastPeakValue = null;
		Integer lastPeakCount = null;
		Double lastAngle = null;
		Long lastLength = null;
		boolean isIncrease = false;
		boolean isDecrease = false;
		Long distanceBetweenPaeks = Long.MAX_VALUE;
		String className = "";

		if (ctx.getCurrentSegment() != null) {
			currentSegment = ctx.getCurrentSegment();
//			currentArea = getExtremeSegmentService().getCalculatedArea(
//					currentSegment);
			currentPeakCount = currentSegment.getPeakEntries().size();
			currentLength = getExtremeSegmentService().getCalculatedLength(
					currentSegment);
//			currentSizeValues = currentSegment.getValues().size();
			stableLength = currentSegment.getValues().indextoMils(
					ctx.getStableCount());
		}

		if (ctx.getExtremeSegments().size() > 0) {
			lastSegment = ctx.getExtremeSegments().getLast();
//			lastArea = getExtremeSegmentService()
//					.getCalculatedArea(lastSegment);
			lastPeakValue = lastSegment.getPeakEntry().getValue();
			lastPeakCount = lastSegment.getPeakEntries().size();
			lastLength = getExtremeSegmentService().getCalculatedLength(
					lastSegment);
//			lastSizeValues = lastSegment.getValues().size();

			currentAngle = getExtremeSegmentService().angle(currentSegment);
			lastAngle = getExtremeSegmentService().angle(lastSegment);

			if (currentSegment.getPeakEntry() != null) {
				currentPeakValue = currentSegment.getPeakEntry().getValue();
				isIncrease = getExtremeSegmentService().isIncrease(
						currentSegment, lastSegment);
				isDecrease = getExtremeSegmentService().isDecrease(
						currentSegment, lastSegment);

//				isSimilar = getExtremeSegmentService().isSimilar(
//						currentSegment, lastSegment);
				className = getClusterService().getClassName(lastSegment, ctx);
				if (currentPeakCount >= 1) {
					Integer first = lastSegment.getPeakEntry().getIndex();
					Integer last = currentSegment.getPeakEntry().getIndex();
					distanceBetweenPaeks = last.longValue() - first;
					distanceBetweenPaeks = currentSegment.getValues()
							.indextoMils(distanceBetweenPaeks.intValue());

				}

				// log.debug(
				// "[testOnRuleBase] Similar: {0}, Increase: {1}; Decrease: {2}; className:{3}",
				// isSimilar, isIncrease, isDecrease, className);
			}
		}
		//for mvel
		params.put(CTX, ctx);
		params.put(CURRENT_SEGMENT, currentSegment);
		params.put(LAST_SEGMENT, lastSegment);
		params.put(LAST_LENGTH, lastLength);
		params.put(LAST_PEAK_COUNT, lastPeakCount);
		params.put(LAST_PEAK_VALUE, lastPeakValue);
		params.put(LAST_ANGLE, lastAngle);
		params.put(DISTANCE_BETWEEN_PAEKS, distanceBetweenPaeks);
		params.put(CURRENT_LENGTH, currentLength);
		params.put(CURRENT_PEAK_COUNT, currentPeakCount);
		params.put(CURRENT_PEAK_VALUE, currentPeakValue);
		params.put(CURRENT_ANGLE, currentAngle);
		params.put(IS_INCREASE, isIncrease);
		params.put(IS_DECREASE, isDecrease);
		params.put(CLASS_NAME, className);
		params.put(STABLE_LENGTH, stableLength);
		
		//for java
		RuleDataCtx ruleDataCtx = new RuleDataCtx();
		ruleDataCtx.setCurrentSegment(currentSegment);
		ruleDataCtx.setLastSegment(lastSegment);
		ruleDataCtx.setLastPeakCount(lastPeakCount);
		ruleDataCtx.setLastLength(lastLength);
		ruleDataCtx.setLastPeakCount(lastPeakCount);
		ruleDataCtx.setLastPeakValue(lastPeakValue);
		ruleDataCtx.setLastAngle(lastAngle);
		ruleDataCtx.setDistanceBetweenPaeks(distanceBetweenPaeks);
		ruleDataCtx.setCurrentLength(currentLength);
		ruleDataCtx.setCurrentPeakCount(currentPeakCount);
		ruleDataCtx.setCurrentPeakValue(currentPeakValue);
		ruleDataCtx.setCurrentAngle(currentAngle);
		ruleDataCtx.setIncrease(isIncrease);
		ruleDataCtx.setDecrease(isDecrease);
		ruleDataCtx.setStableLength(stableLength);
		
		params.put(RULE_DATA_CTX, ruleDataCtx);
		
		

		return params;
	}



	

//	protected ClassifierRuleBaseEnum.action decision(ExtremeSegmentsOnlineCtx ctx, RuleDataCtx c) {
//		if (c.currentSegment == null && ctx.getFeatureInMin()) {
//			log.debug("Current not initialized. This first segment");
//			return ClassifierRuleBaseEnum.action.initSegment;
//		} else if (c.lastSegment == null && ctx.getFeatureInMin()) {
//			log.debug("Previous not initialized. this is second segment");
//			return ClassifierRuleBaseEnum.action.initSegment;
//		} else if (c.lastSegment == null && ctx.getFeatureInMax()) {
//			// do nothing
//			log.debug("Previous not initialized");
//			return ClassifierRuleBaseEnum.action.processSignal;
//		} else if (ctx.getFeatureInMin()) {
//			log.debug("Found min. Possible change point");
//			return ClassifierRuleBaseEnum.action.changePoint;
//		} else if (ctx.getFeatureInMax() && c.distanceBetweenPaeks < 180
//				&& (c.lastLength + c.currentLength) < 280) {
//			log.debug("Found max. join as between peaks not enough space");
//			return ClassifierRuleBaseEnum.action.join;
//		} else if (ctx.getFeatureInMax() && c.isIncrease) {
//			log.debug("Found max. join as increase");
//			return ClassifierRuleBaseEnum.action.join;
//		} else if (ctx.getFeatureInMax() && c.isDecrease
//				&& (c.lastLength + c.currentLength) < 180) {
//			log.debug("Found max. join as decrease {0}", c.isDecrease);
//			return ClassifierRuleBaseEnum.action.join;
// 		} else if (ctx.getFeatureInMax() 
//				&&  c.distanceBetweenPaeks <40) {
//			log.debug("Found max. join as between peaks not enough space {0}", c.distanceBetweenPaeks);
//			return ClassifierRuleBaseEnum.action.join;                       
//			// } else if (ctx.getFeatureInMax() && (lastLength+currentLength) <
//			// 280 ) {
//			// log.debug("too small gap for new segment lastLength:{0}<40, current:{1}>100",
//			// lastLength, currentLength);
//			// return ClassifierRuleBaseEnum.action.join;
//			// } else if (ctx.getFeatureInMax() && currentLength < 40 ) {
//			// log.debug("too small gap for new segment lastLength:{0}<40, current:{1}>100",
//			// lastLength, currentLength);
//			// return ClassifierRuleBaseEnum.action.join;
//			// } else if (ctx.getFeatureInMax() && isSimilar) {
//			// log.debug("Found max. join as similar");
//			// return ClassifierRuleBaseEnum.action.join;
//		} else if (ctx.getFeatureInMax() && c.lastLength < 20) {
//			log.debug("too small last", c.lastLength);
//			return ClassifierRuleBaseEnum.action.join;
//		} else if (ctx.getFeatureInMax() && "0".equals(c.className)) {
//			log.debug("Found max. delete segment as noise");
//			return ClassifierRuleBaseEnum.action.delete;
//		} else if (ctx.getFeatureInMax()) {
//			log.debug("Found max. approve previous change point");
//			return ClassifierRuleBaseEnum.action.changePointLastApproved;
//		}
//		return null;
//	}

	/**
	 * learn delegate to cluster service
	 */
	public void learn(ExtremeSegment currentSegment,
			ExtremeSegmentsOnlineCtx ctx) {
		getClusterService().learn(currentSegment, ctx);
	}

	/**
	 * 
	 * @return
	 */
	public ExtremeOnlineClusterService getClusterService() {
		if (clusterService == null) {
			clusterService = ExtremeOnClassifierServiceFactory
					.createClusterService();

		}
		return clusterService;
	}

	/**
	 * 
	 * @param clusterService
	 */
	public void setClusterService(ExtremeOnlineClusterService clusterService) {
		this.clusterService = clusterService;
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

package org.spantus.extractor.segments.online.rule;

import java.util.Map;

import org.spantus.extractor.segments.online.ExtremeSegmentsOnlineCtx;
import org.spantus.logger.Logger;
/**
 * Rules set should applicable for long real life cases.
 * @author as
 *
 */
public class ClassifierRuleBaseServiceBaseImpl extends
		AbstractClassifierRuleBaseServiceImpl {


	private final static Logger log = Logger
			.getLogger(ClassifierRuleBaseServiceBaseImpl.class);
	

	/**
	 * test on rule base
	 * 
	 * @param ctx
	 */
	public String testOnRuleBase(ExtremeSegmentsOnlineCtx ctx) {
		ClassifierRuleBaseEnum.action actionVal = testOnRuleBase(ctx,
				Boolean.TRUE);
		log.debug("[testOnRuleBase] {0}", actionVal.name());
		return actionVal.name();
	}
	
	/**
	 * 
	 * @param ctx
	 * @param isEnum
	 * @return
	 */
	protected ClassifierRuleBaseEnum.action testOnRuleBase(
			ExtremeSegmentsOnlineCtx ctx, Boolean isEnum) {

		Map<String, Object> param = prepareCtx(ctx);
		RuleDataCtx c = (RuleDataCtx) param.get(RULE_DATA_CTX);

		ClassifierRuleBaseEnum.action action = decision(ctx, c);
		if(action != null){
			return action;
		}
		
		log.debug("[testOnRuleBase]  not handled area and length {0}}",
				c.currentLength);

		return ClassifierRuleBaseEnum.action.processSignal;
	}
	/**
	 * "#id";"rule";"action";"description"
	 * @param ctx
	 * @param c
	 * @return
	 */
	protected ClassifierRuleBaseEnum.action decision(
			ExtremeSegmentsOnlineCtx ctx, RuleDataCtx c) {
//		1;"currentSegment == null && ctx.featureInMax";"processNoise";"Current not initialized. Found max, but there is no min. skip it"		
		if (c.currentSegment == null && ctx.getFeatureInMax()) {
			return ClassifierRuleBaseEnum.action.processNoise;
//		2;"currentSegment == null";"initSegment";"Current not initialized. This first segment"			
		} else if (c.currentSegment == null) {
			return ClassifierRuleBaseEnum.action.initSegment;
//		3;"ctx.featureStable && currentSegment == null";"processNoise";"initial noise"			
		} else if (ctx.getFeatureStable() && c.currentSegment == null) {
			return ClassifierRuleBaseEnum.action.processNoise;
//		4;"ctx.featureStable && stableLength <20";"processSignal";"waiting for decision if this part of signal"			
		} else if (ctx.getFeatureStable() && c.stableLength < 20) {
			return ClassifierRuleBaseEnum.action.processSignal;
//		5;"ctx.featureStable && stableLength >20 && currentPeakCount > 0";"changePoint";"this is part of noise"			
		} else if (ctx.getFeatureStable() && c.stableLength > 20 && c.currentPeakCount > 0) {
			return ClassifierRuleBaseEnum.action.changePoint;
//		6;"ctx.featureStable && stableLength >20 ";"processNoise";"this is part of noise"			
		} else if (ctx.getFeatureStable() && c.stableLength > 20) {
			return ClassifierRuleBaseEnum.action.processNoise;
//		7a;"ctx.featureInMin && ctx.previousValue > 1E6";"processSignal";"Higher mins are M pattern"
		} else if (ctx.getFeatureInMin() && "smooth_SPECTRAL_FLUX_EXTRACTOR".equals(ctx.getExtractorName()) 
				&& ctx.getPreviousValue() > 1E6) {
			return ClassifierRuleBaseEnum.action.processSignal;
		} else if (ctx.getFeatureInMin() && "smooth_ENERGY_EXTRACTOR".equals(ctx.getExtractorName()) 
				&& ctx.getPreviousValue() > 1E6) {
			return ClassifierRuleBaseEnum.action.processSignal;
		} else if (ctx.getFeatureInMin() && "smooth_SIGNAL_ENTROPY_EXTRACTOR".equals(ctx.getExtractorName()) 
				&& ctx.getPreviousValue() > 5E4) {
			return ClassifierRuleBaseEnum.action.processSignal;			

//		} else if (ctx.getFeatureInMin() 
//				&& ctx.getPreviousValue() > 1E6) {
//			return ClassifierRuleBaseEnum.action.processSignal;			
//		7;"ctx.featureInMin";"changePoint";"change point"			
		} else if (ctx.getFeatureInMin()) {
			return ClassifierRuleBaseEnum.action.changePoint;
//		8;"ctx.featureInMax && lastLength < 100 ";"join";"too small last"			
		} else if (c.lastLength != null && ctx.getFeatureInMax() && c.lastLength < 100) {
			return ClassifierRuleBaseEnum.action.join;
//		10;"ctx.featureInMax && isIncrease && distanceBetweenPaeks<190";"join";"Found max. join as increase"			
		} else if (ctx.getFeatureInMax() && c.isIncrease && c.distanceBetweenPaeks < 190) {
			return ClassifierRuleBaseEnum.action.join;
//		11;"ctx.featureInMax && isDecrease && distanceBetweenPaeks<190";"join";"Found max. join as decrease"			
		} else if (ctx.getFeatureInMax() && c.isDecrease && c.distanceBetweenPaeks < 190) {
			return ClassifierRuleBaseEnum.action.join;
//		11a;"ctx.featureInMax && distanceBetweenPaeks<40";"join";"Found max. join as between peaks not enough space"			
        } else if (ctx.getFeatureInMax() && c.getDistanceBetweenPaeks()<40L){
			return ClassifierRuleBaseEnum.action.join;
//		13;"lastSegment != null && ctx.featureInMax && lastSegment.start < 100";"delete";"remove initial"			
		} else if (c.lastSegment != null && ctx.getFeatureInMax() && c.lastSegment.getStart() < 100) {
			return ClassifierRuleBaseEnum.action.delete;
//		14;"ctx.featureInMax";"changePointLastApproved";"Found max. approve previous change point"			
		} else if (ctx.getFeatureInMax()) {
			return ClassifierRuleBaseEnum.action.changePointLastApproved;
		}
//		15;"true";"processSignal";"Rule not match"		
		return ClassifierRuleBaseEnum.action.processSignal;
	}
}

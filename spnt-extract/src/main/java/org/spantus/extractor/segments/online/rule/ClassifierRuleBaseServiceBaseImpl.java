package org.spantus.extractor.segments.online.rule;

import org.spantus.extractor.segments.online.ExtremeSegmentsOnlineCtx;
/**
 * Rules set should applicable for long real life cases.
 * @author as
 *
 */
public class ClassifierRuleBaseServiceBaseImpl extends
		AbstractClassifierRuleBaseServiceImpl {

	@Override
	protected ClassifierRuleBaseEnum.action decision(
			ExtremeSegmentsOnlineCtx ctx, RuleDataCtx c) {
		if (c.currentSegment == null && ctx.getFeatureInMax()) {
			return ClassifierRuleBaseEnum.action.processNoise;
		} else if (c.lastSegment != null && ctx.getFeatureInFlush()) {
			return ClassifierRuleBaseEnum.action.changePointLastApproved;
		} else if (c.currentSegment == null) {
			return ClassifierRuleBaseEnum.action.initSegment;
		} else if (ctx.getFeatureStable() && c.currentSegment == null) {
			return ClassifierRuleBaseEnum.action.processNoise;
		} else if (ctx.getFeatureStable() && c.stableLength < 20) {
			return ClassifierRuleBaseEnum.action.processSignal;
		} else if (ctx.getFeatureStable() && c.stableLength > 20
				&& c.currentPeakCount > 0) {
			return ClassifierRuleBaseEnum.action.changePoint;
		} else if (ctx.getFeatureStable() && c.stableLength > 20) {
			return ClassifierRuleBaseEnum.action.processNoise;
		} else if (ctx.getFeatureInMin()) {
			return ClassifierRuleBaseEnum.action.changePoint;
		} else if (c.lastLength != null && ctx.getFeatureInMax()
				&& c.lastLength < 100) {
			return ClassifierRuleBaseEnum.action.join;
		} else if (ctx.getFeatureInMax() && c.isIncrease
				&& c.distanceBetweenPaeks < 190) {
			return ClassifierRuleBaseEnum.action.join;
		} else if (ctx.getFeatureInMax() && c.isDecrease
				&& c.distanceBetweenPaeks < 190) {
			return ClassifierRuleBaseEnum.action.join;
		} else if (c.lastSegment != null && ctx.getFeatureInMax()
				&& c.lastSegment.getStart() < 100) {
			return ClassifierRuleBaseEnum.action.delete;
		} else if (ctx.getFeatureInMax()) {
			return ClassifierRuleBaseEnum.action.changePointLastApproved;
		}
		return ClassifierRuleBaseEnum.action.processSignal;
	}
}

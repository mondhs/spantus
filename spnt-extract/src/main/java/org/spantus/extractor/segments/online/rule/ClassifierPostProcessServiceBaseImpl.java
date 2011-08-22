package org.spantus.extractor.segments.online.rule;

import org.spantus.extractor.segments.offline.ExtremeSegment;
import org.spantus.extractor.segments.online.ExtremeSegmentsOnlineCtx;
import org.spantus.logger.Logger;

/**
 * 
 * @author mondhs
 * 
 */
public class ClassifierPostProcessServiceBaseImpl implements
		ClassifierRuleBaseService {

	Logger log = Logger.getLogger(getClass());

	public String testOnRuleBase(ExtremeSegmentsOnlineCtx ctx) {
		if(ctx.getCurrentSegment() == null && ctx.getFeatureStable()){
			return ClassifierRuleBaseEnum.action.processNoise.name();
		}else if(ctx.getCurrentSegment() == null){
			return ClassifierRuleBaseEnum.action.initSegment.name();
		}else if(ctx.getFeatureInMin()){
			return ClassifierRuleBaseEnum.action.changePoint.name();	
		}else if(ctx.getFeatureInMax()){
			return ClassifierRuleBaseEnum.action.changePointLastApproved.name();	
		}
		return ClassifierRuleBaseEnum.action.processSignal.name();
	}

	public void learn(ExtremeSegment currentSegment,
			ExtremeSegmentsOnlineCtx ctx) {
		log.debug("No learning needed");
	}

}

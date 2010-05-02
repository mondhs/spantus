package org.spantus.extract.segments.online.rule;

import org.spantus.extract.segments.offline.ExtremeEntry.FeatureStates;
import org.spantus.extract.segments.online.ExtremeSegmentsOnlineCtx;
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

		if(ctx.isFeatureInMin()){
			return ClassifierRuleBaseEnum.action.changePointCurrentApproved.name();	
		}
		return ClassifierRuleBaseEnum.action.processSignal.name();
	}

}

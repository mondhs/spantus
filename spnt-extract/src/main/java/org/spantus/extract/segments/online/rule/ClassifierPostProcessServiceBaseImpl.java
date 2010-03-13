package org.spantus.extract.segments.online.rule;

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
		if(ctx.getExtremeSegments().size()==0){
			return ClassifierRuleBaseEnum.action.processNoise.name();
		}
		if(ctx.getFoundEndSegment()){
			//		ExtremeSegment segment = ctx.getExtremeSegments().getLast();
			return ClassifierRuleBaseEnum.action.endMarkerApproved.name();
		}
		return ClassifierRuleBaseEnum.action.processNoise.name();
	}

}

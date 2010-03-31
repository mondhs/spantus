package org.spantus.extract.segments.online;

import org.spantus.extract.segments.online.cluster.ExtremeOnlineClusterService;
import org.spantus.extract.segments.online.cluster.ExtremeOnlineClusterServiceImpl;
import org.spantus.extract.segments.online.rule.ClassifierRuleBaseService;
import org.spantus.extract.segments.online.rule.ClassifierRuleBaseServiceImpl;

public abstract class ExtremeOnClassifierServiceFactory {
	
	public static ExtremeOnlineClusterService createClusterService(){
		ExtremeOnlineClusterServiceImpl clusgerImpl = new ExtremeOnlineClusterServiceImpl();
//		ExtremeOnlineClusterServiceStaticImpl clusgerImpl = new ExtremeOnlineClusterServiceStaticImpl();
		return clusgerImpl;
	}
	
	public static ClassifierRuleBaseService createClassifierRuleBaseService() {
		ClassifierRuleBaseServiceImpl ruleBase = new ClassifierRuleBaseServiceImpl();
		ruleBase.setClusterService(createClusterService());
		
//		ClassifierPostProcessServiceBaseImpl ruleBase = new ClassifierPostProcessServiceBaseImpl();
		return ruleBase;
	}
}

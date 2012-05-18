package org.spantus.extractor.segments.online;

import org.spantus.extractor.segments.online.cluster.ExtremeOnlineClusterService;
import org.spantus.extractor.segments.online.cluster.ExtremeOnlineClusterServiceImpl;
import org.spantus.extractor.segments.online.rule.ClassifierRuleBaseService;
import org.spantus.extractor.segments.online.rule.ClassifierRuleBaseServiceImpl;

public abstract class ExtremeOnClassifierServiceFactory {
	/**
	 * 
	 * 
	 * @return 
	 */
	public static ExtremeOnlineClusterService createClusterService(){
//		ExtremeOnlineClusterServiceKnnImpl clusgerImpl = new ExtremeOnlineClusterServiceKnnImpl();
              ExtremeOnlineClusterServiceImpl clusgerImpl = new ExtremeOnlineClusterServiceImpl();
//		ExtremeOnlineClusterServiceStaticImpl clusgerImpl = new ExtremeOnlineClusterServiceStaticImpl();
		return clusgerImpl;
	}
	/**
	 * @return
	 */
	public static ClassifierRuleBaseService createClassifierRuleBaseService() {
		ClassifierRuleBaseServiceImpl ruleBase = new ClassifierRuleBaseServiceImpl();
		ruleBase.setClusterService(createClusterService());
		
//		ClassifierPostProcessServiceBaseImpl ruleBase = new ClassifierPostProcessServiceBaseImpl();
		return ruleBase;
	}
}

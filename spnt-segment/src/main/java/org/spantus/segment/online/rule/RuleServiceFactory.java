package org.spantus.segment.online.rule;

public abstract class RuleServiceFactory {
	static RuleBaseService ruleBaseService; 
	
	public static RuleBaseService createRuleBaseService(){
		if(ruleBaseService == null){
			ruleBaseService = new RuleBaseServiceImpl();
		}
		return ruleBaseService;
	}
}

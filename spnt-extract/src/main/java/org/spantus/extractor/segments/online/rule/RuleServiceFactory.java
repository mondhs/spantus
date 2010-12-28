package org.spantus.extractor.segments.online.rule;

public abstract class RuleServiceFactory {
	
	public ClassifierRuleBaseService createRuleBaseService(){
		return new ClassifierPostProcessServiceBaseImpl();
	}
}

package org.spantus.work.extractor.segments.online.rule;

import static org.spantus.extractor.segments.online.rule.ClassifierRuleBaseEnum.action.changePoint;
import static org.spantus.extractor.segments.online.rule.ClassifierRuleBaseEnum.action.changePointLastApproved;
import static org.spantus.extractor.segments.online.rule.ClassifierRuleBaseEnum.action.delete;
import static org.spantus.extractor.segments.online.rule.ClassifierRuleBaseEnum.action.initSegment;
import static org.spantus.extractor.segments.online.rule.ClassifierRuleBaseEnum.action.join;
import static org.spantus.extractor.segments.online.rule.ClassifierRuleBaseEnum.action.processSignal;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.mvel2.MVEL;
import org.spantus.extractor.segments.ExtremeSegmentServiceImpl;
import org.spantus.extractor.segments.online.ExtremeSegmentsOnlineCtx;
import org.spantus.extractor.segments.online.rule.AbstractClassifierRuleBaseServiceImpl;
import org.spantus.extractor.segments.online.rule.ClassifierRuleBaseEnum;
import org.spantus.logger.Logger;

public class ClassifierRuleBaseServiceMvelImpl extends
		AbstractClassifierRuleBaseServiceImpl {
	
	private static final Logger log = Logger
			.getLogger(ClassifierRuleBaseServiceMvelImpl.class);
	private List<Rule> rules;
	
	private ExtremeSegmentServiceImpl extremeSegmentService;
	
	public ClassifierRuleBaseServiceMvelImpl() {
	}
	


	
	
	@Override
	public String testOnRuleBase(ExtremeSegmentsOnlineCtx ctx) {
		Map<String, Object> param = prepareCtx(ctx);
		List<Rule> rules = getRules();
		
		// log.debug(""+MVEL.eval("lastSegment", param));
		String command = null;
		for (Rule ruleEntry : rules) {
//			log.error("exec [" + ruleEntry.getBody() + "]: ");
			Boolean commandInd = execute(ruleEntry.getCompiledRule(), param);
//			log.error("" + commandInd);			
			if (commandInd) {
				command = ruleEntry.getResult();
				ruleEntry.incCounter();
				log.debug("[testOnRuleBase]: finished {0}:{1}", ruleEntry.getName(),
						ruleEntry.getDescription());
				log.debug("[testOnRuleBase]:  param: \n \n  [{0}]\n",param);
				break;
			}
		}
		return command;
	}
	
	
	/**
	 * 
	 * @param compiledRule
	 * @param param
	 * @return
	 */
	private Boolean execute(Serializable compiledRule, Map<String, Object> param) {
		try{
			return  MVEL.executeExpression(compiledRule, param, Boolean.class);
		}catch (Exception e) {
			log.error(e);
			return Boolean.FALSE;
		}
	}
	/**
	 * 
	 * @param compiledRule
	 * @param param
	 * @return
	 */
	public Boolean execute(String strRule, Map<String, Object> param) {
		try{
			return  MVEL.evalToBoolean(strRule, param);
		}catch (Exception e) {
			log.error(e);
			return Boolean.FALSE;
		}
	}



	protected List<Rule> getRules() {
		if (rules == null) {
			rules = new LinkedList<Rule>();
			putRule(rules, "1", "currentSegment == null && ctx.featureInMin",
					initSegment,
					"Current not initialized. This first segment");
			putRule(rules, "2", "lastSegment == null && ctx.featureInMin",
					initSegment,
					"Previous not initialized. this is second segment");
			putRule(rules, "3", "lastSegment == null && ctx.featureInMax",
					processSignal, "Previous not initialized");
			putRule(rules, "4", "ctx.featureInMin", changePoint,
					"Found min. Possible change point");
			putRule(rules,
					"5",
					"ctx.featureInMax && distanceBetweenPaeks<180 && (lastLength+currentLength)<280",
					join, "Found max. join as between peaks not enough space");
			putRule(rules, "6", "ctx.featureInMax && isIncrease", join,
					"Found max. join as increase");
			putRule(rules,
					"7",
					"ctx.featureInMax && isDecrease && (lastLength+currentLength)  < 180",
					join, "Found max. join as decrease");
			putRule(rules, "8", "ctx.featureInMax && lastLength < 20", join,
					"too small last");
			putRule(rules, "9", "ctx.featureInMax && \"0\".equals(className)",
					delete, "Found max. delete segment as noise");
			putRule(rules, "10", "ctx.featureInMax", changePointLastApproved,
					"Found max. approve previous change point");
			putRule(rules, "last", "true", processSignal, "Rule not match");
		}
		return rules;
	}

	protected Rule putRule(List<Rule> rules, String name, String rule,
			ClassifierRuleBaseEnum.action action, String description) {
		Rule newRule = new Rule(name, rule, action.name());
		newRule.setDescription(description);
		newRule.setCompiledRule(MVEL.compileExpression(rule));
		// Assert.isTrue(rules.get(name)==null,"Already exists");
		rules.add(newRule);
		return newRule;
	}

	public void setRules(List<Rule> rules) {
		this.rules = rules;
	}

	public ExtremeSegmentServiceImpl getExtremeSegmentService() {
		if(extremeSegmentService == null){
			extremeSegmentService = new ExtremeSegmentServiceImpl();
		}
		return extremeSegmentService;
	}

	public void setExtremeSegmentService(
			ExtremeSegmentServiceImpl extremeSegmentService) {
		this.extremeSegmentService = extremeSegmentService;
	}







}

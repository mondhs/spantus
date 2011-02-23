package org.spantus.work.extractor.segments.online.rule;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.spantus.extractor.segments.online.ExtremeSegmentsOnlineCtx;
import org.spantus.extractor.segments.online.rule.ClassifierRuleBaseEnum.action;
import org.spantus.logger.Logger;


public class ClassifierRuleBaseServiceFileMvelImpl extends
		ClassifierRuleBaseServiceMvelImpl {

	private String path = "ClassifierRuleBase.csv";
	
	private static Logger log = Logger
			.getLogger(ClassifierRuleBaseServiceFileMvelImpl.class);

	public ClassifierRuleBaseServiceFileMvelImpl() {
		updateRules(getPath());
	}
	/**
	 * update rules from file
	 * @param currentPath
	 */
	protected void updateRules(String currentPath){
		List<Rule> rules = new ArrayList<Rule>();
		URL file = getClass().getClassLoader().getResource(currentPath);
		try {
			BufferedReader br = new BufferedReader(new FileReader(new File(file.toURI())));
			String line; 
			while((line = br.readLine()) != null) { 
				processLine(rules, line);
			}
		} catch (FileNotFoundException e) {
			log.error("Rule file not opended",e);
		} catch (IOException e) {
			log.error("Rule file not opended",e);
		} catch (URISyntaxException e) {
			log.error("Rule file not opended",e);
		} 
		if(!rules.isEmpty()){
			setRules(rules);
		}
	}
	
	@Override
	public String testOnRuleBase(ExtremeSegmentsOnlineCtx ctx) {
		String testOnRuleBase = super.testOnRuleBase(ctx);
		if(log.isDebugMode()){
			for (Rule rule : getRules()) {
				log.debug("rule #{0}: hit {1} <={2}", rule.getName(), rule.getCounter(), rule.getDescription());
			}
		}
		return testOnRuleBase;
	}
	
	protected Rule processLine(List<Rule> rules, String line){
		String[] lineArr = line.split(";");
		//commented out rule
		if(lineArr[0].startsWith("#")){
			return null;
		}
		//first line
		if("id".equals(lineArr[0])){
			return null;
		}
		String name = lineArr[0];
		String rule = lineArr[1];
		action actionName = action.valueOf(lineArr[2]);
		String description = lineArr[3];
		return putRule(rules, name, rule, actionName, description);
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}
	
}

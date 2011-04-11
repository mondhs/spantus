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
		super();
	}
	/**
	 * 
	 */
	protected void lazyInit(){
		if(getRules() != null){
			return;
		}
		List<Rule> rules = new ArrayList<Rule>();
		URL file = getClass().getClassLoader().getResource(path);
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
		lazyInit();
		String testOnRuleBase = super.testOnRuleBase(ctx);
		if(log.isDebugMode()){
			for (Rule rule : getRules()) {
				log.debug("rule #{0}: hit {1} <={2}", rule.getName(), rule.getCounter(), rule.getDescription());
			}
		}
		return testOnRuleBase;
	}
	
	protected Rule processLine(List<Rule> rules, String line){
		String[] lineArr = line.split("[,;]");
		if(lineArr.length == 0){
			return null;
		}
		String key = preprocess(lineArr[0]);
		if(key.startsWith("#")){
			return null;
		}
		if("id".equals(key)){
			return null;
		}
		String name = key;
		String rule = preprocess(lineArr[1]);
		action actionName = action.valueOf(preprocess(lineArr[2]));
		String description = preprocess(lineArr[3]);
		return putRule(rules, name, rule, actionName, description);
	}

	private String preprocess(String str) {
		str = str.trim();
		//trim from start and end
		str = str.replaceAll("(^\")|(\"$)", "");
		//trim double quotes
		str = str.replaceAll("\"\"", "\"");
		return str;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}
	
	
}

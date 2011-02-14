package org.spantus.work.extractor.segments.online.rule;

import java.io.Serializable;

/**
 * 
 * @author mondhs
 *
 */
public class Rule implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String name;
	private String body;
	private Serializable compiledRule;
	private String result;
	private String description;

	private int counter;
	
	public Rule() {
	}
	
	public Rule(String name, String body, String result) {
		super();
		this.name = name;
		this.body = body;
		this.result = result;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getBody() {
		return body;
	}
	public void setBody(String body) {
		this.body = body;
	}
	public String getResult() {
		return result;
	}
	public void setResult(String result) {
		this.result = result;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Serializable getCompiledRule() {
		return compiledRule;
	}

	public void setCompiledRule(Serializable compiledRule) {
		this.compiledRule = compiledRule;
	}

	public int incCounter() {
		return counter++;
	}

	public int getCounter() {
		return counter;
	}
}

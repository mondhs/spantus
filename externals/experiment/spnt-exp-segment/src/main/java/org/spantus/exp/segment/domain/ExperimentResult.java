package org.spantus.exp.segment.domain;

import org.spantus.core.domain.Entity;

/**
 * 
 * @author Mindaugas Greibus
 * 
 */
public class ExperimentResult extends Entity {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	Long experimentID;
	
	//Date experimentDate;
	
	String resource;
	
	String features;
	
	Float totalResult;


	public String getFeatures() {
		return features;
	}

	public void setFeatures(String features) {
		this.features = features;
	}

	public Float getTotalResult() {
		return totalResult;
	}

	public void setTotalResult(Float totalResult) {
		this.totalResult = totalResult;
	}

	public String getResource() {
		return resource;
	}

	public void setResource(String resource) {
		this.resource = resource;
	}
	@Override
	public String toString() {
		return getClass().getSimpleName() + "[" + features + ":" + getTotalResult() + "]";
	}

	public Long getExperimentID() {
		return experimentID;
	}

	public void setExperimentID(Long experimentID) {
		this.experimentID = experimentID;
	}

}

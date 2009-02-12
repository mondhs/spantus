package org.spantus.exp.segment.domain;

import java.util.Date;
import java.util.LinkedHashSet;
import java.util.Set;

import org.spantus.core.domain.Entity;

public class Experiment extends Entity{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	Set<ExperimentResult> experimentResults;
	
	Date experimentDate;

	public Set<ExperimentResult> getExperimentResults() {
		if(experimentResults == null){
			experimentResults = new LinkedHashSet<ExperimentResult>();
		}
		return experimentResults;
	}

	public void setExperimentResults(Set<ExperimentResult> experimentResults) {
		this.experimentResults = experimentResults;
	}

	public Date getExperimentDate() {
		return experimentDate;
	}

	public void setExperimentDate(Date experimentDate) {
		this.experimentDate = experimentDate;
	}

}

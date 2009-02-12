package org.spantus.exp.segment.services;

import java.util.List;

import org.spantus.exp.segment.beans.ComparisionResult;
import org.spantus.exp.segment.domain.ExperimentResult;

public interface ExperimentDao {
	
	public ExperimentResult save(ExperimentResult experimentResult);
	public ExperimentResult save(ComparisionResult comparisionResult, String features, 
			Long experimentID, 
			String experimentName);
	public List<ComparisionResult> findAllComparisionResult();

}

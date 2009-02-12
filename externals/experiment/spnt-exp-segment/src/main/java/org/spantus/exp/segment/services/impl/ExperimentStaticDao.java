package org.spantus.exp.segment.services.impl;

import java.util.LinkedList;
import java.util.List;

import org.spantus.exp.segment.beans.ComparisionResult;
import org.spantus.exp.segment.beans.ComparisionResultTia;
import org.spantus.exp.segment.domain.ExperimentResult;
import org.spantus.exp.segment.domain.ExperimentResultTia;
import org.spantus.exp.segment.services.ExperimentDao;
import org.spantus.logger.Logger;

public class ExperimentStaticDao implements ExperimentDao {
	
	protected Logger log = Logger.getLogger(getClass()); 
	
	protected Float totalResultThreshold = .20F;
	
	protected List<ComparisionResult> comparisionResults;

	public ExperimentResult save(ExperimentResult experimentResult) {
		log.debug(experimentResult.toString());
		return experimentResult;
	}

	public void destroy() {
		log.debug("destroy");
	}

	public ExperimentResult save(ComparisionResult comparisionResult,
			String features,
			Long experimentID, 
			String experimentName) {
		if(comparisionResult.getTotalResult()<getTotalResultThreshold()){
			getComparisionResults().add(comparisionResult);
		}
		
		return save(createExperimentResult(comparisionResult, features, experimentID, experimentName) );
	}
	
	protected ExperimentResult createExperimentResult(
			ComparisionResult comparisionResult, String features, 
			Long experimentID, 
			String experimentName){
		ExperimentResult experimentResult = new ExperimentResult();
		if(comparisionResult instanceof ComparisionResultTia){
			experimentResult = createTiaExperimentResult((ComparisionResultTia)comparisionResult, 
					features, experimentID, experimentName);
		}
		experimentResult.setFeatures(features);
		experimentResult.setExperimentID(experimentID);
		experimentResult.setResource(experimentName);
		experimentResult.setTotalResult(comparisionResult.getTotalResult());
		return experimentResult;
		
	}
	
	protected ExperimentResultTia createTiaExperimentResult(
			ComparisionResultTia comparisionResult, String features, 
			Long experimentID, 
			String experimentName){
		ExperimentResultTia experimentResult = new ExperimentResultTia();
		experimentResult.setDeltaVAF(comparisionResult.getDeltaVAF());
		experimentResult.setOffset(comparisionResult.getOffset());
		experimentResult.setOnset(comparisionResult.getOnset());
		experimentResult.setSteady(comparisionResult.getSteady());
		return experimentResult;
	
	}

	public List<ComparisionResult> findAllComparisionResult() {
		return getComparisionResults();
	}

	protected List<ComparisionResult> getComparisionResults() {
		if(comparisionResults==null){
			comparisionResults=new LinkedList<ComparisionResult>();
		}
		return comparisionResults;
	}

	public Float getTotalResultThreshold() {
		return totalResultThreshold;
	}

	public void setTotalResultThreshold(Float totalResultThreshold) {
		this.totalResultThreshold = totalResultThreshold;
	}

}

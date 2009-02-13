package org.spantus.exp.segment.exec.multi;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.spantus.core.beans.SampleInfo;
import org.spantus.core.marker.MarkerSet;
import org.spantus.core.threshold.IThreshold;
import org.spantus.exp.segment.beans.ComparisionResult;
import org.spantus.exp.segment.beans.ProcessReaderInfo;
import org.spantus.exp.segment.draw.AbstractGraphGenerator;
import org.spantus.exp.segment.services.ExperimentDao;
import org.spantus.exp.segment.services.impl.ExperimentStaticDao;
import org.spantus.segment.SegmentatorParam;
/**
 * 
 * @author Mindaugas Greibus
 * @since 0.0.1
 */
public class MultiFeatureSelectionExp extends AbstractGraphGenerator {

	private ExperimentDao experimentDao;
	
	private Long experimentID;
	
	private Map<String, Set<String>> compbinations;
	
	
	private SampleInfo info;
	
	MarkerSet expertMS;
	
	protected String getGeneratePath() {
		return super.getGeneratePath() + "multifeatures/";
	}

	
	
	/**
	 * 
	 * Main
	 * 
	 * @param args
	 */
	public static void main(final String[] args) {
		String expertMarksPath = DEFAULT_EXPERT_MARKS_PATH, testPath = DEFAULT_TEST_DATA_PATH;
		if (args.length > 0) {
			expertMarksPath = args[0];
		}
		if (args.length > 1) {
			testPath = args[1];
		}

		Integer combinationDepth = 3;
		Double thresholdCoef = 1.6D;
		
		MultiFeatureSelectionExp exp = new MultiFeatureSelectionExp();
		exp.setExpertMarksPath(expertMarksPath);
		exp.setTestPath(testPath);
		MarkerSet expertMS = exp.getWordMarkerSet(exp.getExpertMarkerSet());
		SampleInfo info = exp.getProcessReader().processReader(
				exp.getTestReader(),
				new ProcessReaderInfo(thresholdCoef));
		exp.setExpertMS(expertMS);
		exp.setInfo(info);
		Map<String, Set<String>> compbinations = 
			exp.getProcessReader().generateAllCompbinations(info.getThresholds(), combinationDepth);
		exp.setCompbinations(compbinations);
//		experiment.setGenerateCharts(false);
		exp.process(exp.getExpertMarksPath(), exp.getTestPath());
		
	}
	
	/**
	 * 
	 */
	@Override
	public List<ComparisionResult> compare() {
		for (Entry<String, Set<String>> combinatation : getCompbinations().entrySet()) {
			Set<IThreshold> threshods = getProcessReader().
				getThresholdSet(getInfo().getThresholds(), combinatation.getValue());
			
			compare(combinatation.getKey(),
				threshods,
				expertMS, 
				createDefaultOnlineParam());
		}
		log.info("[compare]Processed iteraions: " + getCompbinations().size());
		return getExperimentDao().findAllComparisionResult();
	}
	/**
	 * 
	 * @param thresholdsMap
	 * @param experMS
	 * @param param
	 */
	public void compare(String featureNames, Set<IThreshold> thresholds,
			MarkerSet experMS, SegmentatorParam param) {

		MarkerSet testMS = getSegmentator().extractSegments(thresholds, param);
		ComparisionResult result = getMakerComparison()
				.compare(experMS, testMS);
		result.setName(featureNames);
		getExperimentDao().save(result, featureNames, getExperimentID(),
				getExperimentName());
	}

	public ExperimentDao getExperimentDao() {
		if(experimentDao == null){
			experimentDao = new ExperimentStaticDao();
		}
		return experimentDao;
	}
	public void setExperimentDao(ExperimentDao experimentDao) {
		this.experimentDao = experimentDao;
	}


	public Long getExperimentID() {
		return experimentID;
	}


	public void setExperimentID(Long experimentID) {
		this.experimentID = experimentID;
	}
	
	public Map<String, Set<String>> getCompbinations() {
		return compbinations;
	}
	
	public void setCompbinations(Map<String, Set<String>> compbinations) {
		this.compbinations = compbinations;
	}



	
	public SampleInfo getInfo() {
		return info;
	}


	public void setInfo(SampleInfo info) {
		this.info = info;
	}


	public MarkerSet getExpertMS() {
		return expertMS;
	}


	public void setExpertMS(MarkerSet experMS) {
		this.expertMS = experMS;
	}







}

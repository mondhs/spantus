package org.spantus.exp.segment.exec.multi;

import java.util.List;
import java.util.Set;

import org.spantus.core.beans.SampleInfo;
import org.spantus.core.marker.MarkerSet;
import org.spantus.core.threshold.IThreshold;
import org.spantus.exp.segment.beans.ComparisionResult;
import org.spantus.exp.segment.beans.ProcessReaderInfo;
import org.spantus.exp.segment.draw.AbstractGraphGenerator;
import org.spantus.exp.segment.services.ExperimentDao;
import org.spantus.exp.segment.services.impl.ExperimentStaticDao;
import org.spantus.segment.SegmentatorParam;
import org.spantus.segment.online.OnlineDecisionSegmentatorParam;
/**
 * 
 * @author Mindaugas Greibus
 * @since 0.0.1
 */
public class MultiFeatureSelectionExp extends AbstractGraphGenerator {

	private ExperimentDao experimentDao;
	
	private Long experimentID;
	
	private Iterable<Set<String>> compbinations;
	
	private OnlineDecisionSegmentatorParam onlineParam;
	
	
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
		Iterable<Set<String>> compbinations = 
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
		long processed = 0l;
		for (Set<String> setCombination : this.getCompbinations()) {
			Set<IThreshold> threshods = getProcessReader().
				getThresholdSet(getInfo().getThresholds(), setCombination);
			
			compare(join(setCombination),
				threshods,
				expertMS, 
				getOnlineParam());
			processed++;
		}
		log.info("[compare]Processed iteraions: " + processed);
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
	///
	//Protected
	///
	protected String join(Set<String> set){
		StringBuffer buf = new StringBuffer();
		String separator = "";
		for (String name : set) {
			String tempName = name;  
			tempName = tempName.replace("BUFFERED_", "");
			tempName = tempName.replace("_EXTRACTOR", "");
			buf.append(separator).append(tempName);
			separator = " ";
		}
		return buf.toString();	
	}
	
	///Getters and setters
	
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
	
	public Iterable<Set<String>> getCompbinations() {
		return compbinations;
	}
	
	public void setCompbinations(Iterable<Set<String>> compbinations) {
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
	
	public void setOnlineParam(OnlineDecisionSegmentatorParam onlineParam) {
		this.onlineParam = onlineParam;
	}

	protected OnlineDecisionSegmentatorParam getOnlineParam(){
		if(onlineParam == null){
			onlineParam = createDefaultOnlineParam();
		}
		return onlineParam;
	}






}

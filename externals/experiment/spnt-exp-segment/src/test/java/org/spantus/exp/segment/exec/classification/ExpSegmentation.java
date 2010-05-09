package org.spantus.exp.segment.exec.classification;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.spantus.core.marker.MarkerSetHolder;
import org.spantus.core.threshold.ClassifierEnum;
import org.spantus.exp.segment.beans.ComparisionResult;
import org.spantus.exp.segment.services.ExpServiceFactory;
import org.spantus.exp.segment.services.MakerComparison;
import org.spantus.exp.segment.services.impl.ComarisionFacadeImpl;
import org.spantus.extractor.impl.ExtractorEnum;
import org.spantus.logger.Logger;
import org.spantus.segment.online.OnlineDecisionSegmentatorParam;
import org.spantus.utils.CollectionUtils;
import org.spantus.utils.FileUtils;
import org.spantus.utils.StringUtils;
import org.spantus.work.services.MarkerDao;
import org.spantus.work.services.WorkServiceFactory;
/**
 * 
 * @author Mindaugas Greibus
 * 
 * @since 0.2
 * Created May 4, 2010
 *
 */
public class ExpSegmentation {
	private ComarisionFacadeImpl comarisionFacade;
	private MarkerDao markerDao;
	private MakerComparison makerComparison;
	//tune
	private ExtractorEnum[] extractors;
	private OnlineDecisionSegmentatorParam param;
	private Logger log = Logger.getLogger(ExpSegmentation.class);
	
	
	/**
	 * 
	 */
	public ExpSegmentation() {
	}
	/**
	 * 
	 */
	public void init(){
		comarisionFacade = new ComarisionFacadeImpl();
		markerDao = WorkServiceFactory.createMarkerDao();
		makerComparison = ExpServiceFactory.createMakerComparison();
	}
	public List<ComparisionResult> multipleMixtureExperiments(List<ExpCriteria> expCriterias){
		List<ComparisionResult> results = new ArrayList<ComparisionResult>();
		for (ExpCriteria expCriteria : expCriterias) {
			results.addAll(multipleMixtureExperiment(expCriteria));
		}
		return results;
	}
	/**
	 * 
	 * @param signalName
	 * @param noiseNames
	 * @param markerName
	 * @return
	 */
	public List<ComparisionResult> multipleMixtureExperiment(ExpCriteria expCriteria){
		List<ComparisionResult> results = new ArrayList<ComparisionResult>();
		List<String> noiseNamesList = expCriteria.getNoiseNames();
		if(noiseNamesList == null){
			noiseNamesList = CollectionUtils.toList("");
		}
		
		for (String signal : expCriteria.getSignalNames()) {
			for (String noise : noiseNamesList) {
				noise = "".equals(noise)?null:noise;
				List<String> signals = CollectionUtils.toList(signal, noise);
				ComparisionResult result = singleMixtureExperiment(signals, expCriteria.getMarkerName());
				results.add(result);
			}
		}
		return results;
	}
	/**
	 * 
	 * @param signals
	 * @param markerName
	 * @return
	 */
	public ComparisionResult singleMixtureExperiment(List<String> signals, String markerName){
		//remove this hack
		if(ClassifierEnum.offline.equals(getComarisionFacade().getClassifier())){
			if(signals.get(0).contains("__")){
				ExpSegmentationUtil.addThresholdCoef(getComarisionFacade().getExtractorParams(), 7F);
			}else {
				ExpSegmentationUtil.addThresholdCoef(getComarisionFacade().getExtractorParams(), 1F);
			}
		}else {
			getComarisionFacade().getExtractorParams().clear();
		}
		
		MarkerSetHolder testMarkerSet = comarisionFacade.calculateMarkers(
				signals, getExtractors(), getParam());
		MarkerSetHolder holder = markerDao.read(
				new File(markerName));
		ComparisionResult result = makerComparison
				.compare(holder, testMarkerSet);
		generateExpName(result, signals);
		return result;
	}
	
	public void generateExpName(ComparisionResult result, List<String> signals){
		StringBuilder sb = new StringBuilder();
		sb.append(comarisionFacade.getSegmentation()).append(";").append(comarisionFacade.getClassifier()).append(";");
		String separator = "";
		for (String signal : signals) {
			if(StringUtils.hasText(signal)){
				sb.append(separator).append(FileUtils.truncateDir(signal).replaceAll(".wav", ""));
				separator ="-";
			}
		}
		result.setName(sb.toString());
		
	}
	public void logResult(List<ComparisionResult> results){
		StringBuilder sb = new StringBuilder();
		sb.append("Segmentation;Classifier;Name;Result\n");
		for (ComparisionResult comparisionResult : results) {
			ComparisionResult result = comparisionResult;
//			ComparisionResultTia result = (ComparisionResultTia)comparisionResult;
			sb.append(result.getName()).append(";")
			.append(result.getTotalResult()).append("\n");
			
		}
		log.debug("\n{0}" , sb);
	}

	////////////////////////////////////////// MAIN
	public static void main(String[] args) {
		List<ComparisionResult> results = null;
		List<ExpCriteria> criterias = null;
		ExpSegmentation expSegmentation = ExpSegmentationFactory.createWavExpSegmentation();
		expSegmentation.getComarisionFacade().setClassifier(ClassifierEnum.rulesOnline);
		
//		results = ExpSegmentationUtil.acceleromerData(ExpSegmentationUtil.SUFIX_accelerometer,
//				ExpSegmentationUtil.SUFIX_iaccelerometer
//				);
//		results = ExpSegmentationUtil.wavNoizeusData(ExpSegmentationUtil.SUFIX_on_off_up_down);
		criterias = ExpSegmentationFactory.createNoizeusExpCriterias(
				ExpSegmentationUtil.NOIZEUS_01,
				ExpSegmentationUtil.NOIZEUS_02,
				ExpSegmentationUtil.NOIZEUS_04,
				ExpSegmentationUtil.NOIZEUS_07,
				ExpSegmentationUtil.NOIZEUS_10,
				ExpSegmentationUtil.NOIZEUS_21
				);
		results = expSegmentation.multipleMixtureExperiments(criterias);
		expSegmentation.logResult(results);
	}
	
	
	///getters and setters
	public ComarisionFacadeImpl getComarisionFacade() {
		return comarisionFacade;
	}
	public void setComarisionFacade(ComarisionFacadeImpl comarisionFacade) {
		this.comarisionFacade = comarisionFacade;
	}
	public MarkerDao getMarkerDao() {
		return markerDao;
	}
	public void setMarkerDao(MarkerDao markerDao) {
		this.markerDao = markerDao;
	}
	public MakerComparison getMakerComparison() {
		return makerComparison;
	}
	public void setMakerComparison(MakerComparison makerComparison) {
		this.makerComparison = makerComparison;
	}
	public ExtractorEnum[] getExtractors() {
		if(extractors == null){
			extractors = new ExtractorEnum[] { ExtractorEnum.ENERGY_EXTRACTOR};
		}
		return extractors;
	}
	public void setExtractors(ExtractorEnum[] extractors) {
		this.extractors = extractors;
	}
	public OnlineDecisionSegmentatorParam getParam() {
		if(param == null){
			OnlineDecisionSegmentatorParam param = new OnlineDecisionSegmentatorParam();
			param.setMinSpace(0L);
			param.setMinLength(0L);
			param.setExpandStart(0L);
			param.setExpandEnd(0L);
			this.param = param;
		}
		return param;
	}
	public void setParam(OnlineDecisionSegmentatorParam param) {
		this.param = param;
	}
}

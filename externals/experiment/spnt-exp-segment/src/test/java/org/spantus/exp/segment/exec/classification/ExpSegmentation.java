package org.spantus.exp.segment.exec.classification;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.spantus.core.extractor.ExtractorParam;
import org.spantus.core.marker.MarkerSetHolder;
import org.spantus.exp.segment.beans.ComparisionResult;
import org.spantus.exp.segment.services.ExpServiceFactory;
import org.spantus.exp.segment.services.MakerComparison;
import org.spantus.exp.segment.services.impl.ComarisionFacade;
import org.spantus.exp.segment.services.impl.ComarisionFacadeImpl;
import org.spantus.extractor.impl.ExtractorEnum;
import org.spantus.logger.Logger;
import org.spantus.segment.online.OnlineDecisionSegmentatorParam;
import org.spantus.utils.CollectionUtils;
import org.spantus.utils.FileUtils;
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
	/**
	 * 
	 * @param signalName
	 * @param noiseNames
	 * @param markerName
	 * @return
	 */
	public List<ComparisionResult> multipleMixtureExperiment(List<String> signalNames, List<String> noiseNames, String markerName){
		List<ComparisionResult> results = new ArrayList<ComparisionResult>();
		for (String signal : signalNames) {
			for (String noise : noiseNames) {
				noise = "".equals(noise)?null:noise;
				List<String> signals = CollectionUtils.toList(signal, noise);
				ComparisionResult result = singleMixtureExperiment(signals, markerName);
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
		for (String signal : signals) {
			sb.append(FileUtils.truncateDir(signal)).append(";");
		}
		result.setName(sb.toString());
		
	}
	public void logResult(List<ComparisionResult> results){
		for (ComparisionResult comparisionResult : results) {
			ComparisionResult result = comparisionResult;
//			ComparisionResultTia result = (ComparisionResultTia)comparisionResult;
			log.debug("Name {0}; Result: {1}" , result.getName(), result.getTotalResult());
		}
	}
	/**
	 * 
	 */
	public static void acceleromerData(){
		ExpSegmentation expSegmentation = new ExpSegmentation();
		expSegmentation.init();
//		expSegmentation.setExtractors(
//				new ExtractorEnum[] { ExtractorEnum.SIGNAL_ENTROPY_EXTRACTOR });
		expSegmentation.setExtractors(
				new ExtractorEnum[] { ExtractorEnum.ENERGY_EXTRACTOR });
		String root = "/home/studijos/wav/data/";
		String signalName = root + "iaccelerometer.txt";
		String markerName = root + "iaccelerometer_system.mspnt.xml";
		root += "noises/";
		String[] noisesArr = new String[]{
				null,
				root + "accelerometer.noises.txt",
				root + "accelerometer.noises.1-2.txt",
				root + "accelerometer.noises.2-0.txt",
				root + "accelerometer.noises.5-0.txt",
				root + "accelerometer.noises.10-0.txt"
				};
		List<String> noises = CollectionUtils.toList(noisesArr);
		

		expSegmentation.getParam().setExpandEnd(30L);
		expSegmentation.getParam().setExpandStart(30L);

		List<ComparisionResult> results = expSegmentation.multipleMixtureExperiment(CollectionUtils.toList(signalName), noises, markerName);
		expSegmentation.logResult(results);
	}
	/**
	 * 
	 */
	public static void wavData(){
		ExpSegmentation expSegmentation = new ExpSegmentation();
		expSegmentation.init();
		Map<String, ExtractorParam> extractorParams = new HashMap<String, ExtractorParam>();
		expSegmentation.setExtractors(
				new ExtractorEnum[] { ExtractorEnum.SPECTRAL_FLUX_EXTRACTOR });
		expSegmentation.getParam().setMinSpace(60L);
		expSegmentation.getParam().setMinLength(90L);
		expSegmentation.getParam().setExpandEnd(60L);
		expSegmentation.getParam().setExpandStart(60L);
		
		String root = "/home/studijos/wav/on_off_up_down_wav/";
		String markerName = root + "exp/_on_off_up_down.mspnt.xml";
		String[] signalArr = new String[]{
				root + "dentist.wav",
				 root + "hammer.wav",
				 root + "keyboard.wav",
				 root + "original.wav",
				 root + "plane.wav",
				 root + "rain.wav",
				 root + "shower.wav",
				 root + "traffic.wav"
				};

		
		List<String> noises = CollectionUtils.toList("");

		
//		ExtractorParam extractorParam = new ExtractorParam();
//		extractorParam.getProperties().put(ExtractorModifiersEnum.mean.name(), Boolean.TRUE);
//		extractorParam.setClassName(ExtractorEnum.SPECTRAL_FLUX_EXTRACTOR.name());
//		extractorParams.put(extractorParam.getClassName(), extractorParam);
//		expSegmentation.comarisionFacade.setExtractorParams(extractorParams);
		
		List<ComparisionResult> results = expSegmentation.multipleMixtureExperiment(
				CollectionUtils.toList(signalArr), noises, markerName);
		
		
		
		expSegmentation.logResult(results);
	}

	////////////////////////////////////////// MAIN
	public static void main(String[] args) {
		acceleromerData();
//		wavData();
	}
	
	
	///getters and setters
	public ComarisionFacade getComarisionFacade() {
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

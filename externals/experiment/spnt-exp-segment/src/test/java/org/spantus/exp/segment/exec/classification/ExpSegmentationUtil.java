package org.spantus.exp.segment.exec.classification;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.spantus.exp.segment.beans.ComparisionResult;
import org.spantus.extractor.impl.ExtractorEnum;
import org.spantus.utils.CollectionUtils;

public abstract class ExpSegmentationUtil {
	public static String NOIZEUS_ROOT = "/home/studijos/wav/noizeus_exp/";
	public static String NOIZEUS_01 = "sp01/";
	public static String NOIZEUS_21 = "sp21/";
	public static String SUFIX_on_off_up_down  = "on_off_up_down/";
	public static String SUFIX_iaccelerometer = "iaccelerometer";
	public static String SUFIX_accelerometer = "accelerometer";
	
	
	/**
	 * 
	 */
	public static List<ComparisionResult> acceleromerData(String node){
		ExpSegmentation expSegmentation = new ExpSegmentation();
		expSegmentation.init();
		expSegmentation.setExtractors(
				new ExtractorEnum[] { ExtractorEnum.ENERGY_EXTRACTOR });
		String root = "/home/studijos/wav/data/";
		String signalName = root + node+".txt";
		String markerName = root + node + "_system.mspnt.xml";
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
		

//		expSegmentation.getParam().setExpandEnd(30L);
//		expSegmentation.getParam().setExpandStart(30L);

		List<ComparisionResult> results = expSegmentation.multipleMixtureExperiment(CollectionUtils.toList(signalName), noises, markerName);
		return results;
	}
	/**
	 * 
	 * @return
	 */
	public static ExpSegmentation createWavExpSegmentation(){
		ExpSegmentation expSegmentation = new ExpSegmentation();	
		expSegmentation.init();
		
		expSegmentation.setExtractors(
				new ExtractorEnum[] { ExtractorEnum.SPECTRAL_FLUX_EXTRACTOR });
		expSegmentation.getParam().setMinSpace(60L);
		expSegmentation.getParam().setMinLength(90L);
		expSegmentation.getParam().setExpandEnd(60L);
		expSegmentation.getParam().setExpandStart(60L);
		
//		Map<String, ExtractorParam> extractorParams = new HashMap<String, ExtractorParam>();
//		ExtractorParam extractorParam = new ExtractorParam();
//		extractorParam.getProperties().put(ExtractorModifiersEnum.stdev.name(), Boolean.TRUE);
//		extractorParam.setClassName(ExtractorEnum.SPECTRAL_FLUX_EXTRACTOR.name());
//		extractorParams.put(extractorParam.getClassName(), extractorParam);
//		expSegmentation.getComarisionFacade().setExtractorParams(extractorParams);
		
		return expSegmentation;
	}

	
	/**
	 * 
	 */
	public static List<ComparisionResult> wavNoizeusData(String node){
		ExpSegmentation expSegmentation = createWavExpSegmentation();
		
		String root = NOIZEUS_ROOT+node;
		String markerName = findMarkerFullPath(root, null);
		List<String> signalList = findWavFullPath(root, null);
		
		List<ComparisionResult> results = expSegmentation.multipleMixtureExperiment(
				signalList, null, markerName);
		
		return results;
	}
	/**
	 * 
	 * @param root
	 * @return
	 */
	public static String findMarkerFullPath(String root, String subdir){
		File rootDir = new File(root);
		if(rootDir.isDirectory()){
			for (String file : rootDir.list()) {
				if(file.endsWith("mspnt.xml")){
					return root + file;
				}
				
			}
		}
		//if not found search for subdir
		return null;
	}
	/**
	 * 
	 */
	public static List<String> findWavFullPath(String root, String subdir){
		File rootDir = new File(root);
		
		List<String> results = new ArrayList<String>();
		
 		if(rootDir.isDirectory()){
			for (String file : rootDir.list()) {
				if(file.endsWith("wav")){
					results.add( root + file);
				}
				
			}
		}
		//if not found search for subdir
		return results;
	}
}

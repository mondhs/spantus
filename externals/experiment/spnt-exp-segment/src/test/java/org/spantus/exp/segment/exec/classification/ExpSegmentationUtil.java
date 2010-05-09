package org.spantus.exp.segment.exec.classification;

import java.util.HashMap;
import java.util.Map;

import org.spantus.core.extractor.ExtractorParam;
import org.spantus.extractor.impl.ExtractorEnum;
import org.spantus.utils.ExtractorParamUtils;

public abstract class ExpSegmentationUtil {
	public static String NOIZEUS_ROOT = "/home/studijos/wav/noizeus_exp/";
	public static String NOIZEUS_01 = "sp01/";
	public static String NOIZEUS_02 = "sp02/";
	public static String NOIZEUS_04 = "sp04/";
	public static String NOIZEUS_07 = "sp07/";
	public static String NOIZEUS_10 = "sp10/";
	public static String NOIZEUS_21 = "sp21/";

	public static String SUFIX_on_off_up_down  = "on_off_up_down/";
	public static String SUFIX_iaccelerometer = "iaccelerometer";
	public static String SUFIX_accelerometer = "accelerometer";
	


	
	public static void addThresholdCoef(Map<String, ExtractorParam> extractorParams, Float coef){
		ExtractorParam extractorParam = createExtractorParam(ExtractorEnum.SPECTRAL_FLUX_EXTRACTOR,
				ExtractorParamUtils.commonParam.threasholdCoef, coef);
		extractorParams = applyParams(extractorParams, extractorParam);

	}
	
	/**
	 * 
	 * @param extractorEnum
	 * @param paramName
	 * @param value
	 * @return
	 */
	public static ExtractorParam createExtractorParam(ExtractorEnum extractorEnum, Enum<?> paramName, Object value){
		ExtractorParam newExtractorParam = new ExtractorParam();
		newExtractorParam.setClassName(extractorEnum.name());
		newExtractorParam.getProperties().put(paramName.name(), value);
		return newExtractorParam;
	}
	/**
	 * 
	 * @param extractorParams
	 * @param newExtractorParam
	 * @return
	 */
	public static Map<String, ExtractorParam> applyParams(Map<String, ExtractorParam> extractorParams, 
			ExtractorParam newExtractorParam){
		if(extractorParams == null){
			extractorParams = new HashMap<String, ExtractorParam>();
		}
		ExtractorParam exsitParam = extractorParams.get(newExtractorParam.getClassName());
		if(exsitParam==null){
			extractorParams.put(newExtractorParam.getClassName(), newExtractorParam);
		}else{
			if(exsitParam.getProperties() == null){
				exsitParam.setPropertiesProp(new HashMap<String, Object>());
			}
			exsitParam.getProperties().putAll(newExtractorParam.getProperties());
		}
		return extractorParams;
	}
	
}

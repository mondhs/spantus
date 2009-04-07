package org.spantus.utils;

import java.util.Map;

import org.spantus.core.extractor.ExtractorParam;

public abstract class ExtractorParamUtils {
	public enum commonParam{thresholdType, threasholdCoef};
	
	public static Boolean getBoolean(ExtractorParam param,String propertyName){
		return (Boolean)param.getProperties().get(propertyName);
	}
	public static Boolean getBoolean(ExtractorParam param,String propertyName, Boolean defaultVal){
		Boolean val = getBoolean(param, propertyName);
		if(val == null){
			val = defaultVal;
		}
		return val;
	}

	public static void setBoolean(ExtractorParam param,String propertyName, Boolean val){
		param.getProperties().put(propertyName, val);
	}

	public static String getString(ExtractorParam param,String propertyName){
		return (String)param.getProperties().get(propertyName);
	}
	public static String getString(ExtractorParam param,String propertyName, String defaultVal){
		return ExtractorParamUtils.<String>getValue(param, propertyName, defaultVal);
	}
	public static <T> void setValue(ExtractorParam param,String propertyName, T val){
		param.getProperties().put(propertyName, val);
	}

	@SuppressWarnings("unchecked")
	public static <E> E getValue(ExtractorParam param,String propertyName){
		return (E)param.getProperties().get(propertyName);
	}
	public static <E> E getValue(ExtractorParam param,String propertyName, E defaultVal){
		E val = ExtractorParamUtils.<E>getValue(param, propertyName);
		if(val == null){
			val = defaultVal;
		}
		return val;
	}
	public static void setString(ExtractorParam param,String propertyName, String val){
		param.getProperties().put(propertyName, val);
	}

	
	public static ExtractorParam getSafeParam(Map<String, ExtractorParam> paramMap, String key){
		ExtractorParam param = paramMap.get(key);
		if(param == null){
			param = new ExtractorParam();
			paramMap.put(key, param);
		}
		return param;
	}
}

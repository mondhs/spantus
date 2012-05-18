package org.spantus.work.services;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

import javax.sound.sampled.AudioFormat;

import org.spantus.core.extractor.DefaultExtractorConfig;
import org.spantus.core.extractor.ExtractorParam;
import org.spantus.core.extractor.IExtractorConfig;
import org.spantus.exception.ProcessingException;
import org.spantus.extractor.ExtractorConfigUtil;
import org.spantus.utils.ExtractorParamUtils;
import org.spantus.utils.StringUtils;

public class ConfigPropertiesDao implements ConfigDao {
	
	public static final String key_threshold_leaningPeriod = "threshold.leaningPeriod";
	public static final String key_format_recordSampleRate = "format.recordSampleRate";
	public static final String key_format_window_InMilsec = "format.window.sizeInMilsec";
	public static final String key_format_extractors = "format.extractors";
	public static final String key_format_window_OverlapInPercent = "format.window.OverlapInPercent";
	public static final String key_format_pathOutput = "format.pathOutput";
	public static final String key_threshold_coef = "threshold.coef";
	public static final String key_segmentation_minLength = "segmentation.minLength";
	public static final String key_segmentation_minSpace= "segmentation.minSpace";
	public static final String key_segmentation_expandStart = "segmentation.expandStart";
	public static final String key_segmentation_expandEnd = "segmentation.expandEnd";
	public static final String key_segmentation_classifier = "segmentation.classifier";
	public static final String key_segmentation_modifier_smooth = "segmentation.modifier.smooth";
	public static final String key_segmentation_modifier_mean= "segmentation.modifier.mean";

	public IExtractorConfig read(File file) {
		Properties properties = new Properties();
		try {
			properties.load(new FileInputStream(file));
		} catch (IOException e) {
			new ProcessingException(e);
		}
		IExtractorConfig config = new DefaultExtractorConfig(); 
		configDefaults(config, properties);
		return config;
	}
	
	
	public IExtractorConfig read(File file, AudioFormat format) {
		Properties properties = new Properties();
		try {
			properties.load(new FileInputStream(file));
		} catch (IOException e) {
			new ProcessingException(e);
		}
		DefaultExtractorConfig config = (DefaultExtractorConfig)ExtractorConfigUtil.defaultConfig(format); 
		configDefaults(config, properties);
		
		return config;
	}
	
	protected void configDefaults(IExtractorConfig config, Properties properties){
		ExtractorParam param = new ExtractorParam();
		param.setClassName(DefaultExtractorConfig.class.getName());
		config.setSampleRate(setFloatValue(param, key_format_recordSampleRate, properties,8000D));
		Integer windowLengthInMilsec = Integer.valueOf(properties.getProperty(key_format_window_InMilsec));
		Integer overlapInPerc = Integer.valueOf(properties.getProperty(key_format_window_OverlapInPercent));
		
		windowLengthInMilsec = windowLengthInMilsec == null?33:windowLengthInMilsec;
		overlapInPerc = overlapInPerc == null?66:overlapInPerc;
		

		DefaultExtractorConfig sizedConfig = (DefaultExtractorConfig)ExtractorConfigUtil.defaultConfig(
				config.getSampleRate(), windowLengthInMilsec, overlapInPerc); 
		config.setWindowSize(sizedConfig.getWindowSize());
		config.setWindowOverlap(sizedConfig.getWindowOverlap());
		
		setLongValue(param, key_threshold_leaningPeriod, properties,5000L);
		setLongValue(param, key_segmentation_minLength, properties,191L);
		setLongValue(param, key_segmentation_minSpace, properties,61L);
		setLongValue(param, key_segmentation_expandStart, properties,160L);
		setLongValue(param, key_segmentation_expandEnd, properties,160L);
		setFloatValue(param, key_threshold_coef, properties,6D);
		setStringValue(param, key_format_pathOutput, properties);
		setStringValue(param, key_segmentation_classifier, properties);
		setBooleanValue(param, key_segmentation_modifier_smooth, properties);
		setBooleanValue(param, key_segmentation_modifier_mean, properties);
		
		
		
		config.getParameters().put(param.getClassName(), param);
		String extractorStr = properties.getProperty(key_format_extractors);
		if(StringUtils.hasText(extractorStr)){
			for (String extr : extractorStr.split(",")) {
				config.getExtractors().add(extr);
			}
		}
		
	}
	

	
	protected void setLongValue(ExtractorParam param, String key, Properties properties){
		ExtractorParamUtils.<Long>setValue(param, 
				key, 
				Long.valueOf(properties.getProperty(key)));
	}
	
	protected void setLongValue(ExtractorParam param, String key, Properties properties, Long defaultValue){
		if(properties.getProperty(key) == null){
			ExtractorParamUtils.<Long>setValue(param, 
					key, defaultValue);
			return;
		}
		setLongValue(param, key, properties);
	}

	protected Double setDoubleValue(ExtractorParam param, String key, Properties properties){
		Double f = Double.valueOf(properties.getProperty(key));
		ExtractorParamUtils.<Double>setValue(param, 
				key, 
				f);
		return f;
	}
	protected Double setFloatValue(ExtractorParam param, String key, Properties properties, Double defaultValue){
		if(properties.getProperty(key) == null){
			ExtractorParamUtils.<Double>setValue(param, 
					key, defaultValue);
			return defaultValue;
		}
		return setDoubleValue(param, key, properties);
	}
	
	protected Integer setIntValue(ExtractorParam param, String key, Properties properties, Integer defaultValue){
		if(properties.getProperty(key) == null){
			ExtractorParamUtils.<Integer>setValue(param, 
					key, defaultValue);
			return defaultValue;
		}
		return setIntValue(param, key, properties);
	}
	
	protected Integer setIntValue(ExtractorParam param, String key, Properties properties){
		Integer f = Integer.valueOf(properties.getProperty(key));
		ExtractorParamUtils.<Integer>setValue(param, 
				key, 
				f);
		return f;
	}

	protected void setStringValue(ExtractorParam param, String key, Properties properties){
		ExtractorParamUtils.<String>setValue(param, 
				key, 
				String.valueOf(properties.getProperty(key)));
	}
	
	protected void setBooleanValue(ExtractorParam param, String key, Properties properties){
		ExtractorParamUtils.<Boolean>setValue(param, 
				key, 
				Boolean.valueOf(properties.getProperty(key)));
	}

	
	public IExtractorConfig read(InputStream inputStream) {
		return null;
	}

	public void write(IExtractorConfig config, File file) {

	}

	public void write(IExtractorConfig config, OutputStream outputStream) {

	}

}

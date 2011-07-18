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
		
		Integer windowLengthInMilsec = Integer.valueOf(properties.getProperty(key_format_window_InMilsec));
		Integer overlapInPerc = Integer.valueOf(properties.getProperty(key_format_window_OverlapInPercent));
		windowLengthInMilsec = windowLengthInMilsec == null?30:windowLengthInMilsec;
		overlapInPerc = overlapInPerc == null?66:overlapInPerc;
		
		DefaultExtractorConfig config = (DefaultExtractorConfig)ExtractorConfigUtil.defaultConfig(
				(double)format.getSampleRate(), windowLengthInMilsec, overlapInPerc); 
//		config.setBitsPerSample(format.getSampleSizeInBits());
		
		configDefaults(config, properties);
		
		return config;
	}
	
	protected void configDefaults(IExtractorConfig config, Properties properties){
		ExtractorParam param = new ExtractorParam();
		param.setClassName(DefaultExtractorConfig.class.getName());
		config.setSampleRate(setFloatValue(param, key_format_recordSampleRate, properties,8000D));
		setLongValue(param, key_threshold_leaningPeriod, properties,5000L);
		setLongValue(param, key_segmentation_minLength, properties,191L);
		setLongValue(param, key_segmentation_minSpace, properties,61L);
		setLongValue(param, key_segmentation_expandStart, properties,160L);
		setLongValue(param, key_segmentation_expandEnd, properties,160L);
		setFloatValue(param, key_threshold_coef, properties,6D);
		setStringValue(param, key_format_pathOutput, properties);
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

	protected void setStringValue(ExtractorParam param, String key, Properties properties){
		ExtractorParamUtils.<String>setValue(param, 
				key, 
				String.valueOf(properties.getProperty(key)));
	}

	
	public IExtractorConfig read(InputStream inputStream) {
		// TODO Auto-generated method stub
		return null;
	}

	public void write(IExtractorConfig config, File file) {
		// TODO Auto-generated method stub

	}

	public void write(IExtractorConfig config, OutputStream outputStream) {
		// TODO Auto-generated method stub

	}

}

package org.spantus.work.services;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

import org.spantus.core.extractor.DefaultExtractorConfig;
import org.spantus.core.extractor.ExtractorParam;
import org.spantus.core.extractor.IExtractorConfig;
import org.spantus.exception.ProcessingException;
import org.spantus.utils.ExtractorParamUtils;

public class ConfigPropertiesDao implements ConfigDao {
	
	public static final String key_threshold_leaningPeriod = "threshold.leaningPeriod";
	public static final String key_format_sampleRate = "format.sampleRate";
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
		String format_sampleRate = properties.getProperty(key_format_sampleRate);
		
		DefaultExtractorConfig config = new DefaultExtractorConfig(); 
		config.setSampleRate(Float.valueOf(format_sampleRate));
		ExtractorParam param = new ExtractorParam();
		param.setClassName(DefaultExtractorConfig.class.getName());
		setLongValue(param, key_threshold_leaningPeriod, properties);
		setLongValue(param, key_segmentation_minLength, properties);
		setLongValue(param, key_segmentation_minSpace, properties);
		setLongValue(param, key_segmentation_expandStart, properties);
		setLongValue(param, key_segmentation_expandEnd, properties);
		setFloatValue(param, key_threshold_coef, properties);
		
		config.getParameters().put(param.getClassName(), param);
		
		return config;
	}
	
	protected void setLongValue(ExtractorParam param, String key, Properties properties){
		ExtractorParamUtils.<Long>setValue(param, 
				key, 
				Long.valueOf(properties.getProperty(key)));
	}

	protected void setFloatValue(ExtractorParam param, String key, Properties properties){
		ExtractorParamUtils.<Float>setValue(param, 
				key, 
				Float.valueOf(properties.getProperty(key)));
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

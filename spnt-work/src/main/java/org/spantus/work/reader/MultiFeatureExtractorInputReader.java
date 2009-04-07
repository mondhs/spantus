package org.spantus.work.reader;

import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.spantus.core.extractor.ExtractorParam;
import org.spantus.core.extractor.IExtractor;
import org.spantus.core.extractor.IExtractorVector;
import org.spantus.core.extractor.IExtractorConfig;
import org.spantus.core.extractor.IExtractorInputReader;
import org.spantus.core.extractor.IGeneralExtractor;
import org.spantus.core.threshold.AbstractThreshold;
import org.spantus.core.threshold.IThreshold;
import org.spantus.core.threshold.ThresholdEnum;
import org.spantus.exception.ProcessingException;
import org.spantus.extractor.ExtractorInputReader;
import org.spantus.extractor.impl.ExtractorEnum;
import org.spantus.extractor.impl.ExtractorTypeEnum;
import org.spantus.extractor.impl.ExtractorUtils;
import org.spantus.mpeg7.Mpeg7ExtractorEnum;
import org.spantus.mpeg7.Mpeg7ExtractorUtils;
import org.spantus.mpeg7.extractors.Mpeg7ExtractorInputReader;
import org.spantus.utils.ExtractorParamUtils;
import org.spantus.utils.StringUtils;

public class MultiFeatureExtractorInputReader implements IExtractorInputReader {
	

	ExtractorInputReader defaultReader;
	Mpeg7ExtractorInputReader mpeg7Reader;
	
	public MultiFeatureExtractorInputReader() {
		defaultReader = new ExtractorInputReader();
		mpeg7Reader = new Mpeg7ExtractorInputReader();
	}

	public ExtractorInputReader getDefaultReader() {
		return defaultReader;
	}




	public Mpeg7ExtractorInputReader getMpeg7Reader() {
		return mpeg7Reader;
	}




	
	public Set<IExtractor> getExtractorRegister() {
		Set<IExtractor> extr = new LinkedHashSet<IExtractor>();
		extr.addAll(getMpeg7Reader().getExtractorRegister());
		extr.addAll(getDefaultReader().getExtractorRegister());
		return extr;
	}

	
	public Set<IExtractorVector> getExtractorRegister3D() {
		Set<IExtractorVector> extr = new LinkedHashSet<IExtractorVector>();
		extr.addAll(getMpeg7Reader().getExtractorRegister3D());
		extr.addAll(getDefaultReader().getExtractorRegister3D());
		return extr;
	}

	
	public void pushValues(Long sample) {
		getDefaultReader().pushValues(sample);
		getMpeg7Reader().pushValues(sample);

	}

	
	public void put(Long sample, float value) {
		getDefaultReader().put(sample, value);
		getMpeg7Reader().put(sample, value);
	}

	
	public void registerExtractor(IGeneralExtractor extractor) {
//		getReader().registerExtractor(extractor);
	}

	
	public void setConfig(IExtractorConfig config) {
		
		IExtractorConfig mp7Config = clone(config);
		IExtractorConfig spntConfig = clone(config);
		
		for (String extr : config.getExtractors()) {
			String[] extractor = extr.split(":");
			SupportableReaderEnum readerType = SupportableReaderEnum.valueOf(extractor[0]);
			switch (readerType) {
			case spantus:
				ExtractorEnum extractorType = ExtractorEnum.valueOf(extractor[1]);
				spntConfig.getExtractors().add(extractorType.name());
				registerTreasholdIfNeeded(extr, config.getParameters(), extractorType, spntConfig);
				spntConfig.getParameters().put(extractorType.name(), 
						config.getParameters().get(extr));
				break;
			case mpeg7:
				Mpeg7ExtractorEnum mpeg7ExtractorType =  Mpeg7ExtractorEnum.valueOf(extractor[1]);
				mp7Config.getExtractors().add(mpeg7ExtractorType.name());
				Mpeg7ExtractorUtils.register(getMpeg7Reader(), mpeg7ExtractorType);
				spntConfig.getParameters().put(mpeg7ExtractorType.name(), 
						config.getParameters().get(extr));
				break;
			default:
				throw new RuntimeException("not impl: " + readerType);
			}
		}
		mpeg7Reader.setConfig(mp7Config);
		defaultReader.setConfig(spntConfig);
	}
	

	/**
	 * 
	 * @param key
	 * @param params
	 * @param extractorType
	 * @param config
	 */
	protected void registerTreasholdIfNeeded(String key, 
			Map<String, ExtractorParam> params, 
			ExtractorEnum extractorType, 
			IExtractorConfig config){
		
		if(!ExtractorEnum.SIGNAL_EXTRACTOR.equals(extractorType) &&
				ExtractorTypeEnum.SequenceOfScalar.equals(extractorType.getType())){

			ExtractorParam param = ExtractorParamUtils.getSafeParam(params, 
					key);
			
			String tresholdType = ExtractorParamUtils.getString(param, 
					ExtractorParamUtils.commonParam.thresholdType.name(), 
					"");
			if(StringUtils.hasText(tresholdType)){
				ThresholdEnum thresholdEnum = ThresholdEnum.valueOf(tresholdType);
				IThreshold threshold = ExtractorUtils.registerThreshold(getDefaultReader(),
						extractorType,
						thresholdEnum);
				param = ExtractorParamUtils.getSafeParam(params, key);
				Float threasholdCoef = ExtractorParamUtils.<Float>getValue(param, 
						ExtractorParamUtils.commonParam.threasholdCoef.name(), Float.valueOf(1.1f));
				if(threshold instanceof AbstractThreshold){
					((AbstractThreshold)threshold).setCoef(threasholdCoef);	
				}
				
				return;
			}
		}
		ExtractorUtils.register(getDefaultReader(), extractorType);
	}
	
	
	
	public IExtractorConfig clone(IExtractorConfig config) {
		IExtractorConfig cloned = null;
		try {
			cloned = config.getClass().newInstance();
		} catch (InstantiationException e) {
			throw new ProcessingException(e);
		} catch (IllegalAccessException e) {
			throw new ProcessingException(e);
		}
		cloned.setFrameSize(config.getFrameSize());
		cloned.setBufferSize(config.getBufferSize());
		cloned.setWindowSize(config.getWindowSize());
		cloned.setWindowOverlap(config.getWindowOverlap());
		cloned.setSampleRate(config.getSampleRate());
		return cloned;
	}

	public IExtractorConfig getConfig() {
		return getDefaultReader().getConfig();
	}
}

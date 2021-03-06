package org.spantus.work.reader;

import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import org.spantus.core.FrameValues;

import org.spantus.core.extractor.*;
import org.spantus.core.threshold.AbstractThreshold;
import org.spantus.core.threshold.ClassifierEnum;
import org.spantus.core.threshold.IClassifier;
import org.spantus.extractor.ExtractorConfigUtil;
import org.spantus.extractor.impl.ExtractorEnum;
import org.spantus.extractor.impl.ExtractorTypeEnum;
import org.spantus.extractor.impl.ExtractorUtils;
import org.spantus.extractor.segments.online.ExtremeOnlineRuleClassifier;
import org.spantus.mpeg7.Mpeg7ExtractorEnum;
import org.spantus.mpeg7.Mpeg7ExtractorUtils;
import org.spantus.mpeg7.extractors.Mpeg7ExtractorInputReader;
import org.spantus.utils.ExtractorParamUtils;
import org.spantus.utils.StringUtils;
import org.spantus.work.services.WorkServiceFactory;

public class MultiFeatureExtractorInputReader implements IExtractorInputReader {
	

	DefaultExtractorInputReader defaultReader;
	Mpeg7ExtractorInputReader mpeg7Reader;
	
	public MultiFeatureExtractorInputReader() {
		defaultReader = new DefaultExtractorInputReader();
		mpeg7Reader = new Mpeg7ExtractorInputReader();
	}

	public DefaultExtractorInputReader getDefaultReader() {
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

        public Set<IGeneralExtractor<?>> getGeneralExtractor() {
            Set<IGeneralExtractor<?>> extr = new LinkedHashSet<IGeneralExtractor<?>>();
            extr.addAll(getMpeg7Reader().getGeneralExtractor());
            extr.addAll(getDefaultReader().getGeneralExtractor());
            return extr;
        }
        
        

	
	public void pushValues(Long sample) {
		getDefaultReader().pushValues(sample);
//		getMpeg7Reader().pushValues(sample);

	}

	
	public void put(Long sample, Double value) {
		getDefaultReader().put(sample, value);
//		getMpeg7Reader().put(sample, value);
	}

	
	public void registerExtractor(IGeneralExtractor<?> extractor) {
//		getReader().registerExtractor(extractor);
	}

	
	public void setConfig(IExtractorConfig config) {
		
		IExtractorConfig mp7Config = ExtractorConfigUtil.clone(config);
		IExtractorConfig spntConfig = ExtractorConfigUtil.clone(config);
		
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
	
	protected ClassifierEnum getThresholdEnum(String tresholdType){
		if(!StringUtils.hasText(tresholdType)) return null;
		try{
			return ClassifierEnum.valueOf(tresholdType);
		}catch (IllegalArgumentException e) {
			return null;
		}
		
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
		
		//threshold can not be applied to signal extractor and not sequen of scalar extractor
		if(!ExtractorEnum.SIGNAL_EXTRACTOR.equals(extractorType) &&
				ExtractorTypeEnum.SequenceOfScalar.equals(extractorType.getType())){

			ExtractorParam param = ExtractorParamUtils.getSafeParam(params, 
					key);
			
			String tresholdType = ExtractorParamUtils.getValue(param, 
					ExtractorParamUtils.commonParam.thresholdType.name(), 
					"");
			ClassifierEnum thresholdEnum = getThresholdEnum(tresholdType);
			if(thresholdEnum != null){
				//construct extractor with threshold
				IClassifier threshold = ExtractorUtils.registerThreshold(getDefaultReader(),
						extractorType,
						params.get(key),
						thresholdEnum);
				
				if(threshold instanceof ExtremeOnlineRuleClassifier){
					WorkServiceFactory.udpateClassifierRuleBaseService(
							(ExtremeOnlineRuleClassifier)threshold, null);
				}
					
				param = ExtractorParamUtils.getSafeParam(params, key);
				Number threasholdCoef = ExtractorParamUtils.<Double>getValue(param, 
						ExtractorParamUtils.commonParam.threasholdCoef.name(), 1.1D);
				if(threshold instanceof AbstractThreshold){
					((AbstractThreshold)threshold).setCoef(threasholdCoef.doubleValue());	
				}
				return;
			}
		}
		//construct regular extractor
		ExtractorUtils.register(getDefaultReader(), extractorType, params.get(params.get(extractorType.name())));
	}
	
	
	
	

	public IExtractorConfig getConfig() {
		return getDefaultReader().getConfig();
	}

    @Override
    public Long getAvailableStartMs() {
        return getDefaultReader().getAvailableStartMs();
    }

    @Override
    public Long getAvailableSignalLengthMs() {
        return getDefaultReader().getAvailableSignalLengthMs();
    }

    @Override
    public FrameValues findSignalValues(Long startMs, Long lengthMs) {
        return getDefaultReader().findSignalValues(startMs, lengthMs);
    }
}

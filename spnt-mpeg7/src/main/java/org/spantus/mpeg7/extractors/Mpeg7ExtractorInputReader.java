package org.spantus.mpeg7.extractors;

import java.util.HashSet;
import java.util.Set;
import org.spantus.core.FrameValues;

import org.spantus.core.extractor.IExtractor;
import org.spantus.core.extractor.IExtractorConfig;
import org.spantus.core.extractor.IExtractorInputReader;
import org.spantus.core.extractor.IExtractorVector;
import org.spantus.core.extractor.IGeneralExtractor;
import org.spantus.mpeg7.config.Mpeg7ExtractorConfig;

public class Mpeg7ExtractorInputReader implements IExtractorInputReader {

	Set<IExtractor> extractors = new HashSet<IExtractor>();
	Set<IExtractorVector> vectorExtractors = new HashSet<IExtractorVector>();
	IExtractorConfig config;

	
	public Set<IExtractor> getExtractorRegister() {
		return extractors;
	}

	
	public Set<IExtractorVector> getExtractorRegister3D() {
		return vectorExtractors;
	}

        public Set<IGeneralExtractor<?>> getGeneralExtractor() {
            Set<IGeneralExtractor<?>> generalSet = new HashSet<IGeneralExtractor<?>>();
            generalSet.addAll(getExtractorRegister());
            generalSet.addAll(getExtractorRegister3D());
            return generalSet;
        }

        
	
	public void pushValues(Long sample) {
		throw new RuntimeException("Should not be used");
	}

	
	public void put(Long sample, Double value) {
		throw new RuntimeException("Should not be used");
	}

	
	public void registerExtractor(IGeneralExtractor<?> extractor) {
	}
	
	public IExtractorConfig getConfig() {
		if(config == null){
			config = new Mpeg7ExtractorConfig();
		}
		return config;
	}
	
	
	public void setConfig(IExtractorConfig config) {
		this.config = config;
	}

    @Override
    public Long getAvailableStartMs() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Long getAvailableSignalLengthMs() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public FrameValues findSignalValues(Long l, Long l1) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}

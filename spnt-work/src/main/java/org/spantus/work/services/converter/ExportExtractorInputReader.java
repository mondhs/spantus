package org.spantus.work.services.converter;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.spantus.core.FrameValues;
import org.spantus.core.extractor.*;

import java.io.Serializable;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

public class ExportExtractorInputReader implements IExtractorInputReader, Serializable {
    private Set<IExtractor> extractors = new LinkedHashSet<>();
    private Set<IExtractorVector> extractorVectors = new LinkedHashSet<>();

    @JsonIgnore
    private Long availableStartMs;

    @JsonIgnore
    private Long availableSignalLengthMs;

    @JsonIgnore
    private IExtractorConfig config;

    @Override
    public void put(Long sample, Double value) {

    }

    @Override
    public void pushValues(Long sample) {

    }

    @Override
    public Set<IExtractor> getExtractorRegister() {
        return extractors;
    }

    @Override
    public Set<IExtractorVector> getExtractorRegister3D() {
        return extractorVectors;
    }

    @Override @JsonIgnore
    public Set<IGeneralExtractor<?>> getGeneralExtractor() {
        return null;
    }

    @Override
    public void registerExtractor(IGeneralExtractor<?> extractor) {
        if(extractor instanceof  IExtractor){
            extractors.add((IExtractor) extractor);
        }else if(extractor instanceof  IExtractorVector){
            extractorVectors.add((IExtractorVector) extractor);
        }

    }

    @Override
    public void setConfig(IExtractorConfig config) {
        this.config = config;
    }

    @Override
    public IExtractorConfig getConfig() {
        return config;
    }

    @Override
    public Long getAvailableStartMs() {
        return null;
    }

    @Override
    public Long getAvailableSignalLengthMs() {
        return null;
    }

    @Override
    public FrameValues findSignalValues(Long startMs, Long lengthMs) {
        return null;
    }
}

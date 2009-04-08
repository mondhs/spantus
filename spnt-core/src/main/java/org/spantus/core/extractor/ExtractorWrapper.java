package org.spantus.core.extractor;

import java.util.LinkedHashSet;
import java.util.Set;

import org.spantus.core.FrameValues;

public class ExtractorWrapper implements IExtractor {

	IExtractor extractor;
	Set<IExtractorListener> listeners;

	public ExtractorWrapper(IExtractor extractor) {
		this.extractor = extractor;
	}
	
	public FrameValues calculate(Long sample, FrameValues values) {

		for (IExtractorListener lstn1: getListeners()) {
			lstn1.beforeCalculated(sample, values);
		}
		FrameValues result = getExtractor().calculate(sample, values);
	
		for (IExtractorListener lstn1: getListeners()) {
			lstn1.afterCalculated(sample, result);
		}
		return result;
	}

	public FrameValues calculateWindow(FrameValues window) {
		FrameValues result = getExtractor().calculateWindow(window);
		return result;
	}

	public FrameValues getOutputValues() {
		return getExtractor().getOutputValues();
	}

	public IExtractorConfig getConfig() {
		return getExtractor().getConfig();
	}

	public float getExtractorSampleRate() {
		return getExtractor().getExtractorSampleRate();
	}

	public String getName() {
		return getExtractor().getName();
	}

	public void putValues(Long sample, FrameValues values) {
		getExtractor().putValues(sample, values);
	}

	public void setConfig(IExtractorConfig config) {
		getExtractor().setConfig(config);
	}
	public IExtractor getExtractor() {
		return extractor;
	}
	
	public Set<IExtractorListener> getListeners() {
		if(listeners == null){
			listeners = new LinkedHashSet<IExtractorListener>();
		}
		return listeners;
	}

	public void flush() {
		getExtractor().flush();
	}


}

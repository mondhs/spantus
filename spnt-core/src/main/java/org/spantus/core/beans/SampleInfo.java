package org.spantus.core.beans;

import java.util.LinkedHashSet;
import java.util.Set;

import org.spantus.core.extractor.IExtractorInputReader;
import org.spantus.core.marker.MarkerSetHolder;
import org.spantus.core.threshold.IClassifier;

public class SampleInfo {
	
	IExtractorInputReader reader;
	
	Set<IClassifier> thresholds;
	
	public Set<IClassifier> getThresholds() {
		if(thresholds == null){
			thresholds = new LinkedHashSet<IClassifier>();
		}
		return thresholds;
	}

	MarkerSetHolder markerSetHolder;

	public IExtractorInputReader getReader() {
		return reader;
	}

	public void setReader(IExtractorInputReader reader) {
		this.reader = reader;
	}

	public MarkerSetHolder getMarkerSetHolder() {
		return markerSetHolder;
	}

	public void setMarkerSetHolder(MarkerSetHolder markerSetHolder) {
		this.markerSetHolder = markerSetHolder;
	}
}

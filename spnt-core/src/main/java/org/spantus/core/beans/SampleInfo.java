package org.spantus.core.beans;

import java.util.LinkedHashSet;
import java.util.Set;

import org.spantus.core.extractor.IExtractorInputReader;
import org.spantus.core.marker.MarkerSetHolder;
import org.spantus.core.threshold.IThreshold;

public class SampleInfo {
	
	IExtractorInputReader reader;
	
	Set<IThreshold> thresholds;
	
	public Set<IThreshold> getThresholds() {
		if(thresholds == null){
			thresholds = new LinkedHashSet<IThreshold>();
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

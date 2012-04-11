package org.spantus.core.beans;

import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.annotation.XmlRootElement;

import org.spantus.core.FrameValues;
import org.spantus.core.FrameVectorValues;
import org.spantus.core.marker.Marker;

@XmlRootElement(name = "SignalSegment")
public class SignalSegment {

	private Marker marker;
	private Map<String, FrameValues> featureFrameValuesMap = new HashMap<String, FrameValues>();
	private Map<String, FrameVectorValues> featureFrameVectorValuesMap = new HashMap<String, FrameVectorValues>();

	public Marker getMarker() {
		return marker;
	}

	public void setMarker(Marker marker) {
		this.marker = marker;
	}

	public Map<String, FrameValues> getFeatureFrameValuesMap() {
		return featureFrameValuesMap;
	}

	public void setFeatureFrameValuesMap(
			Map<String, FrameValues> featureFrameValuesMap) {
		this.featureFrameValuesMap = featureFrameValuesMap;
	}

	public Map<String, FrameVectorValues> getFeatureFrameVectorValuesMap() {
		return featureFrameVectorValuesMap;
	}

	public void setFeatureFrameVectorValuesMap(
			Map<String, FrameVectorValues> featureFrameVectorValuesMap) {
		this.featureFrameVectorValuesMap = featureFrameVectorValuesMap;
	}

}

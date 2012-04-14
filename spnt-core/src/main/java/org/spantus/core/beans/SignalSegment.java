package org.spantus.core.beans;

import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.annotation.XmlRootElement;

import org.spantus.core.marker.Marker;

@XmlRootElement(name = "SignalSegment")
public class SignalSegment {

	private Marker marker;
	private Map<String, FrameValuesHolder> featureFrameValuesMap = new HashMap<String, FrameValuesHolder>();
	private Map<String, FrameVectorValuesHolder> featureFrameVectorValuesMap = new HashMap<String, FrameVectorValuesHolder>();

	public Marker getMarker() {
		return marker;
	}

	public void setMarker(Marker marker) {
		this.marker = marker;
	}

	public Map<String, FrameValuesHolder> getFeatureFrameValuesMap() {
		return featureFrameValuesMap;
	}

	public void setFeatureFrameValuesMap(
			Map<String, FrameValuesHolder> featureFrameValuesMap) {
		this.featureFrameValuesMap = featureFrameValuesMap;
	}

	public Map<String, FrameVectorValuesHolder> getFeatureFrameVectorValuesMap() {
		return featureFrameVectorValuesMap;
	}

	public void setFeatureFrameVectorValuesMap(
			Map<String, FrameVectorValuesHolder> featureFrameVectorValuesMap) {
		this.featureFrameVectorValuesMap = featureFrameVectorValuesMap;
	}

}

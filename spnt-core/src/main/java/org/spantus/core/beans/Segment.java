package org.spantus.core.beans;

import java.util.HashMap;
import java.util.Map;

import org.spantus.core.IValues;
import org.spantus.core.marker.Marker;

public class Segment {
	
	private String name;
	private Marker marker;
	private Map<String, IValues> values = new HashMap<String, IValues>();

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Map<String, IValues> getValues() {
		return values;
	}

	public void setValues(Map<String, IValues> values) {
		this.values = values;
	}

	public Marker getMarker() {
		return marker;
	}

	public void setMarker(Marker marker) {
		this.marker = marker;
	}
}

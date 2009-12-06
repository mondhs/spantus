package org.spantus.core.marker;

import java.util.LinkedList;
import java.util.List;

public class MarkerSet {
	
	String markerSetType;
	List<Marker> markers;

	public List<Marker> getMarkers() {
		if(markers == null){
			markers = new LinkedList<Marker>();
		}
		return markers;
	}

	public String getMarkerSetType() {
		return markerSetType;
	}

	public void setMarkerSetType(String markerSetType) {
		this.markerSetType = markerSetType;
	}
}

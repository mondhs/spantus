package org.spantus.core.marker.service;

import java.math.BigDecimal;

import org.spantus.core.marker.Marker;
import org.spantus.core.marker.MarkerSet;

public class MarkerServiceImp implements IMarkerService {

	public Marker addMarker(MarkerSet markerSet, BigDecimal start, BigDecimal length) {
		Marker marker = new Marker();
		marker.setStart(start);
		marker.setLength(length);
		markerSet.getMarkers().add(marker);
		return marker;
	}

	public boolean removeMarker(MarkerSet markerSet, Marker marker) {
		boolean removed = markerSet.getMarkers().remove(marker);
		return removed;
	}

	public boolean validate(MarkerSet markerSet, Marker marker,
			BigDecimal newStart, BigDecimal newLength) {
		// TODO Auto-generated method stub
		return false;
	}

}

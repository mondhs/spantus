package org.spantus.core.marker.service;

import java.math.BigDecimal;

import org.spantus.core.marker.Marker;
import org.spantus.core.marker.MarkerSet;

public interface IMarkerService {
	public Marker addMarker(MarkerSet markerSet, BigDecimal start, BigDecimal length);
	public boolean removeMarker(MarkerSet markerSet, Marker marker);
	public boolean validate(MarkerSet markerSet, Marker marker, BigDecimal newStart, BigDecimal newLength);

}

package org.spantus.core.marker.service;

import org.spantus.core.marker.Marker;
import org.spantus.core.marker.MarkerSet;

public interface IMarkerService {
	public Marker addMarker(MarkerSet markerSet, Long start, Long length);
	public boolean removeMarker(MarkerSet markerSet, Marker marker);
	public boolean validate(MarkerSet markerSet, Marker marker, Long newStart, Long newLength);
	public Long getTime(int sampleNum, Float sampleRate);

}

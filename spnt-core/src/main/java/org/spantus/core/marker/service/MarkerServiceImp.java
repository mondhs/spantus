/*
 	Copyright (c) 2009 Mindaugas Greibus (spantus@gmail.com)
 	Part of program for analyze speech signal 
 	http://spantus.sourceforge.net

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>
*/
package org.spantus.core.marker.service;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.spantus.core.marker.Marker;
import org.spantus.core.marker.MarkerSet;

public class MarkerServiceImp implements IMarkerService {

	public Marker addMarker(MarkerSet markerSet, Long start, Long length) {
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
			Long newStart, Long newLength) {
		return true;
	}
	
	public Long getTime(int sampleNum, Float sampleRate) {
		return BigDecimal.valueOf((sampleNum * 1000) / sampleRate).setScale(0,
				RoundingMode.HALF_UP).longValue();
	}

}

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

import java.util.Collection;

import org.spantus.core.marker.Marker;
import org.spantus.core.marker.MarkerSet;
import org.spantus.core.marker.MarkerSetHolder;

public interface IMarkerService {
	public Marker addMarker(MarkerSet markerSet, Long start, Long length);
	public boolean removeMarker(MarkerSet markerSet, Marker marker);
	public boolean validate(MarkerSet markerSet, Marker marker, Long newStart, Long newLength);
	public Long getTime(int sampleNum, Double sampleRate);
	public Marker findFirstByLabel(MarkerSetHolder markerSetHolder,String label);
	public Collection<Marker> findAllSequenesByLabels(MarkerSetHolder markerSetHolder,String label1,String label2);
	public Marker findFirstByPhrase(MarkerSetHolder markerSetHolder, String... labels);
	public Collection<Marker> findAllByLabel(MarkerSetHolder markerSetHolder,String label);
	public Collection<Marker> findAllByPhrase(MarkerSetHolder markerSetHolder, String... labels);

}

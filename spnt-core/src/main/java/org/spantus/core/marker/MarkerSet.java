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
package org.spantus.core.marker;

import java.io.Serializable;
import java.text.MessageFormat;
import java.util.LinkedList;
import java.util.List;

import org.spantus.core.marker.MarkerSetHolder.MarkerSetHolderEnum;
/**
 * Marker collection holder with additional information. MarkerSet represents one level of segmentation process
 * 
 *  @see Marker
 *  @see MarkerSetHolderEnum
 * 
 * @author mondhs
 *
 */
public class MarkerSet implements Serializable,Cloneable {
	
	private static final long serialVersionUID = 1L;

	private String markerSetType;
	
	private List<Marker> markers;
	/**
	 * Marker collection
	 * @return
	 */
	public List<Marker> getMarkers() {
		if(markers == null){
			markers = new LinkedList<Marker>();
		}
		return markers;
	}
	public void setMarkerSetType(String markerSetType) {
		this.markerSetType = markerSetType;
	}

	/** 
	 * 
	 * @see MarkerSetHolderEnum
	 * 
	 * @return type of this marker set.
	 */
	public String getMarkerSetType() {
		return markerSetType;
	}

	public void setMarkers(List<Marker> markers) {
		this.markers = markers;
	}

	public MarkerSet clone(){
		try {
			MarkerSet msCloned =  (MarkerSet)super.clone();
			msCloned.setMarkers(new LinkedList<Marker>());
			for (Marker marker : this.getMarkers()) {
				msCloned.getMarkers().add(marker.clone());
			}
			return msCloned;
		} catch (CloneNotSupportedException e) {
			throw new IllegalArgumentException(e);
		}
	}
	
	@Override
	public String toString() {
		int size = 0;
		size = getMarkers() == null?0:getMarkers().size();
		String str = MessageFormat.format("{0}: {1} markers: {2};", 
				getClass().getSimpleName(), getMarkerSetType(), size);
		return str;
	}

}

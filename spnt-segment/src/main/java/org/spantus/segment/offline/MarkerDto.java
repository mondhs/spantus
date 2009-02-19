/**
 * Part of program for analyze speech signal 
 * Copyright (c) 2008 Mindaugas Greibus (spantus@gmail.com)
 * http://code.google.com/p/spantus/
 * 
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the
 * Free Software Foundation; either version 2 of the License, or (at your
 * option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 675 Mass Ave, Cambridge, MA 02139, USA.
 * 
 */
package org.spantus.segment.offline;

import org.spantus.core.marker.Marker;

public class MarkerDto {
	
	private Marker marker;
	
	private MarkerDto next;
	
	private MarkerDto previous;

	public Marker getMarker() {
		return marker;
	}

	public void setMarker(Marker marker) {
		this.marker = marker;
	}

	public MarkerDto getNext() {
		return next;
	}

	public void setNext(MarkerDto next) {
		this.next = next;
	}

	public MarkerDto getPrevious() {
		return previous;
	}

	public void setPrevious(MarkerDto previous) {
		this.previous = previous;
	}
	
	public Long getDistanceToNext(){
		if(getNext() == null || getMarker() == null){
			return 0L;
		}
		Long distanceToNext = getMarker().getStart()+
				getMarker().getLength()-getNext().getMarker().getStart();

		return Math.abs(distanceToNext);
	}

	public Long getDistanceToPrevious(){
		if(getPrevious() == null || getMarker() == null){
			return 0L;
		}
		Long distanceToPrevious = getPrevious().getMarker().getStart()+
				getPrevious().getMarker().getLength()-getMarker().getStart();

		return Math.abs(distanceToPrevious);

	}

	@Override
	public String toString() {
		String nextName = getNext() == null?"null":getNext().getMarker().getLabel();
		String previousName = getPrevious() == null? "null":getPrevious().getMarker().getLabel();
		return getClass().getName()+"[" + previousName+ "->" +
			getMarker()+ "->" + nextName + "]";
	}
}

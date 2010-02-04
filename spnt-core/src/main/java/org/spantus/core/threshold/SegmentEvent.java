/*
 	Copyright (c) 2009, 2010 Mindaugas Greibus (spantus@gmail.com)
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
package org.spantus.core.threshold;

import java.io.Serializable;
import java.text.MessageFormat;

import org.spantus.core.marker.Marker;
/**
 * 
 * @author Mindaugas Greibus
 * 
 * Created Feb 3, 2010
 *
 */
public class SegmentEvent implements Serializable, Cloneable{
	
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String id;
	private Long time;
	private Long sample;
	private Marker marker;
	public String getId() {
		return id;
	}
	/**
	 * 
	 */
	public SegmentEvent() {}
	/**
	 * 
	 * @param id
	 * @param time
	 * @param sample
	 * @param marker
	 */
	public SegmentEvent(String id, Long time, Marker marker, Long sample) {
		super();
		this.id = id;
		this.time = time;
		this.sample = sample;
		this.marker = marker;
	}
	
	
	public void setId(String id) {
		this.id = id;
	}
	public Long getTime() {
		return time;
	}
	public void setTime(Long time) {
		this.time = time;
	}
	public Long getSample() {
		return sample;
	}
	public void setSample(Long sample) {
		this.sample = sample;
	}
	public Marker getMarker() {
		return marker;
	}
	public void setMarker(Marker marker) {
		this.marker = marker;
	}
	
	@Override
	public String toString() {
		return MessageFormat.format("{0} [id:{1}; time{2}]", SegmentEvent.class.getSimpleName(), getId(), getTime());
	}
	
	public SegmentEvent clone(){
		try {
			return (SegmentEvent)super.clone();
		} catch (CloneNotSupportedException e) {
			throw new IllegalArgumentException(e);
		}
	}
}

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

import org.spantus.core.FrameValues;
import org.spantus.core.IValues;
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
	private String extractorId;
	private Long time;
	private Long sample;
	private Marker marker;
	private Double value;
	private IValues outputValues;
	private FrameValues windowValues;
	private boolean signalState;
	
	public String getExtractorId() {
		return extractorId;
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
	public SegmentEvent(String extractorId, Long time, Marker marker, Long sample,Double value, boolean signalState) {
		super();
		this.extractorId = extractorId;
		this.time = time;
		this.sample = sample;
		this.marker = marker;
		this.value = value;
		this.signalState=signalState;
	}
	
	
	public void setExtractorId(String id) {
		this.extractorId = id;
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
		return MessageFormat.format("{0} [id:{1}; time:{2}]", SegmentEvent.class.getSimpleName(), getExtractorId(), getTime());
	}
	
	public SegmentEvent clone(){
		try {
			return (SegmentEvent)super.clone();
		} catch (CloneNotSupportedException e) {
			throw new IllegalArgumentException(e);
		}
	}

	public Double getValue() {
		return value;
	}

	public void setValue(Double value) {
		this.value = value;
	}
	public IValues getOutputValues() {
		return outputValues;
	}
	public void setOutputValues(IValues iValues) {
		this.outputValues = iValues;
	}
	public FrameValues getWindowValues() {
		return windowValues;
	}
	public void setWindowValues(FrameValues windowValues) {
		this.windowValues = windowValues;
	}
	public boolean getSignalState() {
		return signalState;
	}
	public void setSignalState(boolean signalState) {
		this.signalState = signalState;
	}
}

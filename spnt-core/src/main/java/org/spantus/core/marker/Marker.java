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

import org.spantus.utils.Assert;
/**
 * Marker represent segment information in segmentation process
 * 
 * @author Mindaugas Greibus
 *
 */
public class Marker implements Serializable, Cloneable{

	private static final long serialVersionUID = 1L;

	private Long start;
	
	private Long length;

	private String label;
	

	public Long getStart() {
		return start;
	}

	public Long getLength() {
		return length;
	}

	public void setEnd(Long end) {
		Assert.isTrue(getStart()!=null, "start not set");
		Assert.isTrue(end-getStart()>0, "End should be after start");
		setLength(end-getStart());
	}
	
	public Long getEnd(){
		Assert.isTrue(getStart()!=null, "start not set");
		Assert.isTrue(getLength()!=null, "length not set");
		return getStart()+getLength();
	}

	public void setStart(Long start) {
		this.start = start;
	}

	public void setLength(Long length) {
		Assert.isTrue(length>0, "Length cannot be negative");
		this.length = length;
	}
	
	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}
	@Override
	public String toString() {
		long saveStart = getStart() == null?0L:getStart();
		long saveLength = getLength() == null?0L:getLength();
		String str = MessageFormat.format("{0}: {1} [{2}; {3}]", 
				getClass().getSimpleName(), getLabel(),
				saveStart, (saveStart+saveLength));
		return str;
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj == null) return false;
		boolean val = this.hashCode() == obj.hashCode();
		return val;
	}

	public Marker clone(){
		try {
			return (Marker)super.clone();
		} catch (CloneNotSupportedException e) {
			throw new IllegalArgumentException(e);
		}
	}
	
//	public MarkerExtractionData getExtractionData() {
//		if(extractionData == null){
//			extractionData = new MarkerExtractionData();
//		}
//		return extractionData;
//	}

//	public void setExtractionData(MarkerExtractionData extractionData) {
//		this.extractionData = extractionData;
//	}

	
}

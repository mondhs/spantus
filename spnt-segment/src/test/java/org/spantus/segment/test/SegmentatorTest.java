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
package org.spantus.segment.test;

import junit.framework.TestCase;

import org.spantus.core.marker.Marker;
import org.spantus.core.marker.MarkerSet;
import org.spantus.core.marker.MarkerSetHolder;
import org.spantus.core.marker.MarkerSetHolder.MarkerSetHolderEnum;
import org.spantus.core.threshold.IClassifier;

public abstract class SegmentatorTest extends TestCase {
	/**
	 * 
	 * @param markers
	 * @return
	 */
	public IClassifier contsructClassifier(Integer[][] markers){
		MockClassifier classifier = new MockClassifier();
//		FrameValues states = new FrameValues(statesF);
//		states.setSampleRate(100f);
		classifier.setExtractorSampleRate(20f);
		classifier.setMarkSet(createMarkerSet(markers));
		return classifier;
	}
	/**
	 * 
	 * @param markers
	 * @return
	 */
	public MarkerSet createMarkerSet(Integer[][] markers){
		MarkerSet markerSet = new MarkerSet();
		markerSet.setMarkerSetType(MarkerSetHolderEnum.phone.name());
		for (Integer[] markerIntegers : markers) {
			Marker marker = createMarker(
						markerIntegers[0],
						markerIntegers[1]);
			markerSet.getMarkers().add(marker);
		}
		return markerSet;
	}
	/**
	 * 
	 * @param message
	 * @param n1
	 * @param n2
	 */
	protected void assertEqualsLong(String message, Number n1, Number n2){
		assertEquals(message,n1.longValue(), n2.longValue());
	}
	
	protected void assertEqualsMarkers(String message, Integer[][] expexted, MarkerSetHolder result){
		MarkerSet markerSet = result.getMarkerSets().get(MarkerSetHolderEnum.word.name());
		assertEquals(expexted.length, markerSet.getMarkers().size());
		int i=0;
		for (Integer[] integers : expexted) {
			assertEqualsLong((i+1) + " marker start", integers[0], markerSet.getMarkers().get(i).getStart());
			assertEqualsLong((i+1) + " marker end", integers[1], markerSet.getMarkers().get(i).getEnd());
			i++;
		}
	}
	
	/**
	 * 
	 * @param start
	 * @param end
	 * @return
	 */
	protected Marker createMarker(Integer start, Integer end){
		Marker marker = new Marker();
		marker.setStart(start.longValue());
		marker.setEnd(end.longValue());
		return marker;
	}

}

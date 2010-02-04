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
package org.spantus.segment.online.test;

import java.util.ArrayList;
import java.util.List;

import org.spantus.core.marker.Marker;
import org.spantus.core.marker.MarkerSet;
import org.spantus.logger.Logger;
/**
 * 
 * 
 * @author Mindaugas Greibus
 *
 * @since 0.0.1
 * 
 *
 */
public class OnlineSegmentatorMarkerTest extends AbstractOnlineSegmentTest {
	
	Logger log = Logger.getLogger(getClass());
	public static final int step = 10;
	public static final float sampleRate = 100F;
	
	

	public void testOnline(){
		MarkerSet current = createMarkerSet();
		MarkerSet calculated = segmentRuleBase(transfor(current), step, sampleRate);
		assertEquals(4, calculated.getMarkers().size());
		
	}

	public Float[] transfor(MarkerSet markerSet){
		List<Float> floats1 = new ArrayList<Float>();
		long current = 0;
		for (Marker m : markerSet.getMarkers()) {
			for (; current < m.getStart(); current+=step) {
				floats1.add(0F);
			}
			Long end = m.getStart() + m.getLength();
			for (; current < end; current+=step) {
				floats1.add(1F);
			}
			
		}
		return floats1.toArray(new Float[0]);
	}
	
	
	/**
	 * 
	 * @return
	 */
	public MarkerSet createMarkerSet(){
		MarkerSet markerSet = new MarkerSet();
		markerSet.getMarkers().add(createMarker(499, 458));
		markerSet.getMarkers().add(createMarker(1517, 417));
		markerSet.getMarkers().add(createMarker(2474, 163));
		markerSet.getMarkers().add(createMarker(2657, 245));
		markerSet.getMarkers().add(createMarker(3278, 581));
		return markerSet;
	}
	/**
	 * 
	 * @return
	 */
	public Marker createMarker(int start, int length){
		Marker marker = new Marker();
		marker.setStart(Long.valueOf(start));
		marker.setLength(Long.valueOf(length));
		marker.setLabel(""+marker.getStart());
		return marker;
	}
	
	
	
}

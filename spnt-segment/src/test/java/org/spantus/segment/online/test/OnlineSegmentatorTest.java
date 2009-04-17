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
package org.spantus.segment.online.test;

import org.spantus.core.marker.Marker;
import org.spantus.core.marker.MarkerSet;
import org.spantus.logger.Logger;
import org.spantus.segment.online.MultipleSegmentatorOnline;
import org.spantus.segment.online.ThresholdSegmentatorOnline;
/**
 * 
 * 
 * @author Mindaugas Greibus
 *
 * @since 0.0.1
 * 
 * Created 2008.11.27
 *
 */
public class OnlineSegmentatorTest extends AbstractOnlineSegmentTest {
	
	Logger log = Logger.getLogger(getClass());
	
	public static float[] SEGMENT1_VALS = new float[]{.5f, 0f, 0f, 1f, 1f, 0f, 0f, 0f, 0f, 1f, 1f, 1f, 0f};
	public static float[] SEGMENT2_VALS = new float[]{.5f, 0f, 0f, 0f, 1f, 1f, 0f, 0f, 0f, 1f, 1f, 1f, 0f};
	public static float[] SEGMENT3_VALS = new float[]{.5f, 0f, 1f, 0f, 1f, 1f, 0f, 0f, 0f, 0f, 1f, 1f, 0f};
	

	public void testOnline(){
		MultipleSegmentatorOnline multipe = new MultipleSegmentatorOnline();
		
		ThresholdSegmentatorOnline segmentator1 = getSegmentator("extractor1", multipe);
		ThresholdSegmentatorOnline segmentator2 = getSegmentator("extractor2", multipe);
		ThresholdSegmentatorOnline segmentator3 = getSegmentator("extractor3", multipe);
		
		for (int i = 0; i < SEGMENT1_VALS.length; i++) {
			float f1 = SEGMENT1_VALS[i];
			float f2 = SEGMENT2_VALS[i];
			float f3 = SEGMENT3_VALS[i];
			Long l = Long.valueOf(i);
			segmentator1.calculate(l, getWindow(f1));
			segmentator2.calculate(l, getWindow(f2));
			segmentator3.calculate(l, getWindow(f3));
		}
		log.debug("Markers: " + multipe.getMarkSet().getMarkers());
		assertEquals(2, multipe.getMarkSet().getMarkers().size());
		Marker m = multipe.getMarkSet().getMarkers().get(0);
		assertEquals(4000, m.getStart().intValue());
		assertEquals(2000, m.getLength().intValue());
		m = multipe.getMarkSet().getMarkers().get(1);
		assertEquals(9000, m.getStart().intValue());
		assertEquals(3000, m.getLength().intValue());
	}

	public void testOnlineRuleSimple(){
		MarkerSet markSet = 
			segmentRuleBase(
				new Float[]{.5f, 0f, 0f, 1f, 1f, 1f, 1f, 0f, 0f, 0f, 1f, 1f, 1f, 0f, 0f});
		
		log.debug("Markers: " + markSet.getMarkers());
		assertEquals(2, markSet.getMarkers().size());
		Marker m = markSet.getMarkers().get(0);
		assertEquals(3000, m.getStart().intValue());
		assertEquals(4000, m.getLength().intValue());
		m = markSet.getMarkers().get(1);
		assertEquals(10000, m.getStart().intValue());
		assertEquals(3000, m.getLength().intValue());
	}
	
	public void testOnlineRuleDelete(){
		MarkerSet markSet = 
			segmentRuleBase(
				new Float[]{.5f, 0f, 0f, 1f, 1f, 1f, 1f, 0f, 0f, 0f, 1f, 0f, 0f, 1f, 0f});
		
		log.debug("Markers: " + markSet.getMarkers());
		assertEquals(1, markSet.getMarkers().size());
		Marker m = markSet.getMarkers().get(0);
		assertEquals(3000, m.getStart().intValue());
		assertEquals(4000, m.getLength().intValue());
	}
	
	public void testOnlineRuleJoin(){
		MarkerSet markSet = 
			segmentRuleBase(
					new Float[]{.5f, 0f, 0f, 1f, 0f, 1f, 1f, 0f, 1f, 0f, 0f });
		
		log.debug("Markers: " + markSet.getMarkers());
		assertEquals(1, markSet.getMarkers().size());
		Marker m = markSet.getMarkers().get(0);
		assertEquals(3000, m.getStart().intValue());
		assertEquals(6000, m.getLength().intValue());
	
	}
	
	
}

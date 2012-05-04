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

import org.spantus.core.marker.Marker;
import org.spantus.core.marker.MarkerSet;
import org.spantus.core.threshold.IClassifier;
import org.spantus.logger.Logger;
import org.spantus.segment.online.MultipleSegmentatorListenerOnline;
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
	
	public static Double[] SEGMENT1_VALS = new Double[]{.5D, 0D, 0D, 1D, 1D, 0D, 0D, 0D, 0D, 1D, 1D, 1D, 0D};
	public static Double[] SEGMENT2_VALS = new Double[]{.5D, 0D, 0D, 0D, 1D, 1D, 0D, 0D, 0D, 1D, 1D, 1D, 0D};
	public static Double[] SEGMENT3_VALS = new Double[]{.5D, 0D, 1D, 0D, 1D, 1D, 0D, 0D, 0D, 0D, 1D, 1D, 0D};
	/**
	 * 
	 */
	public void testOnline(){
		MultipleSegmentatorListenerOnline multipeListener = new MultipleSegmentatorListenerOnline();
		
		IClassifier segmentator1 = getSegmentator("extractor1", multipeListener);
		IClassifier segmentator2 = getSegmentator("extractor2", multipeListener);
		IClassifier segmentator3 = getSegmentator("extractor3", multipeListener);
		
		for (int i = 0; i < SEGMENT1_VALS.length; i++) {
			Double f1 = SEGMENT1_VALS[i];
			Double f2 = SEGMENT2_VALS[i];
			Double f3 = SEGMENT3_VALS[i];
			Long l = Long.valueOf(i);
			segmentator1.calculateWindow(l, getWindow(f1));
			segmentator2.calculateWindow(l, getWindow(f2));
			segmentator3.calculateWindow(l, getWindow(f3));
		}
		assertNotNull(multipeListener.getMarkSet());
		log.debug("Markers: " + multipeListener.getMarkSet().getMarkers());
		assertEquals(2, multipeListener.getMarkSet().getMarkers().size());
		Marker m = multipeListener.getMarkSet().getMarkers().get(0);
		assertEquals(4000, m.getStart().intValue());
		assertEquals(2000, m.getLength().intValue());
		m = multipeListener.getMarkSet().getMarkers().get(1);
		assertEquals(9000, m.getStart().intValue());
		assertEquals(3000, m.getLength().intValue());
		
		for (int i = 0; i < SEGMENT1_VALS.length; i++) {
			Double f2 = SEGMENT2_VALS[i];
			Double f3 = SEGMENT3_VALS[i];
			Long l = Long.valueOf(i);
			segmentator2.calculateWindow(l, getWindow(f2));
			segmentator3.calculateWindow(l, getWindow(f3));
		}
	}
	/**
	 * 
	 */
	public void testOnline2Segements(){
		MultipleSegmentatorListenerOnline multipeListener = new MultipleSegmentatorListenerOnline();
		
		IClassifier segmentator2 = getSegmentator("extractor2", multipeListener);
		IClassifier segmentator3 = getSegmentator("extractor3", multipeListener);
		
		for (int i = 0; i < SEGMENT1_VALS.length; i++) {
			Double f2 = SEGMENT2_VALS[i];
			Double f3 = SEGMENT3_VALS[i];
			Long l = Long.valueOf(i);
			segmentator2.calculateWindow(l, getWindow(f2));
			segmentator3.calculateWindow(l, getWindow(f3));
		}
		assertNotNull(multipeListener.getMarkSet());
		log.debug("Markers: " + multipeListener.getMarkSet().getMarkers());
		assertEquals(2, multipeListener.getMarkSet().getMarkers().size());
		Marker m = multipeListener.getMarkSet().getMarkers().get(0);
		assertEquals(4000, m.getStart().intValue());
		assertEquals(2000, m.getLength().intValue());
		m = multipeListener.getMarkSet().getMarkers().get(1);
		assertEquals(10000, m.getStart().intValue());
		assertEquals(2000, m.getLength().intValue());
		
		for (int i = 0; i < SEGMENT1_VALS.length; i++) {
			Double f2 = SEGMENT2_VALS[i];
			Double f3 = SEGMENT3_VALS[i];
			Long l = Long.valueOf(i);
			segmentator2.calculateWindow(l, getWindow(f2));
			segmentator3.calculateWindow(l, getWindow(f3));
		}
	}
	/**
	 * 
	 */
	public void testOnlineRuleSimple(){
		MarkerSet markSet = 
			segmentRuleBase(
				new Double[]{.5D, 0D, 0D, 1D, 1D, 1D, 1D, 0D, 0D, 0D, 1D, 1D, 1D, 0D, 0D});
		assertNotNull(markSet);
		
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
				new Double[]{.5D, 0D, 0D, 1D, 1D, 1D, 1D, 0D, 0D, 0D, 1D, 0D, 0D, 1D, 0D});
		assertNotNull(markSet);
		log.debug("Markers: " + markSet.getMarkers());
		assertEquals(1, markSet.getMarkers().size());
		Marker m = markSet.getMarkers().get(0);
		assertEquals(3000, m.getStart().intValue());
		assertEquals(4000, m.getLength().intValue());
	}
	
	public void testOnlineRuleJoin(){
		MarkerSet markSet = 
			segmentRuleBase(
					new Double[]{.5D, 0D, 0D, 1D, 1D, 1D, 0D, 1D, 1D, 0D, 1D, 0D, 0D });
		assertNotNull(markSet);
		log.debug("Markers: " + markSet.getMarkers());
		assertEquals(1, markSet.getMarkers().size());
		Marker m = markSet.getMarkers().get(0);
		assertEquals(3000, m.getStart().intValue());
		assertEquals(8000, m.getLength().intValue());
	
	}
	
	
}

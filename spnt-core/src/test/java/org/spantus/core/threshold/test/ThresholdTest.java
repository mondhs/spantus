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
package org.spantus.core.threshold.test;

import junit.framework.TestCase;

import org.spantus.core.FrameValues;
import org.spantus.core.extractor.ExtractorWrapper;
import org.spantus.core.marker.Marker;
import org.spantus.core.marker.MarkerSet;
import org.spantus.core.threshold.StaticThreshold;

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
public class ThresholdTest extends TestCase {
	
	Double[] values = new Double[]{2D, 4D, 5D, 2D,2D, 4D, 5D, 2D};
	Double[] expectedThreshold = new Double[]{2d, 3d, 3d, 3d, 3d, 3d, 3d, 3d};
	
	
	public void testThreshold(){
		StaticThreshold threshold = new StaticThreshold();
		threshold.setCoef(0D);
		MockExtractor mockExtractor= new MockExtractor();
		mockExtractor.setExtractorSampleRate(1D);
		threshold.setLearningPeriod(1000L);
		ExtractorWrapper wraper = new ExtractorWrapper(mockExtractor);
		threshold.setExtractor(wraper);
		wraper.getListeners().add(threshold);
		threshold.setConfig(new MockExtractorConfig());
		
		for (long i = 0; i < values.length; i++) {
			Double f1 = values[(int)i];
			FrameValues fv = new FrameValues(getWindow(threshold, f1));
			wraper.calculate(i, fv);			
		}
		int j = 0;
		Double avg = 0D;
		for (Double fv1 : threshold.getThresholdValues()) {
			assertEquals(j + " element", fv1,expectedThreshold[j++]);
			avg += fv1;
		}
		assertEquals("Average", 23.0, avg);
		assertEquals("segments", 2, threshold.getMarkSet().getMarkers().size());
		Marker marker = threshold.getMarkSet().getMarkers().get(0);
		assertEquals("start 1 segment", 1000L, marker.getStart().longValue());
		assertEquals("length 1 segment", 2000L, marker.getLength().longValue());
		marker = threshold.getMarkSet().getMarkers().get(1);
		assertEquals("start 2 segment", 5000L, marker.getStart().longValue());
		assertEquals("length 2 segment", 2000L, marker.getLength().longValue());

	}
	/**
	 * @throws CloneNotSupportedException 
	 * 
	 */
	public void testClone(){
		Marker m = new Marker();
		m.setStart(1L);
		m.setLength(11L);
		m.setLabel("111");
		Marker clone = (Marker)m.clone();
		assertEquals(m.getStart(), clone.getStart());
		
		MarkerSet ms = new MarkerSet();
		ms.getMarkers().add(m);
		ms.getMarkers().add(clone);
		MarkerSet msClone = ms.clone();
		assertEquals(ms.getMarkers().size(), msClone.getMarkers().size());
		msClone.getMarkers().clear();
		assertTrue("sizes should be not impacted", ms.getMarkers().size()!= 0);
		
	}
	
	public Double[] getWindow(StaticThreshold threshold, Double windowIndex){
		return new Double[]{windowIndex, windowIndex};
	}
	
}

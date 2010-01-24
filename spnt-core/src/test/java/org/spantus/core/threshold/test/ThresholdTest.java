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
	
	Float[] values = new Float[]{2f, 4f, 5f, 2f,2f, 4f, 5f, 2f};
	Float[] expectedThreshold = new Float[]{2f, 3f, 3f, 3f, 3f, 3f, 3f, 3f};
	
	
	public void testThreshold(){
		
		StaticThreshold threshold = new StaticThreshold();
		threshold.setCoef(1f);
		MockClassifier mockExtractor= new MockClassifier();
		mockExtractor.setExtractorSampleRate(1);
		threshold.setLearningPeriod(1000L);
		ExtractorWrapper wraper = new ExtractorWrapper(mockExtractor);
		threshold.setExtractor(wraper);
		wraper.getListeners().add(threshold);
		threshold.setConfig(new MockExtractorConfig());
		
		for (long i = 0; i < values.length; i++) {
			Float f1 = values[(int)i];
			FrameValues fv = new FrameValues(getWindow(threshold, f1));
			wraper.calculate(i, fv);			
		}
		int j = 0;
		float avg = 0;
		for (Float fv1 : threshold.getThresholdValues()) {
			assertEquals(j + " element", fv1,expectedThreshold[j++]);
			avg += fv1;
		}
		assertEquals("segments", 2, threshold.getMarkSet().getMarkers().size());
		Marker marker = threshold.getMarkSet().getMarkers().get(0);
		assertEquals("start 1 segment", 1000L, marker.getStart().longValue());
		assertEquals("length 1 segment", 2000L, marker.getLength().longValue());
		marker = threshold.getMarkSet().getMarkers().get(1);
		assertEquals("start 2 segment", 5000L, marker.getStart().longValue());
		assertEquals("length 2 segment", 2000L, marker.getLength().longValue());

	}
	public Float[] getWindow(StaticThreshold threshold, float windowIndex){
		return new Float[]{windowIndex, windowIndex};
	}
	
}

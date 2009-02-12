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
package org.spantus.core.threshold.test;

import junit.framework.TestCase;

import org.spantus.core.FrameValues;
import org.spantus.core.extractor.ExtractorWrapper;
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
	
	Float[] values = new Float[]{2f, 4f, 5f, 2f};
	Float[] expectedThreshold = new Float[]{2f, 3f, 3f, 3f};
	Float[] expectedState = new Float[]{0f, 1f, 1f, 0f};
	
	
	public void testThreshold(){
		
		StaticThreshold threshold = new StaticThreshold();
		threshold.setCoef(1f);
		MockExtractor mockExtractor= new MockExtractor();
		mockExtractor.setExtractorSampleRate(1);
		threshold.setLearningPeriod(1000f);
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
			assertEquals(fv1,expectedThreshold[j++]);
			avg += fv1;
		}
		j=0;
		for (Float fv1 : threshold.getState()) {
			assertEquals(fv1,expectedState[j++]);
		}
	}
	public Float[] getWindow(StaticThreshold threshold, float windowIndex){
		return new Float[]{windowIndex, windowIndex};
	}
	
}

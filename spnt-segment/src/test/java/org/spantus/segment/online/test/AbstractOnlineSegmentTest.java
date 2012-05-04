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

import junit.framework.TestCase;

import org.spantus.core.FrameValues;
import org.spantus.core.extractor.ExtractorWrapper;
import org.spantus.core.marker.MarkerSet;
import org.spantus.core.threshold.IClassifier;
import org.spantus.core.threshold.StaticThreshold;
import org.spantus.segment.online.DecisionSegmentatorOnline;
import org.spantus.segment.online.MultipleSegmentatorListenerOnline;
import org.spantus.segment.online.OnlineDecisionSegmentatorParam;
/**
 * 
 * @author Mindaugas Greibus
 *
 */
public abstract class AbstractOnlineSegmentTest extends TestCase {
	protected MarkerSet segmentRuleBase(Double[] vals) {
		return segmentRuleBase(vals, 1, 1D);
	}

	protected MarkerSet segmentRuleBase(Double[] vals, int step, Double sampleRate) {
		DecisionSegmentatorOnline multipeListener = new DecisionSegmentatorOnline();
		
		IClassifier segmentator1 = getSegmentator("segmentator1", multipeListener);
		IClassifier segmentator2 = getSegmentator("segmentator2", multipeListener);
		
		multipeListener.setParam(createParam());
		for (int i = 0; i < vals.length; i++) {
			Double f = vals[i];
			Long l = Long.valueOf(i * step);
			segmentator1.calculateWindow(l, getWindow(f, sampleRate));
			segmentator2.calculateWindow(l, getWindow(f, sampleRate));
		}
		return multipeListener.getMarkSet();
	}

	protected OnlineDecisionSegmentatorParam createParam() {
		OnlineDecisionSegmentatorParam param = new OnlineDecisionSegmentatorParam();
		param.setMinSpace(999L);
		param.setMinLength(1999L);
		return param;
	}

	public IClassifier getSegmentator(String name,
			MultipleSegmentatorListenerOnline multipeListener) {
		StaticThreshold segmentator1 = new StaticThreshold();
		MockSegmentatorExtractor mockExtractor = new MockSegmentatorExtractor();
		mockExtractor.setName(name);
		ExtractorWrapper wraper = new ExtractorWrapper(mockExtractor);
		segmentator1.setExtractor(wraper);
		wraper.getListeners().add(segmentator1);
		segmentator1.setConfig(new MockSegmentatorExtractorConfig());
		mockExtractor.setExtractorSampleRate(1D);
		segmentator1.setLearningPeriod(1000L);
		segmentator1.addClassificationListener(multipeListener);
		return segmentator1;

	}

	public FrameValues getWindow(Double i, Double sampleRate) {
		FrameValues fv = new FrameValues(new Double[] {  i });
		fv.setSampleRate(sampleRate);
		return fv;
	}
	
	public FrameValues getWindow(Double i) {
		return getWindow(i, 1D);
	}
}

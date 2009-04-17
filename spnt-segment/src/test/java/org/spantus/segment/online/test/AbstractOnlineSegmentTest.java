package org.spantus.segment.online.test;

import org.spantus.core.FrameValues;
import org.spantus.core.extractor.ExtractorWrapper;
import org.spantus.core.marker.MarkerSet;
import org.spantus.segment.online.DecistionSegmentatorOnline;
import org.spantus.segment.online.MultipleSegmentatorOnline;
import org.spantus.segment.online.OnlineDecisionSegmentatorParam;
import org.spantus.segment.online.ThresholdSegmentatorOnline;

import junit.framework.TestCase;

public abstract class AbstractOnlineSegmentTest extends TestCase {
	protected MarkerSet segmentRuleBase(Float[] vals) {
		return segmentRuleBase(vals, 1, 1);
	}

	protected MarkerSet segmentRuleBase(Float[] vals, int step, float sampleRate) {
		DecistionSegmentatorOnline multipe = new DecistionSegmentatorOnline();
		multipe.setParam(createParam());
		ThresholdSegmentatorOnline segmentator = getSegmentator("extractor",
				multipe);
		for (int i = 0; i < vals.length; i++) {
			float f = vals[i];
			Long l = Long.valueOf(i * step);
			segmentator.calculate(l, getWindow(f, sampleRate));
		}
		return multipe.getMarkSet();
	}

	protected OnlineDecisionSegmentatorParam createParam() {
		OnlineDecisionSegmentatorParam param = new OnlineDecisionSegmentatorParam();
		param.setMinSpace(999L);
		param.setMinLength(1999L);
		return param;
	}

	public ThresholdSegmentatorOnline getSegmentator(String name,
			MultipleSegmentatorOnline multipe) {
		ThresholdSegmentatorOnline segmentator1 = new ThresholdSegmentatorOnline();
		MockSegmentatorExtractor mockExtractor = new MockSegmentatorExtractor();
		mockExtractor.setName(name);
		ExtractorWrapper wraper = new ExtractorWrapper(mockExtractor);
		segmentator1.setExtractor(wraper);
		wraper.getListeners().add(segmentator1);
		segmentator1.setConfig(new MockSegmentatorExtractorConfig());
		mockExtractor.setExtractorSampleRate(1);
		segmentator1.setLearningPeriod(1000L);
		segmentator1.setOnlineSegmentator(multipe);
		return segmentator1;

	}

	public FrameValues getWindow(float i, float sampleRate) {
		FrameValues fv = new FrameValues(new Float[] { i });
		fv.setSampleRate(sampleRate);
		return fv;
	}
	
	public FrameValues getWindow(float i) {
		return getWindow(i, 1F);
	}
}

package org.spantus.core.threshold.test;

import junit.framework.TestCase;

import org.spantus.core.FrameValues;
import org.spantus.core.extractor.DefaultExtractorConfig;
import org.spantus.core.extractor.ExtractorOutputHolder;
import org.spantus.core.threshold.DynamicThreshold;

public class DynamicThresholdTest extends TestCase {
	Float extractorSampleRate = 100F;
//	Double[] vals = new Double[]{1.0, 1.0, 2.0, 3.0, 4.0, 4.0, 3.0, 2.0, 1.0, 1.0};
	Double[][] valss = new Double[][]{
			new Double[]{1.0, 1.0, 2.0},
			new Double[]{3.0, 4.0, 4.0},
			new Double[]{3.0, 2.0, 1.0}};
	Double[] negativeVals = new Double[]{-5.0, -5.0, -3.0, -2.0, -1.0, -1.0, -2.0, -3.0, -4.0, -4.0};
	Double[][] nvalss = new Double[][]{
			new Double[]{-5.0, -5.0, -2.0},
			new Double[]{-1.0, -1.0, -1.0},
			new Double[]{-2.0, -3.0, -4.0}};
	
	
	
	public void testPositiveValuesTest(){
		DynamicThreshold threshold = new DynamicThreshold();
		ExtractorOutputHolder mockExtractor = new ExtractorOutputHolder();
		mockExtractor.setExtractorSampleRate(extractorSampleRate);
		mockExtractor.setConfig(new DefaultExtractorConfig());
		threshold.setExtractor(mockExtractor);
		Long i = 0L;
		for (Double[] dv : valss) {
			threshold.afterCalculated(i, getFrameValues(dv));
			i+=3;
		}
		assertEquals("Current Threshold Value", 1.0F, threshold.getCurrentThresholdValue());
		assertEquals("First Threshold Value", 1.1F,threshold.getThresholdValues().iterator().next());
	}
	
	public void testNegativesValuesTest(){
		DynamicThreshold threshold = new DynamicThreshold();
		ExtractorOutputHolder mockExtractor = new ExtractorOutputHolder();
		mockExtractor.setExtractorSampleRate(extractorSampleRate);
		mockExtractor.setConfig(new DefaultExtractorConfig());
		threshold.setExtractor(mockExtractor);
		Long i = 0L;
		for (Double[] dv : nvalss) {
			threshold.afterCalculated(i, getFrameValues(dv));
			i+=3;
		}
		assertEquals(-5.0F, threshold.getCurrentThresholdValue());
		assertEquals(-4.5F,threshold.getThresholdValues().iterator().next());
	}
	
	
	
	protected FrameValues  getFrameValues(Double[] vals){
		FrameValues fv = new FrameValues();
		fv.setSampleRate(100);
		for (Double float1 : vals){
			fv.add(float1.floatValue());
		}
		return fv;
	}
}

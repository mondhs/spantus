package org.spantus.exp.segment.test;

import junit.framework.TestCase;

import org.spantus.core.FrameValues;
import org.spantus.core.extractor.DefaultExtractorConfig;
import org.spantus.core.extractor.ExtractorOutputHolder;
import org.spantus.exp.threshold.SampleEstimationThreshold;

public class SampleEstimationThresholdTest extends TestCase {
	
	Double[] vals = new Double[]{1.0, 1.0, 2.0, 3.0, 4.0, 4.0, 3.0, 2.0, 1.0, 1.0};
	Double[] negativeVals = new Double[]{-5.0, -5.0, -3.0, -2.0, -1.0, -1.0, -2.0, -3.0, -4.0, -4.0};
	
	
	
	public void testPositiveValuesTest(){
		SampleEstimationThreshold threshold = new SampleEstimationThreshold();
		ExtractorOutputHolder mockExtractor = new ExtractorOutputHolder();
		mockExtractor.setConfig(new DefaultExtractorConfig());
		mockExtractor.setOutputValues(getFrameValues(vals));
		threshold.setExtractor(mockExtractor);
		assertEquals(2.0F, threshold.getCurrentThresholdValue());
		assertEquals(2.2F,threshold.getThresholdValues().iterator().next());
	}
	
	public void testNegativesValuesTest(){
		SampleEstimationThreshold threshold = new SampleEstimationThreshold();
		ExtractorOutputHolder mockExtractor = new ExtractorOutputHolder();
		mockExtractor.setConfig(new DefaultExtractorConfig());
		mockExtractor.setOutputValues(getFrameValues(negativeVals));
		threshold.setExtractor(mockExtractor);
		assertEquals(-4.0F, threshold.getCurrentThresholdValue());
		assertEquals(-3.6F,threshold.getThresholdValues().iterator().next());
	}
	
	
	
	protected FrameValues  getFrameValues(Double[] vals){
		FrameValues fv = new FrameValues();
		for (Double float1 : vals) {
			fv.add(float1.floatValue());
		}
		return fv;
	}
}

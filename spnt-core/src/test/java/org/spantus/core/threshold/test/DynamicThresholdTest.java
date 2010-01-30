package org.spantus.core.threshold.test;

import junit.framework.TestCase;

import org.spantus.core.FrameValues;
import org.spantus.core.extractor.DefaultExtractorConfig;
import org.spantus.core.extractor.ExtractorOutputHolder;
import org.spantus.core.threshold.DynamicThreshold;

public class DynamicThresholdTest extends TestCase {
	private Float extractorSampleRate = 10F;
	private static final Float thresholdCoef = 0.1F;
	private Double[][] valss = new Double[][]{
			new Double[]{1.0, 1.0, 2.0},
			new Double[]{3.0, 4.0, 4.0},
			new Double[]{3.0, 2.0, 1.0}};
	private Double[][] nvalss = new Double[][]{
			new Double[]{-5.0, -5.0, -2.0},
			new Double[]{-1.0, -1.0, -1.0},
			new Double[]{-2.0, -3.0, -4.0}};
	
	
	/**
	 * test positive values
	 */
	public void testPositiveValuesTest(){
		
		DynamicThreshold threshold = createDynamicThreshold(valss);
		assertEquals("Current Threshold Value", 1.65F, threshold.getCurrentThresholdValue());
		assertEquals("threshol collection size", 9, threshold.getThresholdValues().size());
		//values should be higher than #thresholdCoef value
		assertEquals("Threshold Value from 1 frame", 1.1F,threshold.getThresholdValues().get(1));
		assertEquals("Threshold Value from 2 frame", 2.2F,threshold.getThresholdValues().get(4));
		assertEquals("Threshold Value from 3 frame", 1.65F,threshold.getThresholdValues().get(7));
		assertEquals(1, threshold.getMarkSet().getMarkers().size());
	}
	/**
	 * test with negatinve values
	 */
	public void testNegativesValuesTest(){
		DynamicThreshold threshold = createDynamicThreshold(nvalss);
		//values should be higher then #thresholdCoef value
		assertEquals(-2.25F, threshold.getCurrentThresholdValue());
		assertEquals("threshol collection size", 9, threshold.getThresholdValues().size());
		assertEquals("Threshold Value from 1 frame", -4.5F,threshold.getThresholdValues().get(1));
		assertEquals("Threshold Value from 2 frame", -1.35F,threshold.getThresholdValues().get(4));
		assertEquals("Threshold Value from 3 frame", -2.25F,threshold.getThresholdValues().get(7));
		assertEquals(1, threshold.getMarkSet().getMarkers().size());
	}
	
	protected DynamicThreshold createDynamicThreshold(Double[][] values){
		DynamicThreshold threshold = new DynamicThreshold();
		threshold.setCoef(thresholdCoef);
		ExtractorOutputHolder mockExtractor = new ExtractorOutputHolder();
		mockExtractor.setExtractorSampleRate(extractorSampleRate);
		mockExtractor.setConfig(new DefaultExtractorConfig());
		threshold.setExtractor(mockExtractor);
		Long i = 0L;
		for (Double[] dv : values) {
			threshold.afterCalculated(i, getFrameValues(dv));
			i+=3;
		}
		return threshold;
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

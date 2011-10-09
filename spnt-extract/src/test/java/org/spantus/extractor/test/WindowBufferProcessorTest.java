package org.spantus.extractor.test;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.spantus.core.FrameValues;
import org.spantus.extractor.ExtractorConfig;
import org.spantus.extractor.FrameValuesTestUtils;
import org.spantus.extractor.WindowBufferProcessor;

public class WindowBufferProcessorTest {

	private WindowBufferProcessor processor;

	@Before
	public void setUp() throws Exception {
		processor = new WindowBufferProcessor();
	}

	@Test
	public void testTwoWindow() {
		// given
		FrameValues values = FrameValuesTestUtils.generateFrameValues(21);
		ExtractorConfig config = FrameValuesTestUtils.createExtractorConfig();
		config.setWindowSize(10);
		config.setWindowOverlap(6);
		// when
		FrameValues windowValues1 = processor.calculate(0L, values, config, new FrameValues() );
		FrameValues windowValues2 = processor.calculate(0L, values, config, windowValues1);
		// then
		Assert.assertEquals(10, windowValues1.size());
		Assert.assertEquals(9, windowValues1.getLast().intValue());
		Assert.assertEquals(10, windowValues2.size());
		Assert.assertEquals(15, windowValues2.getLast().intValue());
	}
	
	
	@Test
	public void testNotFullWindow() {
		// given
		FrameValues values = FrameValuesTestUtils.generateFrameValues(11);
		ExtractorConfig config = FrameValuesTestUtils.createExtractorConfig();
		config.setWindowSize(10);
		config.setWindowOverlap(6);
		// when
		FrameValues windowValues1 = processor.calculate(0L, values, config, new FrameValues() );
		FrameValues windowValues2 = processor.calculate(0L, values, config, windowValues1);
		// then
		Assert.assertEquals(10, windowValues1.size());
		Assert.assertEquals(9, windowValues1.getLast().intValue());
		Assert.assertNull(windowValues2);
	}


}

package org.spantus.extractor.test;

import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.spantus.core.FrameValues;
import org.spantus.extractor.ExtractorConfig;
import org.spantus.extractor.FrameValuesTestUtils;
import org.spantus.extractor.WindowBufferProcessor;
import org.spantus.extractor.WindowBufferProcessorCtx;

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
		Double size=12.0;
		Double overlap=3.0;
		config.setWindowSize(size.intValue());
		config.setWindowOverlap(overlap.intValue());
		config.setSampleRate(size*(size-overlap));
		List<FrameValues> fv = new ArrayList<FrameValues>();
		// when
		WindowBufferProcessorCtx ctx = WindowBufferProcessor.ctreateWindowBufferProcessorCtx(config);
		for (Double val : values) {
			FrameValues windowValues = processor.calculate( val, ctx);
			if(windowValues != null){
				fv.add(windowValues);
			}
		}
		// then
		Assert.assertEquals("Windows ", 2,fv.size());
		FrameValues windowValues1  = fv.get(0);
		FrameValues windowValues2 =  fv.get(1);
		Assert.assertEquals(12, windowValues1.size());
		Assert.assertEquals(11, windowValues1.getLast().intValue());
		Assert.assertEquals(0, windowValues1.getFrameIndex().intValue());
		
		Assert.assertEquals(12, windowValues2.size());
		Assert.assertEquals(20, windowValues2.getLast().intValue());
		Assert.assertEquals(1, windowValues2.getFrameIndex().intValue());
		
		Assert.assertEquals(1.0D, windowValues2.getTime());
	}
	
	
	@Test
	public void testNotFullWindow() {
		// given
		FrameValues values = FrameValuesTestUtils.generateFrameValues(11);
		ExtractorConfig config = FrameValuesTestUtils.createExtractorConfig();
		config.setWindowSize(10);
		config.setWindowOverlap(6);
		List<FrameValues> fv = new ArrayList<FrameValues>();
		// when
		WindowBufferProcessorCtx ctx = WindowBufferProcessor.ctreateWindowBufferProcessorCtx(config);
		for (Double val : values) {
			FrameValues windowValues = processor.calculate( val, ctx);
			if(windowValues != null){
				fv.add(windowValues);
			}
		}
		// then
		Assert.assertEquals("Windows ", 1,fv.size());
		FrameValues windowValues1  = fv.get(0);
		Assert.assertEquals(10, windowValues1.size());
		Assert.assertEquals(9, windowValues1.getLast().intValue());
	}


}

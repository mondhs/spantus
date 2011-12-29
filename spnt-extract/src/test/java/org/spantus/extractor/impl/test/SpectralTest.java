package org.spantus.extractor.impl.test;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.spantus.core.FrameValues;
import org.spantus.extractor.AbstractExtractor;
import org.spantus.extractor.ExtractorConfig;
import org.spantus.extractor.FrameValuesTestUtils;
import org.spantus.extractor.impl.SpectralCentroid;
import org.spantus.extractor.impl.SpectralEntropy;
import org.spantus.extractor.impl.SpectralFlux;

public class SpectralTest {
//	protected Logger log = Logger.getLogger(getClass());
	FrameValues x;
	ExtractorConfig config;
	
	@Before
	public void setUp() throws Exception {
		x = new FrameValues();
		x.setFrameIndex(1L);
		for (float i = 1; i < 6.4 * Math.PI; i+=.3) {
			x.add(Math.sin(i));
		}
		config= FrameValuesTestUtils.createExtractorConfig();
		x.setSampleRate(1.0);
	}
	
	@Test
	public void testSpectralCentroid(){
		AbstractExtractor extractor = new SpectralCentroid();
		extractor.setConfig(config);
		FrameValues y = extractor.calculateWindow(x);
//		log.debug(extractor.getName() + ": " + y);
		Assert.assertEquals(1, y.size());
	}
	@Test
	public void testSpectralEntropy(){
		AbstractExtractor extractor = new SpectralEntropy();
		extractor.setConfig(config);
		FrameValues y = extractor.calculateWindow(x);
//		log.debug(extractor.getName() + ": " + y);
		Assert.assertEquals(1, y.size());
	}
	
	@Test
	public void testSpectralFlux(){
		AbstractExtractor extractor = new SpectralFlux();
		extractor.setConfig(config);
		FrameValues y = extractor.calculateWindow(x);
//		log.debug(extractor.getName() + ": " + y);
		Assert.assertEquals(1, y.size());
	}
}

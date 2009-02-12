package org.spantus.core.extractors.impl.test;

import junit.framework.TestCase;

import org.spantus.core.FrameValues;
import org.spantus.extractor.AbstractExtractor;
import org.spantus.extractor.ExtractorConfig;
import org.spantus.extractor.impl.SpectralCentroid;
import org.spantus.extractor.impl.SpectralEntropy;
import org.spantus.extractor.impl.SpectralFlux;
import org.spantus.logger.Logger;

public class SpectralTest extends TestCase{
	protected Logger log = Logger.getLogger(getClass());
	FrameValues x;
	ExtractorConfig config;
	
	
	protected void setUp() throws Exception {
		super.setUp();
		x = new FrameValues();
		for (float i = 1; i < 6.4 * Math.PI; i+=.3) {
			x.add(new Float(Math.sin(i)));
		}
		config=new ExtractorConfig();
		config.setSampleRate(8000);
	}
	
	public void testSpectralCentroid(){
		AbstractExtractor extractor = new SpectralCentroid();
		extractor.setConfig(config);
		FrameValues y = extractor.calculateWindow(x);
		log.debug(extractor.getName() + ": " + y);
		assertEquals(1, y.size());
	}
	public void testSpectralEntropy(){
		AbstractExtractor extractor = new SpectralEntropy();
		extractor.setConfig(config);
		FrameValues y = extractor.calculateWindow(x);
		log.debug(extractor.getName() + ": " + y);
		assertEquals(1, y.size());
	}
	public void testSpectralFlux(){
		AbstractExtractor extractor = new SpectralFlux();
		extractor.setConfig(config);
		FrameValues y = extractor.calculateWindow(x);
		log.debug(extractor.getName() + ": " + y);
		assertEquals(1, y.size());
	}
}

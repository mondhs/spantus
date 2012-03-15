package org.spantus.extractor.impl.test;

import java.util.Collection;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.spantus.core.FrameValues;
import org.spantus.core.FrameVectorValues;
import org.spantus.extractor.AbstractExtractor;
import org.spantus.extractor.ExtractorConfig;
import org.spantus.extractor.FrameValuesTestUtils;
import org.spantus.extractor.impl.DeltaMFCCExtractor;
import org.spantus.extractor.impl.SpectralCentroid;
import org.spantus.extractor.impl.SpectralEntropy;
import org.spantus.extractor.impl.SpectralFlux;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;

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
	
	@Test
	public void testDeltaMFCC(){
		DeltaMFCCExtractor extractor = new DeltaMFCCExtractor();
		extractor.setConfig(config);
		
		Collection<Double> x2Collection = Collections2.transform(x, new Function<Double, Double>(){
			@Override
			public Double apply(Double input) {
				return input*2;
			}
		});
		Collection<Double> x3Collection = Collections2.transform(x, new Function<Double, Double>(){
			@Override
			public Double apply(Double input) {
				return input*3;
			}
		});
		FrameValues x2 = new FrameValues(x2Collection, x.getSampleRate());
		x2.setFrameIndex((long) 2);
		FrameValues x3 = new FrameValues(x3Collection, x.getSampleRate());
		x3.setFrameIndex((long) 3);
		
		FrameVectorValues y = extractor.calculateWindow(x);
		FrameVectorValues y2 = extractor.calculateWindow(x2);
		FrameVectorValues y3 = extractor.calculateWindow(x3);
		
//		log.debug(extractor.getName() + ": " + y);
		Assert.assertEquals(1, y.size());
		Assert.assertEquals(1, y2.size());
		Assert.assertNotSame(y2.get(0).get(0), y3.get(0).get(0));
	}
}

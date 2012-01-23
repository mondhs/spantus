/**
 * Part of program for analyze speech signal 
 * Copyright (c) 2008 Mindaugas Greibus (spantus@gmail.com)
 * http://spantus.sourceforge.net
 * 
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the
 * Free Software Foundation; either version 2 of the License, or (at your
 * option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 675 Mass Ave, Cambridge, MA 02139, USA.
 * 
 */
package org.spantus.extractor.impl.test;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.spantus.core.FrameValues;
import org.spantus.extractor.ExtractorConfig;
import org.spantus.extractor.FrameValuesTestUtils;
import org.spantus.extractor.impl.SignalExtractor;
import org.spantus.extractor.modifiers.MeanExtractor;
import org.spantus.logger.Logger;

public class SignalExtractorTest{

	protected Logger log = Logger.getLogger(getClass());
	FrameValues x;
	ExtractorConfig config;
	
	@Before
	public void setUp() throws Exception {
		x = new FrameValues();
		x.setSampleRate(1D);
		for (float i = 1; i < 6.4 * Math.PI; i+=.3) {
			x.add(Math.sin(i));
		}
		config=FrameValuesTestUtils.createExtractorConfig();
	}
	@Test
	public void testSignal(){
		SignalExtractor extractor = new SignalExtractor();
		extractor.setDownScale(1);
		int expectedSize = x.size();
		extractor.setConfig(config);
		FrameValues y = extractor.calculate(0L, x);
		log.debug(extractor.getName() + ": " + y);
		Assert.assertEquals(expectedSize, y.size());
		Assert.assertEquals(extractor.getExtractorSampleRate(), extractor.getConfig().getSampleRate());
	}
	
	public void testMean(){
		MeanExtractor meanExtractor = new MeanExtractor();
		for (int i = 1; i < 10; i++) {
			meanExtractor.calculateMean((double)i);
		}
		Assert.assertEquals(5F, meanExtractor.getMean()) ;
		Assert.assertEquals(2.738613F, meanExtractor.getStdev()) ;
		
	}
	
	public void testDownSampledSignal(){
		SignalExtractor extractor = new SignalExtractor();
		extractor.setDownScale(2);
		int expectedSize = x.size()/2;
		extractor.setConfig(config);
		FrameValues y = extractor.calculate(0L,x);
		log.debug(extractor.getName() + ": " + y);
		Assert.assertEquals(expectedSize, y.size());
		Assert.assertEquals(extractor.getExtractorSampleRate()*2, extractor.getConfig().getSampleRate());
		
	}
}

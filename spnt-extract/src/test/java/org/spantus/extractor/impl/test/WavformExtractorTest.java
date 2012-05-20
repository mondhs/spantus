/*
 	Copyright (c) 2009 Mindaugas Greibus (spantus@gmail.com)
 	Part of program for analyze speech signal
 	http://spantus.sourceforge.net

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>
 */
package org.spantus.extractor.impl.test;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.spantus.core.FrameValues;
import org.spantus.core.FrameVectorValues;
import org.spantus.core.extractor.IExtractorConfig;
import org.spantus.extractor.ExtractorConfigUtil;
import org.spantus.extractor.impl.WavformExtractor;

/**
 * 
 * @author mondhs
 */
public class WavformExtractorTest {
	IExtractorConfig config;
	FrameValues x;

	@Before
	public void setUp() throws Exception {
		x = new FrameValues();
		x.setSampleRate(8000D);
		for (float i = 1; i < 6.4 * 2 * Math.PI; i += .3) {
			x.add(Math.sin(i));
		}
		config = ExtractorConfigUtil.defaultConfig(x.getSampleRate());

	}

	@Test
	public void testWavformExtractor() {

		// given
		WavformExtractor wavformExtractor = new WavformExtractor();
		wavformExtractor.setDevideInto(3);
		wavformExtractor.setConfig(config);

		// when
		FrameVectorValues y = wavformExtractor.calculateWindow(x);
		;

		// then
		Assert.assertEquals(3, y.size());
		Assert.assertEquals("Times equals", 21, y.getTime(), 0);
//		Assert.assertEquals("Times equals",x.getTime(), y.getTime(), 0.005);

	}
}

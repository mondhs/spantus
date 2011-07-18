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
import org.spantus.core.extractor.IExtractor;
import org.spantus.core.extractor.IExtractorVector;
import org.spantus.extractor.ExtractorConfig;
import org.spantus.extractor.ExtractorResultBuffer;
import org.spantus.extractor.ExtractorResultBuffer3D;
import org.spantus.extractor.impl.EnergyExtractor;
import org.spantus.extractor.impl.WavformExtractor;

/**
 *
 * @author mondhs
 */
public class WavformExtractorTest {
        ExtractorConfig config;
        FrameValues x;

        @Before
	public void setUp() throws Exception {
		x = new FrameValues();
                x.setSampleRate(8000D);
		for (float i = 1; i < 6.4 * Math.PI; i+=.3) {
			x.add(Math.sin(i));
		}
		config=new ExtractorConfig();
                config.setWindowOverlap(30);
		config.setSampleRate(x.getSampleRate());


	}
        
	@Test
	public void testWavformExtractor(){
		IExtractorVector extractor = new ExtractorResultBuffer3D(
                        new WavformExtractor());
                IExtractor energyExtractor = new ExtractorResultBuffer(
                        new EnergyExtractor());
		energyExtractor.setConfig(config);
                extractor.setConfig(config);
                
		extractor.putValues(1L, x);
		energyExtractor.putValues(1L, x);
                FrameVectorValues y = extractor.getOutputValues();
                FrameValues yEnergy = energyExtractor.getOutputValues();
//		log.debug(extractor.getName() + ": " + y);
		Assert.assertEquals(6, y.size());
                Assert.assertEquals(2, yEnergy.size());

                Assert.assertEquals("Times equals",yEnergy.getTime(), y.getTime(),
                        0.000001);
                Assert.assertEquals("Times equals",(double)x.getTime(),
                        (double)y.getTime(), 0.005);
                
	}
}

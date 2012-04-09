package org.spantus.core.io.test;

import javax.sound.sampled.AudioFormat;

import junit.framework.Assert;

import org.junit.Test;
import org.spantus.core.io.AudioUtil;

public class AudioUtilTest {
	
	@Test
	public void test16Conversion(){
		AudioFormat afSigned = new AudioFormat(1,16,1,true,true);
//		AudioFormat afNotSigned = new AudioFormat(1,16,1,false,true);
		
		assertCaclc(new Byte[]{1,1}, 257D, afSigned);

		assertCaclc(new Byte[]{3,32}, 800D, afSigned);

		
	}
	public void assertCaclc(Byte[] bs1, Double number, AudioFormat af){
		Double f1 = AudioUtil.read16(bs1[0], bs1[1], true, true);
		Assert.assertEquals("conversion error", number,f1);
		Byte[] bs = AudioUtil.get16(f1, af);
		assertEquals(bs1, bs);
	}
	
	public void assertEquals(Byte[] bs1, Byte[] bs2){
		Assert.assertEquals("conversion error", bs1[0],bs2[0]);
		Assert.assertEquals("conversion error", bs1[1],bs2[1]);
	}
}

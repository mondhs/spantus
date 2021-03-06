package org.spantus.extractor.impl.test;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.spantus.core.FrameValues;
import org.spantus.extractor.FrameValuesTestUtils;
import org.spantus.extractor.impl.LogAttackTimeExtractor;
/**
 * 
 * @author Mindaugas Greibus
 *
 */

public class LogAttackTimeExtractorTest{
	LogAttackTimeExtractor latExtractor;
	
	@Before
	public void setUp() throws Exception {
		latExtractor = new LogAttackTimeExtractor();
		latExtractor.setThreshold(.5F);
	}
	
	@Test
	public void testLAT() throws Exception {
		Integer[] dArr = new Integer[]{
				1, 2, 3, 4, 5, 6, 5 ,4 ,3
		};
		FrameValues fv = latExtractor.calculateWindow(FrameValuesTestUtils.convert(dArr));
		Assert.assertTrue(3F < fv.get(0));
	}
	

}

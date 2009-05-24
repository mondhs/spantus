package org.spantus.core.extractors.impl.test;

import org.spantus.core.FrameValues;
import org.spantus.extractor.impl.LogAttackTimeExtractor;

import junit.framework.TestCase;
/**
 * 
 * @author Mindaugas Greibus
 *
 */
public class LogAttackTimeExtractorTest extends TestCase {
	LogAttackTimeExtractor latExtractor;
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		latExtractor = new LogAttackTimeExtractor();
		latExtractor.setThreshold(.5F);
	}
	
	public void testLAT() throws Exception {
		Integer[] dArr = new Integer[]{
				1, 2, 3, 4, 5, 6, 5 ,4 ,3
		};
		FrameValues fv = latExtractor.calculateWindow(convert(dArr));
		assertEquals(new Double(Math.log10(2)).floatValue(), fv.get(0));
	}
	
	public FrameValues convert(Integer[] intArr){
		FrameValues rtn = new FrameValues();
		for (Integer i1 : intArr) {
			rtn.add(i1.floatValue());
		}
		return rtn;
	}
}

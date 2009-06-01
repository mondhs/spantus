package org.spantus.core.extractors.impl.test;

import junit.framework.TestCase;

import org.spantus.core.FrameValues;
import org.spantus.extractor.impl.LPCResidualExtractor;
/**
 * 
 * @author Mindaugas Greibus
 *
 */
public class LPCResidualExtractorTest extends TestCase {
	LPCResidualExtractor lpcResidualExtractor;
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		lpcResidualExtractor = new LPCResidualExtractor();
	}
	
	public void testLPCResidual() throws Exception {
		Integer[] dArr = new Integer[100];
		for (int i = 0; i < dArr.length; i++) {
			dArr[i]=i;
		}
		FrameValues fv = lpcResidualExtractor.calculateWindow(convert(dArr));
		assertEquals(0F, fv.get(0));
	}
	
	public FrameValues convert(Integer[] intArr){
		FrameValues rtn = new FrameValues();
		for (Integer i1 : intArr) {
			rtn.add(i1.floatValue());
		}
		return rtn;
	}
}

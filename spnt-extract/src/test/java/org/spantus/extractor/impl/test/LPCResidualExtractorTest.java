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
import org.spantus.extractor.impl.LPCResidualExtractor;
/**
 * 
 * @author Mindaugas Greibus
 * 
 * @since 0.0.1
 * Created Jun 3, 2009
 *
 */
public class LPCResidualExtractorTest {
	LPCResidualExtractor lpcResidualExtractor;
	
	@Before
	public void setUp() throws Exception {
		lpcResidualExtractor = new LPCResidualExtractor();
	}
	@Test
	public void testLPCResidual() throws Exception {
		Integer[] dArr = new Integer[100];
		for (int i = 0; i < dArr.length; i++) {
			dArr[i]=i;
		}
		FrameValues fv = lpcResidualExtractor.calculateWindow(convert(dArr));
		Assert.assertTrue( fv.get(0)>0F);
	}
	
	public FrameValues convert(Integer[] intArr){
		FrameValues rtn = new FrameValues();
		for (Integer i1 : intArr) {
			rtn.add(i1.floatValue());
		}
		return rtn;
	}
}

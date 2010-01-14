package org.spantus.math.test;

import java.util.List;
import java.util.logging.Logger;

import junit.framework.TestCase;

import org.spantus.math.MFCC;
import org.spantus.math.MatrixUtils;

public class MFCCTest extends TestCase{
	Logger log = Logger.getLogger(this.getClass().getSimpleName());
	List<Float> x;
	double[] mfccData =  new double[]{
			-2.7508381928662002, 3.5377788981783587, -9.698668856772205, 6.630642620376074, -2.7145711732056372, -2.3413094866154793, 2.9234692527998845, -1.982370166347817, -0.6955000406536725, 1.6206185119511574, -1.6933507398581757, 0.478056346492891
	};
	
	protected void setUp() throws Exception {
		super.setUp();
		x = MatrixUtils.zeros(256);
        x.set(2, 10f); x.set(4, 14f);

	}
	
	public void testMFCC(){
		assertEquals(256, x.size());
		List<Float> mfcc = MFCC.calculateMFCC(x, 800);
		assertEquals(mfcc.size(), 13);
//		assertCollectionEqual(mfcc, mfccData);
		log.severe("mfcc" + mfcc);
	}
	
	
	public void assertCollectionEqual(List<Float> coll, double[] arr){
		if(coll.size() !=  arr.length){
			assertTrue(false);
		}
		for (int i = 0; i < arr.length; i++) {
			double double1 = arr[i];
			assertTrue("element not match" + i ,equals(double1, coll.get(i).floatValue()));
		}
	}
	public boolean equals( double d1, Float d2 ){
//		log.log(Level.SEVERE, " |" + d1 + "-" + d2 + "|=" + Math.abs(d1 - d2) );
		return Math.abs(d1 - d2) < 0.000001;
	}

}

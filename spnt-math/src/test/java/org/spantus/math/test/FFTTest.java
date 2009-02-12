package org.spantus.math.test;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import junit.framework.TestCase;

import org.spantus.math.TransformUtil;

public class FFTTest extends TestCase{
	Logger log = Logger.getLogger(this.getClass().getSimpleName());
	List<Float> x;
	double[] fftData =  new double[]{
			0.9971752, 
			1.137864, 
			1.8730474, 
			31.974953, 
			1.7774407, 
			0.83476263, 
			0.54233867, 
			0.40267822, 
			0.32176602, 
			0.2693107, 
			0.23269865, 
			0.20577854, 
			0.18520701, 
			0.16901876, 0.15599051, 0.14531639, 0.13644774, 0.12899686, 0.1226832, 0.117299564, 0.11269078, 0.10873479, 0.10533715, 0.10242466, 0.099940635, 0.097837806, 0.09607709, 0.09463118, 0.09347728, 0.09259753, 0.0919773, 0.09160867, 0.09148574, 0.0};
	
	
	protected void setUp() throws Exception {
		super.setUp();
		x = new ArrayList<Float>();
		for (float i = 1; i < 6.4 * Math.PI; i+=.3) {
			x.add(new Float(Math.sin(i)));
		}
	}
	
	public void testFft(){
		assertEquals(64, x.size());
		List<Float> result = TransformUtil.calculateFFTMagnitude(new ArrayList<Float>(x));
		assertEquals(34, result.size());
		assertCollectionEqual(result, fftData);
		log.severe("Result: " + result);
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

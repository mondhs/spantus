	package org.spantus.math.test;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import junit.framework.TestCase;

import org.spantus.math.Autocorrelation;
import org.spantus.math.LPC;

public class LpcTest extends TestCase {
	
	Logger log = Logger.getLogger(this.getClass().getSimpleName());
	
	List<Float> x;
	int order = 12;
	Float[] autoCorrData = new Float[]{
			   1.000000f,
			   0.952101f,
			   0.820204f,
			   0.617046f,
			   0.361552f,
			   0.077072f,
			  -0.210749f,
			  -0.476286f,
			  -0.696211f,
			  -0.851546f,
			  -0.929298f,
			  -0.923538f,
	};
	Float[] lpcData = new Float[]{
	1.0f, -1.290723230620402f, 0.2011109322227299f, 
	0.12346162699398927f, 0.0757128454910762f, 0.04630056082569299f, 
	0.028101566080985026f, 0.016708342196380798f, 0.009361624051918024f,
	0.004282148523291314f, 2.397440795367852E-4f, 0.00937779814279676f};
	
	
	protected void setUp() throws Exception {
		super.setUp();
		x = new ArrayList<Float>();
//		x.addAll(Arrays.asList(data));
		for (float i = 1; i < 16 * Math.PI; i+=.3) {
			x.add(new Float(Math.sin(i)));
		}
	}
	
	
	public void testLpc(){
		assertEquals(165, x.size());
		//log.log(Level.SEVERE, "x: " + x);
		List<Float> autocorr = Autocorrelation.calc(x, order);
		log.log(Level.SEVERE, "autocorr: " + autocorr);
		assertEquals(order, autocorr.size());
		assertCollectionEqual(autocorr, autoCorrData);
	
		List<Float> lpc = LPC.calcForAutocorr(autocorr);
		log.log(Level.SEVERE, "lpc: " + lpc);
		assertEquals(autocorr.size(), lpc.size());
		assertCollectionEqual(lpc, lpcData);
	}
	
	public void assertCollectionEqual(List<Float> coll, Float[] arr){
		if(coll.size() !=  arr.length){
			assertTrue(false);
		}
		for (int i = 0; i < arr.length; i++) {
			Float float1 = arr[i];
			assertTrue("element not match" + i ,equals(float1, coll.get(i).floatValue()));
		}
	}
	
	public boolean equals( Float d1, Float d2 ){
//		log.log(Level.SEVERE, " |" + d1 + "-" + d2 + "|=" + Math.abs(d1 - d2) );
		return Math.abs(d1 - d2) < 0.001;
	}
}

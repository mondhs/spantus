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
	
	List<Double> x;
	int order = 12;
	Double[] autoCorrData = new Double[]{
			   1.000000D,
			   0.952101D,
			   0.820204D,
			   0.617046D,
			   0.361552D,
			   0.077072D,
			  -0.210749D,
			  -0.476286D,
			  -0.696211D,
			  -0.851546D,
			  -0.929298D,
			  -0.923538D,
	};
	Double[] lpcData = new Double[]{
	-1.290723230620402D, 0.2011109322227299D, 
	0.12346162699398927D, 0.0757128454910762D, 0.04630056082569299D, 
	0.028101566080985026D, 0.016708342196380798D, 0.009361624051918024D,
	0.004282148523291314D, 2.397440795367852E-4D, 0.00937779814279676D};
	
	
	protected void setUp() throws Exception {
		super.setUp();
		x = new ArrayList<Double>();
//		x.addAll(Arrays.asList(data));
		for (Double i = 1D; i < 16 * Math.PI; i+=.3) {
			x.add(new Double(Math.sin(i)));
		}
	}
	
	
	public void testLpc(){
		assertEquals(165, x.size());
		//log.log(Level.SEVERE, "x: " + x);
		List<Double> autocorr = Autocorrelation.calc(x, order);
		log.log(Level.SEVERE, "autocorr: " + autocorr);
		assertEquals(order, autocorr.size());
		assertCollectionEqual(autocorr, autoCorrData);
	
		List<Double> lpc = LPC.calcForAutocorr(autocorr).getResult();
		log.log(Level.SEVERE, "lpc: " + lpc);
		assertEquals(autocorr.size()-1, lpc.size());
		assertCollectionEqual(lpc, lpcData);
	}
	
	public void assertCollectionEqual(List<Double> coll, Double[] arr){
		if(coll.size() !=  arr.length){
			assertTrue(false);
		}
		for (int i = 0; i < arr.length; i++) {
			Double float1 = arr[i];
			assertTrue("element not match" + i ,equals(float1, coll.get(i)));
		}
	}
	
	public boolean equals( Double d1, Double d2 ){
//		log.log(Level.SEVERE, " |" + d1 + "-" + d2 + "|=" + Math.abs(d1 - d2) );
		return Math.abs(d1 - d2) < 0.001;
	}
}

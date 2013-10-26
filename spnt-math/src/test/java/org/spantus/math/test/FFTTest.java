package org.spantus.math.test;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import junit.framework.TestCase;

import org.spantus.math.TransformUtil;
import org.spantus.math.VectorUtils;

import edu.cmu.sphinx.spantus.frontend.transform.DiscreteFourierTransform;

public class FFTTest extends TestCase{
	Logger log = Logger.getLogger(this.getClass().getSimpleName());
	List<Double> x;
	double[] fftData =  new double[]{
			0.9971997129313572,
			1.137879946835331,
			1.8730209938589475,
			31.974957881189027,
			1.7774082983765895,
			0.8347745730408521,
			0.5423521465729119,
			0.4026843043302596,
			0.3217679568570612,
			0.26931402495971934,
			0.23270248963345724,
			0.2057808308959133,
			0.18520826748899344,
			0.16902114839842936,
			0.1559926403348743,
			0.14531814828704587,
			0.13644875808647264,
			0.12899757427756814,
			0.12268436616075172,
			0.11730145890601708,
			0.11269195477802021,
			0.10873539516024108,
			0.1053380679404346,
			0.10242630321266626,
			0.09994174353041096,
			0.09783795104362926,
			0.09607794053628695,
			0.09463236782963044,
			0.09347819228715212,
			0.09259769030284341,
			0.09197773547509255,
			0.09160928779886557,
			0.09148705308595306,
			0.0,};
	
	
	protected void setUp() throws Exception {
		super.setUp();
		x = new ArrayList<Double>();
		for (Double i = 1D; i < 6.4 * Math.PI; i+=.3) {
			x.add(new Double(Math.sin(i)));
		}
	}
	
	public void testFft(){
		assertEquals(64, x.size());
		List<Double> result = TransformUtil.calculateFFTMagnitude(new ArrayList<Double>(x));
		assertEquals(34, result.size());
		assertCollectionEqual(result, fftData);
		log.severe("Result: " + result);
	}
    public void testFftSpinx(){
		assertEquals(64, x.size());
        int logm = (int) (Math.log(x.size()) / Math.log(2));
        int n = 1 << logm;

        DiscreteFourierTransform sFft = new DiscreteFourierTransform(n, false);
        sFft.initialize();
        Double[] sResult = sFft.process(VectorUtils.toArray(new ArrayList<Double>(x)),16000);
        List<Double> result = VectorUtils.toList(sResult);

		assertEquals(33, result.size());
//		assertCollectionEqual(result, fftData);
		log.severe("Sphinx Result: " + result);
	}
	
	
	public void assertCollectionEqual(List<Double> coll, double[] arr){
		assertEquals("sizes should be same", coll.size(),arr.length );
		for (int i = 0; i < arr.length; i++) {
			double double1 = arr[i];
			assertTrue("element not match" + i + "("+double1+"<>"+coll.get(i)+")" ,equals(double1, coll.get(i)));
		}
	}
	public boolean equals( Double d1, Double d2 ){
//		log.log(Level.SEVERE, " |" + d1 + "-" + d2 + "|=" + Math.abs(d1 - d2) );
		return Math.abs(d1 - d2) < 0.000001;
	}

}

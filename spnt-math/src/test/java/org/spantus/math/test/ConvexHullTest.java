package org.spantus.math.test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import junit.framework.Assert;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.junit.Before;
import org.junit.Test;
import org.spantus.math.services.impl.ConvexHullServiceImpl;
import org.spantus.math.windowing.Windowing;
import org.spantus.math.windowing.WindowingEnum;
import org.spantus.math.windowing.WindowingFactory;

public class ConvexHullTest {
	public static final Logger LOG = LoggerFactory.getLogger(ConvexHullTest.class);
	private List<Double> signalDouble;
	private Integer[] idealResult = new Integer[]{
			0,
			15,
			16,
			48,
			58,
			63
	};
	ConvexHullServiceImpl convexHullServiceImpl;
	
	@Before
	public void setup() {
		convexHullServiceImpl = new ConvexHullServiceImpl();
		signalDouble= new ArrayList<Double>();
		 List<Double> window =new  ArrayList<Double>();
		 
		for (double i = 0; i < 3 * Math.PI; i+=.3) {
			window.add(Math.abs(Math.sin(i)));
		}
		
		Windowing windowing = WindowingFactory.createWindowing(WindowingEnum.Hamming);
		windowing.apply(window);

//		int j = 0;
		for (int count = 0; count < 2; count++) {

			for (Double double1 : window) {
				signalDouble.add(double1);
			}
		}

//		signalFloat.add(1F);
//		signalFloat.add(10F);
//		signalFloat.add(5F);
//		signalFloat.add(10F);
//		signalFloat.add(1F);
	}

	@Test
	public void testConvexHull(){
		Map<Integer, Double> result = convexHullServiceImpl.calculateConvexHull(signalDouble);
		assertCollectionEqual(result, idealResult);
		
//		for (Entry<Integer, Double> pair : result.entrySet()) {
//			LOG.debug(pair);
//		}
		
	}
	@Test
	public void testConvexHullTreshold(){
		List<Double> result = convexHullServiceImpl.calculateConvexHullTreshold(signalDouble);
		
//		StringBuilder signalsb = new StringBuilder();
//		for (Double pair : signalDouble) {
//			signalsb.append(pair).append(",");
//		}
//		StringBuilder sb = new StringBuilder();
//		for (Double pair : result) {
//			sb.append(pair).append(",");
//		}
//		LOG.debug("\n" + signalsb.toString() + "\n" +  sb.toString());		
		Assert.assertEquals("size",result.size(), signalDouble.size());
	}

	private void assertCollectionEqual(Map<Integer, Double> result,
			Integer[] givenIdealResult) {
		Assert.assertEquals("size",result.size(), givenIdealResult.length);
		
		for (int i = 0; i < givenIdealResult.length; i++) {
			Integer float1 = givenIdealResult[i];
			Assert.assertNotNull("element not match" + i ,result.get(float1));
		}		
	}
	
}

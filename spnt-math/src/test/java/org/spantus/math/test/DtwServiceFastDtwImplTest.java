/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.spantus.math.test;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.spantus.math.dtw.DtwResult;
import org.spantus.math.dtw.DtwServiceFastDtwImpl;
import org.spantus.math.dtw.DtwServiceJavaMLImpl;

/**
 * 
 * @author mondhs
 */
public class DtwServiceFastDtwImplTest {

	Double[] targetArr = new Double[] { 1D, 1D, 2D, 2D, 3D, 3D, 3D, 3D, 5D, 5D};
	Double[][] sampleArr = new Double[][] {
			new Double[] { 0D, 1D, 2D, 2D, 1D, 1D, 4D, 4D, 4D, 5D, 5D },
			new Double[] { 2D, 2D, 3D, 3D, 5D, 7D, 7D, 8D, 9D, 9D, 0D, 0D },
			new Double[] {  0D, 1D, 1D, 1D, 2D, 3D, 3D, 4D, 5D, 5D, 5D, 0D, 0D  },
			new Double[] { 10D, 10D, 20D, 20D, 30D, 30D, 40D, 40D, 50D, 50D } };
	List<Double> target = new ArrayList<Double>(Arrays.asList(targetArr));
	List<Double> sample1 = new ArrayList<Double>(Arrays.asList(sampleArr[0]));
	List<Double> sample2 = new ArrayList<Double>(Arrays.asList(sampleArr[1]));
	List<Double> sample3 = new ArrayList<Double>(Arrays.asList(sampleArr[2]));
	List<Double> sample4 = new ArrayList<Double>(Arrays.asList(sampleArr[3]));
	private static float radius = 10;
	private DtwServiceFastDtwImpl dtwService;

	@Before
	public void onSetup() {
		dtwService = new DtwServiceFastDtwImpl();
	}

	@Test
	public void testCalculateDistance() {
		
		//given
		
		//when 
                Double distance0 = dtwService.calculateDistance(target, target);
		Double distance1 = dtwService.calculateDistance(target, sample1);
		Double distance2 = dtwService.calculateDistance(target, sample2);
		Double distance3 = dtwService.calculateDistance(target, sample3);
		//then
                Assert.assertEquals("dynamic time wraping: ", 0D, distance0);
		Assert.assertEquals("dynamic time wraping: ", 7D, distance1);
		Assert.assertEquals("dynamic time wraping: ", 27D, distance2);
		Assert.assertEquals("dynamic time wraping: ", 12D, distance3);
	}


	@Test 
	public void testCalculateDistanceVector() {
		//given
		List<List<Double>> targetMatrix = createMatrix(target);
		
		//when
                Double distance0 = dtwService
				.calculateDistanceVector(targetMatrix, targetMatrix);
		Double distance1 = dtwService
				.calculateDistanceVector(targetMatrix, createMatrix(sample1));
		Double distance2 = dtwService
				.calculateDistanceVector(targetMatrix, createMatrix(sample2));
		Double distance3 = dtwService
				.calculateDistanceVector(targetMatrix, createMatrix(sample3));
		
		//then
                Assert.assertEquals("dynamic time wraping: ", 0D, distance0);
		Assert.assertEquals("dynamic time wraping: ", 7D, distance1);
		Assert.assertEquals("dynamic time wraping: ", 27D, distance2);
		Assert.assertEquals("dynamic time wraping: ", 12D, distance3);
	}

	public static List<List<Double>> createMatrix(List<Double> vector) {
		List<List<Double>> targetMatrix = new ArrayList<List<Double>>();
		for (Double float1 : vector) {
			List<Double> column = new ArrayList<Double>();
			column.add(float1);
			targetMatrix.add(column);
		}

		return targetMatrix;
	}

//	Test
	public void testDtwPerformance() {
		// given
		float radius = 20;
		dtwService.setSearchRadius(radius);
//		dtwService
//				.setSearchWindow(DtwServiceJavaMLImpl.JavaMLSearchWindow.ParallelogramWindow);
//		dtwService.setLocalConstaints(DtwServiceJavaMLImpl.JavaMLLocalConstraint.Angle);
		//when
		StringBuilder sb = new StringBuilder();
		for (int i = 100; i < 10000; i += 100) {
			sb.append(performance(i)).append("\n");
		}
//		debug("\n" + sb.toString());

	}

	public StringBuffer performance(int size) {
		int offset = 10;
		StringBuffer sb = new StringBuffer();
		debug("Dtw calculation size:" + size);

		sb.append(size).append(",");
		List<Double> sampleP = new ArrayList<Double>();
		List<Double> targetP = new ArrayList<Double>();

		for (int i = 0; i < size; i++) {
			sampleP.add(Double.valueOf(i));
			if (i >= offset) {
				targetP.add(Double.valueOf(i));
			}
		}

		long start = System.currentTimeMillis();

		start = System.currentTimeMillis();
		Double result = dtwService.calculateDistance(sampleP, targetP);
		long time = (System.currentTimeMillis() - start);
		debug("Dtw calculation time ms :" + time);
		Assert.assertTrue("dinamic time wraping : ",
				!result.equals(Double.MAX_VALUE));
		debug("result: " + result);
		sb.append(time).append(",");

		return sb;

	}

	private void debug(String str) {
		System.out.println("[DtwTest]: " + str);
	}
}

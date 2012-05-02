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
import org.junit.Test;
import org.spantus.math.dtw.DtwResult;
import org.spantus.math.dtw.DtwServiceJavaMLImpl;

/**
 * 
 * @author mondhs
 */
public class DtwServiceJavaMLImplTest {

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
	private DtwServiceJavaMLImpl dtwService;

	@Before
	public void onSetup() {
		dtwService = new DtwServiceJavaMLImpl();
	}

	@Test
	public void testCalculateDistance() {
		
		//given
		
		//when 
		Double distance1 = dtwService.calculateDistance(target, sample1);
		Double distance2 = dtwService.calculateDistance(target, sample2);
		Double distance3 = dtwService.calculateDistance(target, sample3);
		//then
		Assert.assertEquals("dynamic time wraping: ", 6D, distance1);
		Assert.assertEquals("dynamic time wraping: ", 25D, distance2);
		Assert.assertEquals("dynamic time wraping: ", 11D, distance3);
	}

	@Test
	public void testExpandedResWindow() {
		// given
		int radius = 3;

		// when

		dtwService.setSearchRadius(radius);
		dtwService
				.setSearchWindow(DtwServiceJavaMLImpl.JavaMLSearchWindow.ExpandedResWindow);
		Double distance2 = dtwService.calculateDistance(target, sample2);
		Double distance3 = dtwService.calculateDistance(target, sample3);
		Double distance4 = dtwService.calculateDistance(target, sample4);

		// then
		Assert.assertEquals("dynamic time wraping: ", 25D, distance2);
		Assert.assertEquals("dynamic time wraping: ", 11D, distance3);
		Assert.assertEquals("dynamic time wraping: ", 250D, distance4);
	}
	
	@Test
	public void testLinearWindow() {
		// given
		int radius = 3;
		dtwService.setSearchRadius(radius);
		dtwService
				.setSearchWindow(DtwServiceJavaMLImpl.JavaMLSearchWindow.LinearWindow);

		// when


		Double distance2 = dtwService.calculateDistance(target, sample2);
		Double distance3 = dtwService.calculateDistance(target, sample3);
		Double distance4 = dtwService.calculateDistance(target, sample4);

		// then
		Assert.assertEquals("dynamic time wraping: ", 25D, distance2);
		Assert.assertEquals("dynamic time wraping: ", 11D, distance3);
		Assert.assertEquals("dynamic time wraping: ", 254D, distance4);
	}
	
	@Test
	public void testAngleLocalConstraint_LinearWindow() {
		// given
		int radius = 3;
		dtwService.setSearchRadius(radius);
		dtwService
				.setSearchWindow(DtwServiceJavaMLImpl.JavaMLSearchWindow.LinearWindow);
		dtwService.setLocalConstaints(DtwServiceJavaMLImpl.JavaMLLocalConstraint.Angle);

		// when
		Double distance2 = dtwService.calculateDistance(target, sample2);
		Double distance3 = dtwService.calculateDistance(target, sample3);
		Double distance4 = dtwService.calculateDistance(target, sample4);

		// then
		Assert.assertEquals("dynamic time wraping: ", 19D, distance2);
		Assert.assertEquals("dynamic time wraping: ", 8D, distance3);
		Assert.assertEquals("dynamic time wraping: ", 155D, distance4);
	}
	
	
	@Test
	public void testParallelogramWindow() {
		// given
		int radius = 3;
		dtwService.setSearchRadius(radius);
		dtwService
				.setSearchWindow(DtwServiceJavaMLImpl.JavaMLSearchWindow.ParallelogramWindow);

		// when


		DtwResult dtwResult2 = dtwService.calculateInfo(target, sample2);
		DtwResult dtwResult3 = dtwService.calculateInfo(target, sample3);
		DtwResult dtwResult4 = dtwService.calculateInfo(target, sample4);
		Point firstPoint = dtwResult2.getPath().get(0);
		Point lastPoint = dtwResult2.getPath().get(dtwResult2.getPath().size()-1);

		// then
		Assert.assertEquals("dynamic time wraping: ", 32D, dtwResult2.getResult());
		Assert.assertEquals("first iteration point: ", new Point(9, 11), lastPoint);
		Assert.assertEquals("last iteration point: ", new Point(0, 0), firstPoint);
		
		Assert.assertEquals("dynamic time wraping: ", 11D, dtwResult3.getResult());
		Assert.assertEquals("dynamic time wraping: ", 264D, dtwResult4.getResult());
	}
	

	//Test 
	public void testCalculateDistanceVector() {
		//given
		List<List<Double>> targetMatrix = createMatrix(target);
		
		//when
		Double distance1 = dtwService
				.calculateDistanceVector(targetMatrix, createMatrix(sample1));
		Double distance2 = dtwService
				.calculateDistanceVector(targetMatrix, createMatrix(sample2));
		Double distance3 = dtwService
				.calculateDistanceVector(targetMatrix, createMatrix(sample3));
		
		//then
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
		int radius = 5;
		dtwService.setSearchRadius(radius);
		dtwService
				.setSearchWindow(DtwServiceJavaMLImpl.JavaMLSearchWindow.ParallelogramWindow);
		dtwService.setLocalConstaints(DtwServiceJavaMLImpl.JavaMLLocalConstraint.Angle);
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

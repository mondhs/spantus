package org.spantus.math.test;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import junit.framework.TestCase;

import org.spantus.math.DTW;
import org.spantus.math.dtw.DtwInfo;
import org.spantus.math.dtw.DtwResult;
import org.spantus.math.dtw.DtwInfo.DtwType;

public class DtwTest extends TestCase {
	
	Double[] targetArr = new Double[]{1D, 2D, 3D, 4D, 5D};
	Double[][] sampleArr = new Double[][]{
			new Double[]{1D, 2D, 1D, 4D, 4D, 5D},
			new Double[]{2D, 3D, 5D, 7D, 8D, 9D},
			new Double[]{1D, 1D, 2D, 3D, 4D, 5D, 5D}
	};
	List<Double> target = new ArrayList<Double>(Arrays.asList(targetArr));
	List<Double> sample1 = new ArrayList<Double>(Arrays.asList(sampleArr[0]));
	List<Double> sample2 = new ArrayList<Double>(Arrays.asList(sampleArr[1]));
	List<Double> sample3 = new ArrayList<Double>(Arrays.asList(sampleArr[2]));
	

	public void testDtwI(){
		assertEquals("dinamic time wraping: " , 2D, DTW.estimate(target, sample1));
		assertEquals("dinamic time wraping: " , 11D, DTW.estimate(target, sample2));
		assertEquals("dinamic time wraping: " , 0D, DTW.estimate(target, sample3));
	}
	public void testDtwII(){
		DtwInfo info1 = DTW.createDtwInfo(target, sample1);
		info1.setType(DtwType.typeII);
		assertEquals("dinamic time wraping: " , 2D, DTW.estimate(info1));
		DtwInfo info2 = DTW.createDtwInfo(target, sample2);
		info2.setType(DtwType.typeII);
		assertEquals("dinamic time wraping: " , 13D, DTW.estimate(info2));
		DtwInfo info3 = DTW.createDtwInfo(target, sample3);
		info3.setType(DtwType.typeII);
		assertEquals("dinamic time wraping: " , 1D, DTW.estimate(info3));
	}
	public void testDtwIII(){
		DtwInfo info1 = DTW.createDtwInfo(target, sample1);
		info1.setType(DtwType.typeIII);
		assertEquals("dinamic time wraping: " , 1D, DTW.estimate(info1));
		DtwInfo info2 = DTW.createDtwInfo(target, sample2);
		info2.setType(DtwType.typeIII);
		assertEquals("dinamic time wraping: " , 8D, DTW.estimate(info2));
		DtwInfo info3 = DTW.createDtwInfo(target, sample3);
		info3.setType(DtwType.typeIII);
		assertEquals("dinamic time wraping: " , 0D, DTW.estimate(info3));
	}
	
	public StringBuffer performance(int size){
		int offset = 2;
		StringBuffer sb = new StringBuffer();
		debug("Dtw calculation size:" + size);

		sb.append(size).append(",");
		List<Double> sample = new ArrayList<Double>();
		List<Double> target = new ArrayList<Double>();

		for (int i = 0; i < size; i++) {
			sample.add(Double.valueOf(i));
			if(i >= offset){
				target.add(Double.valueOf(i));
			}
		} 
		DtwInfo info = DTW.createDtwInfo(target, sample, (int)(size*.6));
//		debug("matrix: \n" + DtwUtils.logMatrix(info.getDistanceMatrix()));

		long start = System.currentTimeMillis();
		DtwResult result = null;

//		info.setType(DtwType.typeI);
//		result = DTW.dtwRecusion(info);
//		debug("Dtw calculation time ms:" + (System.currentTimeMillis() - start));
//		debug("result: " + result.getResult() + "; iter: " + info.getIterationCount() );
//		assertTrue("dinamic time wraping type I: " ,!result.getResult().equals(Double.MAX_VALUE));
		
		
		info.setType(DtwType.typeII);
		info.resetIterationCount();
		start = System.currentTimeMillis();
		result = DTW.dtwRecusion(info);
		long time = (System.currentTimeMillis() - start);
		debug("Dtw calculation time ms type II:" + time);
		assertTrue("dinamic time wraping type II: " ,!result.getResult().equals(Double.MAX_VALUE));
		debug("result: " + result.getResult() + "; iter: " + info.getIterationCount() );
		sb.append(time).append(",").append(info.getIterationCount()).append(",");

		
		info.setType(DtwType.typeIII);
		info.resetIterationCount();
		start = System.currentTimeMillis();
		result = DTW.dtwRecusion(info);
		time = (System.currentTimeMillis() - start);
		debug("Dtw calculation time ms type III:" + (System.currentTimeMillis() - start));
		assertTrue("dinamic time wraping type III: " ,!result.getResult().equals(Double.MAX_VALUE));
		debug("result: " + result.getResult() + "; iter: " + info.getIterationCount() );

		sb.append(time).append(",").append(info.getIterationCount()).append(",");

		return sb;

	}
	
	public void _testDtwPerformance(){
		StringBuffer sb = new StringBuffer();
		for (int i = 7; i < 25; i++) {
			sb.append(performance(i)).append("\n");
		}
		debug("\n" + sb.toString());
			
	}
		
	
	private void debug(String str){
		System.out.println("[DtwTest]: " + str);
	}
}

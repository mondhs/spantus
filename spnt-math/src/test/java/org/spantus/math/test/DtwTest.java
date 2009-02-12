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
	
	Float[] targetArr = new Float[]{1f, 2f, 3f, 4f, 5f};
	Float[][] sampleArr = new Float[][]{
			new Float[]{1f, 2f, 1f, 4f, 4f, 5f},
			new Float[]{2f, 3f, 5f, 7f, 8f, 9f},
			new Float[]{1f, 1f, 2f, 3f, 4f, 5f, 5f}
	};
	List<Float> target = new ArrayList<Float>(Arrays.asList(targetArr));
	List<Float> sample1 = new ArrayList<Float>(Arrays.asList(sampleArr[0]));
	List<Float> sample2 = new ArrayList<Float>(Arrays.asList(sampleArr[1]));
	List<Float> sample3 = new ArrayList<Float>(Arrays.asList(sampleArr[2]));
	

	public void testDtwI(){
		assertEquals("dinamic time wraping: " , 2f, DTW.estimate(target, sample1));
		assertEquals("dinamic time wraping: " , 11f, DTW.estimate(target, sample2));
		assertEquals("dinamic time wraping: " , 0f, DTW.estimate(target, sample3));
	}
	public void testDtwII(){
		DtwInfo info1 = DTW.createDtwInfo(target, sample1);
		info1.setType(DtwType.typeII);
		assertEquals("dinamic time wraping: " , 2f, DTW.estimate(info1));
		DtwInfo info2 = DTW.createDtwInfo(target, sample2);
		info2.setType(DtwType.typeII);
		assertEquals("dinamic time wraping: " , 13f, DTW.estimate(info2));
		DtwInfo info3 = DTW.createDtwInfo(target, sample3);
		info3.setType(DtwType.typeII);
		assertEquals("dinamic time wraping: " , 1f, DTW.estimate(info3));
	}
	public void testDtwIII(){
		DtwInfo info1 = DTW.createDtwInfo(target, sample1);
		info1.setType(DtwType.typeIII);
		assertEquals("dinamic time wraping: " , 1f, DTW.estimate(info1));
		DtwInfo info2 = DTW.createDtwInfo(target, sample2);
		info2.setType(DtwType.typeIII);
		assertEquals("dinamic time wraping: " , 8f, DTW.estimate(info2));
		DtwInfo info3 = DTW.createDtwInfo(target, sample3);
		info3.setType(DtwType.typeIII);
		assertEquals("dinamic time wraping: " , 0f, DTW.estimate(info3));
	}
	
	public StringBuffer performance(int size){
		int offset = 2;
		StringBuffer sb = new StringBuffer();
		debug("Dtw calculation size:" + size);

		sb.append(size).append(",");
		List<Float> sample = new ArrayList<Float>();
		List<Float> target = new ArrayList<Float>();

		for (int i = 0; i < size; i++) {
			sample.add(Float.valueOf(i));
			if(i >= offset){
				target.add(Float.valueOf(i));
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
//		assertTrue("dinamic time wraping type I: " ,!result.getResult().equals(Float.MAX_VALUE));
		
		
		info.setType(DtwType.typeII);
		info.resetIterationCount();
		start = System.currentTimeMillis();
		result = DTW.dtwRecusion(info);
		long time = (System.currentTimeMillis() - start);
		debug("Dtw calculation time ms type II:" + time);
		assertTrue("dinamic time wraping type II: " ,!result.getResult().equals(Float.MAX_VALUE));
		debug("result: " + result.getResult() + "; iter: " + info.getIterationCount() );
		sb.append(time).append(",").append(info.getIterationCount()).append(",");

		
		info.setType(DtwType.typeIII);
		info.resetIterationCount();
		start = System.currentTimeMillis();
		result = DTW.dtwRecusion(info);
		time = (System.currentTimeMillis() - start);
		debug("Dtw calculation time ms type III:" + (System.currentTimeMillis() - start));
		assertTrue("dinamic time wraping type III: " ,!result.getResult().equals(Float.MAX_VALUE));
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
		//System.out.println("[DtwTest]: " + str);
	}
}

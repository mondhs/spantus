/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.spantus.math.test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import junit.framework.Assert;
import net.sf.javaml.core.DenseInstance;
import net.sf.javaml.core.Instance;
import net.sf.javaml.distance.fastdtw.Abstraction;
import org.junit.Before;
import org.junit.Test;
import org.spantus.math.VectorUtils;
import org.spantus.math.dtw.DtwServiceJavaMLImpl;

/**
 *
 * @author mondhs
 */
public class DtwServiceJavaMLImplTest {

    Float[] targetArr = new Float[]{1f, 2f, 3f, 4f, 5f};
    Float[][] sampleArr = new Float[][]{
        new Float[]{1f, 2f, 1f, 4f, 4f, 5f},
        new Float[]{2f, 3f, 5f, 7f, 8f, 9f},
        new Float[]{1f, 1f, 2f, 3f, 4f, 5f, 5f},
        new Float[]{10f, 20f, 30f, 40f, 50f }
    };
    List<Float> target = new ArrayList<Float>(Arrays.asList(targetArr));
    List<Float> sample1 = new ArrayList<Float>(Arrays.asList(sampleArr[0]));
    List<Float> sample2 = new ArrayList<Float>(Arrays.asList(sampleArr[1]));
    List<Float> sample3 = new ArrayList<Float>(Arrays.asList(sampleArr[2]));
    List<Float> sample4 = new ArrayList<Float>(Arrays.asList(sampleArr[3]));
    private DtwServiceJavaMLImpl dtwService;

    @Before
    public void onSetup() {
        dtwService = new DtwServiceJavaMLImpl();
    }

    @Test
    public void testCalculateDistance() {
        Assert.assertEquals("dinamic time wraping: ", 2f, dtwService.calculateDistance(target, sample1));
        Assert.assertEquals("dinamic time wraping: ", 11f, dtwService.calculateDistance(target, sample2));
        Assert.assertEquals("dinamic time wraping: ", 0f, dtwService.calculateDistance(target, sample3));
    }
    
    @Test
    public void testfdtw(){
        Instance tsTarget = new DenseInstance(VectorUtils.toDoubleArray(target));
        Instance tsSample = new DenseInstance(VectorUtils.toDoubleArray(sample4));
        int radius =5; 
        Abstraction ac=new Abstraction(radius);
        
        double dtwResult = ac.measure(tsTarget, tsSample);
        
//        PAA tsTargetPAA = new PAA(tsTarget, radius);
//        PAA tsSamplePAA = new PAA(tsSample, radius);
//        WarpPath warpPath = new WarpPath(1); 
//        SearchWindow sw = 
////                new FullWindow(tsTarget, tsSample);
//                new LinearWindow(tsTarget, tsSample,radius);
////                new ExpandedResWindow(tsTarget, tsSample, tsTargetPAA, 
////                tsSamplePAA,warpPath,  radius);
////                new  ExpandedResWindow(tsTarget, tsSample, radius);
//        double dtwResult = DTW.getWarpDistBetween(tsTarget, tsSample, sw);
//    
        dtwService.setSearchRadius(radius);
        dtwService.setSearchWindow(
                DtwServiceJavaMLImpl.JavaMLSearchWindow.ExpandedResWindow);
        Assert.assertEquals("dinamic time wraping: ", dtwResult, 
                dtwService.calculateDistance(target, sample4).doubleValue());
        Assert.assertEquals("DTW: ", 135.0, dtwResult);
        Assert.assertEquals("dinamic time wraping: ", 11f, dtwService.calculateDistance(target, sample2));
        Assert.assertEquals("dinamic time wraping: ", 0f, dtwService.calculateDistance(target, sample3));
    }

    
    @Test
    public void testCalculateDistanceVector() {
        List<List<Float>> targetMatrix = createMatrix(target);
        Assert.assertEquals("dinamic time wraping: ", 2f, dtwService.calculateDistanceVector(targetMatrix,
                createMatrix(sample1)));
        Assert.assertEquals("dinamic time wraping: ", 11f, dtwService.calculateDistanceVector(targetMatrix,
                createMatrix(sample2)));
        Assert.assertEquals("dinamic time wraping: ", 0f, dtwService.calculateDistanceVector(targetMatrix,
                createMatrix(sample3)));
    }

    public static List<List<Float>> createMatrix(List<Float> vector) {
        List<List<Float>> targetMatrix = new ArrayList<List<Float>>();
        for (Float float1 : vector) {
            List<Float> column = new ArrayList<Float>();
            column.add(float1);
            targetMatrix.add(column);
        }

        return targetMatrix;
    }


    
    //Test
    public void testDtwPerformance() {
        StringBuilder sb = new StringBuilder();
        for (int i = 100; i < 1000; i+=100) {
            sb.append(performance(i)).append("\n");
        }
        debug("\n" + sb.toString());

    }

    public StringBuffer performance(int size){
		int offset = 2;
		StringBuffer sb = new StringBuffer();
		debug("Dtw calculation size:" + size);

		sb.append(size).append(",");
		List<Float> sampleP = new ArrayList<Float>();
		List<Float> targetP = new ArrayList<Float>();

		for (int i = 0; i < size; i++) {
			sampleP.add(Float.valueOf(i));
			if(i >= offset){
				targetP.add(Float.valueOf(i));
			}
		}

		long start = System.currentTimeMillis();



		start = System.currentTimeMillis();
		Float result = dtwService.calculateDistance(sampleP, targetP);
		long time = (System.currentTimeMillis() - start);
		debug("Dtw calculation time ms :" + time);
		Assert.assertTrue("dinamic time wraping : " ,!result.equals(Float.MAX_VALUE));
		debug("result: " + result );
		sb.append(time).append(",");



		return sb;

	}


    private void debug(String str) {
        System.out.println("[DtwTest]: " + str);
    }
}

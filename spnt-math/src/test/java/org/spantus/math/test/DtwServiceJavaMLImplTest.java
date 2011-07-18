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

    Double[] targetArr = new Double[]{1D, 2D, 3D, 4D, 5D};
    Double[][] sampleArr = new Double[][]{
        new Double[]{1D, 2D, 1D, 4D, 4D, 5D},
        new Double[]{2D, 3D, 5D, 7D, 8D, 9D},
        new Double[]{1D, 1D, 2D, 3D, 4D, 5D, 5D},
        new Double[]{10D, 20D, 30D, 40D, 50D}
    };
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
        Assert.assertEquals("dinamic time wraping: ", 2D, dtwService.calculateDistance(target, sample1));
        Assert.assertEquals("dinamic time wraping: ", 11D, dtwService.calculateDistance(target, sample2));
        Assert.assertEquals("dinamic time wraping: ", 0D, dtwService.calculateDistance(target, sample3));
    }
    
    @Test
    public void testfdtw(){
        Instance tsTarget = new DenseInstance(VectorUtils.todoubleArray(target));
        Instance tsSample = new DenseInstance(VectorUtils.todoubleArray(sample4));
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
        Assert.assertEquals("dinamic time wraping: ", 11D, dtwService.calculateDistance(target, sample2));
        Assert.assertEquals("dinamic time wraping: ", 0D, dtwService.calculateDistance(target, sample3));
    }

    
    @Test
    public void testCalculateDistanceVector() {
        List<List<Double>> targetMatrix = createMatrix(target);
        Assert.assertEquals("dinamic time wraping: ", 2D, dtwService.calculateDistanceVector(targetMatrix,
                createMatrix(sample1)));
        Assert.assertEquals("dinamic time wraping: ", 11D, dtwService.calculateDistanceVector(targetMatrix,
                createMatrix(sample2)));
        Assert.assertEquals("dinamic time wraping: ", 0D, dtwService.calculateDistanceVector(targetMatrix,
                createMatrix(sample3)));
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
		List<Double> sampleP = new ArrayList<Double>();
		List<Double> targetP = new ArrayList<Double>();

		for (int i = 0; i < size; i++) {
			sampleP.add(Double.valueOf(i));
			if(i >= offset){
				targetP.add(Double.valueOf(i));
			}
		}

		long start = System.currentTimeMillis();



		start = System.currentTimeMillis();
		Double result = dtwService.calculateDistance(sampleP, targetP);
		long time = (System.currentTimeMillis() - start);
		debug("Dtw calculation time ms :" + time);
		Assert.assertTrue("dinamic time wraping : " ,!result.equals(Double.MAX_VALUE));
		debug("result: " + result );
		sb.append(time).append(",");



		return sb;

	}


    private void debug(String str) {
        System.out.println("[DtwTest]: " + str);
    }
}

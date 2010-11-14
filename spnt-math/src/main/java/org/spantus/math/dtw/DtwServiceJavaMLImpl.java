package org.spantus.math.dtw;

import java.awt.Point;
import java.util.List;
import net.sf.javaml.distance.fastdtw.dtw.DTW;
import net.sf.javaml.core.DenseInstance;
import net.sf.javaml.core.Instance;
import net.sf.javaml.distance.fastdtw.dtw.ExpandedResWindow;
import net.sf.javaml.distance.fastdtw.dtw.FullWindow;
import net.sf.javaml.distance.fastdtw.dtw.LinearWindow;
import net.sf.javaml.distance.fastdtw.dtw.ParallelogramWindow;
import net.sf.javaml.distance.fastdtw.dtw.SearchWindow;
import net.sf.javaml.distance.fastdtw.dtw.TimeWarpInfo;
import net.sf.javaml.distance.fastdtw.dtw.WarpPath;
import net.sf.javaml.distance.fastdtw.matrix.ColMajorCell;
import net.sf.javaml.distance.fastdtw.timeseries.PAA;
import net.sf.javaml.distance.fastdtw.timeseries.TimeSeries;
import net.sf.javaml.distance.fastdtw.timeseries.TimeSeriesPoint;

/**
 * 
 * @author Mindaugas Greibus
 * 
 * @since 0.0.1
 * Created Apr 30, 2009
 *
 */
public class DtwServiceJavaMLImpl implements DtwService {

    private JavaMLSearchWindow searchWindow;
    private Integer searchRadius;
    public enum JavaMLSearchWindow{FullWindow, LinearWindow, ParallelogramWindow, ExpandedResWindow}


    public static final Integer DEFAULT_RADIUS = 3;

    public Float calculateDistance(List<Float> targetVector,
            List<Float> sampleVector) {

        TimeSeries tsTarget = toTimeSeries(targetVector);
        TimeSeries tsSample = toTimeSeries(sampleVector);

        Double info = null;
        if(getSearchWindow()==null){
           info = DTW.getWarpDistBetween(tsTarget, tsSample);
        }else{
           SearchWindow searchWindowInstance = createSearchWindow(tsTarget, tsSample);
           info = DTW.getWarpDistBetween(tsTarget, tsSample,  searchWindowInstance);
        }
        
        return info.floatValue();
    }


    public Float calculateDistanceVector(List<List<Float>> targetMatrix,
            List<List<Float>> sampleMatrix) {
        TimeSeries tsSample = toTimeSeries(sampleMatrix, sampleMatrix.get(0).size());
        TimeSeries tsTarget = toTimeSeries(targetMatrix, targetMatrix.get(0).size());

        Double info = null;
        if(getSearchWindow()==null){
           info = DTW.getWarpDistBetween(tsTarget, tsSample);
        }else{
           SearchWindow searchWindowInstance = createSearchWindow(tsTarget, tsSample);
           info = DTW.getWarpDistBetween(tsTarget, tsSample, searchWindowInstance);
        }
        return info.floatValue();

    }
    /**
     * 
     * @param targetVector
     * @param sampleVector
     * @return
     */
    public DtwResult calculateInfo(List<Float> targetVector, List<Float> sampleVector) {
        TimeSeries tsTarget = toTimeSeries(targetVector);
        TimeSeries tsSample = toTimeSeries(sampleVector);

        TimeWarpInfo info = null;
        if(getSearchWindow()==null){
           info = DTW.getWarpInfoBetween(tsTarget, tsSample);
        }else{
           SearchWindow searchWindowInstance = createSearchWindow(tsTarget, tsSample);
           info = DTW.getWarpInfoBetween(tsTarget, tsSample, searchWindowInstance);
        }

        
        
        DtwResult result =  new DtwResult();
        result.setResult(Double.valueOf(info.getDistance()).floatValue());
        for (int i=1; i<info.getPath().size()-1; i++){
            ColMajorCell cell = info.getPath().get(i);
            Point point = new Point(cell.getRow(), cell.getCol());
            result.getPath().add(point);
        }
        return result;
    }
    /**
     * 
     * @param targetMatrix
     * @param sampleMatrix
     * @return
     */
    public DtwResult calculateInfoVector(List<List<Float>> targetMatrix,
            List<List<Float>> sampleMatrix) {
        TimeSeries tsSample = toTimeSeries(sampleMatrix, sampleMatrix.get(0).size());
        TimeSeries tsTarget = toTimeSeries(targetMatrix, targetMatrix.get(0).size());
        
        TimeWarpInfo info = null;
        if(getSearchWindow()==null){
           info = DTW.getWarpInfoBetween(tsTarget, tsSample);
        }else{
           SearchWindow searchWindowInstance = createSearchWindow(tsTarget, tsSample);
           info = DTW.getWarpInfoBetween(tsTarget, tsSample,searchWindowInstance);
        }
        
        
        DtwResult result =  new DtwResult();
        result.setResult(Double.valueOf(info.getDistance()).floatValue());

        for (int i=1; i<info.getPath().size()-1; i++){
            ColMajorCell cell = info.getPath().get(i);
            Point point = new Point(cell.getRow(), cell.getCol());
            result.getPath().add(point);
        }
        return result;

    }

    /**
     *
     * @param values
     * @return
     */
    protected double[] toDoubleArray(List<Float> values) {
        double[] doubles = new double[values.size()];
        int i = 0;
        for (Float float1 : values) {
            doubles[i++] = float1.doubleValue();
        }
        return doubles;
    }

    public TimeSeries toTimeSeries(List<Float> values) {
        TimeSeries ts = new TimeSeries(new DenseInstance(toDoubleArray(values)));
        return ts;
    }

    protected TimeSeries toTimeSeries(List<List<Float>> matrix, int numOfDimensions) {
//        Instance instanceValues = new DenseInstance(toDoubleArray(values));
        TimeSeries ts = new TimeSeries(numOfDimensions);
        double i = 0;
        for (List<Float> list : matrix) {
            TimeSeriesPoint tsp = new TimeSeriesPoint(toDoubleArray(list));
            ts.addLast(i++, tsp);
        }
        return ts;
    }
    
    protected SearchWindow createSearchWindow(TimeSeries tsTarget, TimeSeries tsSample) {
        Integer radius = getSearchRadius();
        if(JavaMLSearchWindow.FullWindow.equals(getSearchWindow())){
            return new FullWindow(tsTarget, tsSample);
        }else if(JavaMLSearchWindow.LinearWindow.equals(getSearchWindow())){
            if(radius == null){
                return new LinearWindow(tsTarget, tsSample);
            }else{
                return new LinearWindow(tsTarget, tsSample, radius);
            }
        }else if(JavaMLSearchWindow.ParallelogramWindow.equals(getSearchWindow())){
            if(radius == null){
                throw new IllegalArgumentException("please set setSearchRadius()");
            }
            return new ParallelogramWindow(tsTarget, tsSample,  radius);
        }else if(JavaMLSearchWindow.ExpandedResWindow.equals(getSearchWindow())){
            if(radius == null){
                throw new IllegalArgumentException("please set setSearchRadius()");
            }
            PAA tsTargetPAA = new PAA(tsTarget, radius);
            PAA tsSamplePAA = new PAA(tsSample, radius);
            WarpPath warpPath = new WarpPath(radius); 
            return new ExpandedResWindow(tsTarget, tsSample, tsTargetPAA, tsSamplePAA,warpPath,  radius);
        }
        return null;
    }

    public JavaMLSearchWindow getSearchWindow() {
        return searchWindow;
    }

    public void setSearchWindow(JavaMLSearchWindow searchWindow) {
        this.searchWindow = searchWindow;
    }

    private Integer getSearchRadius() {
        return this.searchRadius;
    }
    public void setSearchRadius(Integer searchRadius) {
        this.searchRadius = searchRadius;
    }
    

}

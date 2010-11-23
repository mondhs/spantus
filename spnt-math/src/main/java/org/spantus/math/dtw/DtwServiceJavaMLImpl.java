package org.spantus.math.dtw;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import net.sf.javaml.distance.fastdtw.dtw.DTW;
import net.sf.javaml.core.DenseInstance;
import net.sf.javaml.distance.fastdtw.dtw.ExpandedResWindow;
import net.sf.javaml.distance.fastdtw.dtw.FullWindow;
import net.sf.javaml.distance.fastdtw.dtw.LinearWindow;
import net.sf.javaml.distance.fastdtw.dtw.ParallelogramWindow;
import net.sf.javaml.distance.fastdtw.dtw.SearchWindow;
import net.sf.javaml.distance.fastdtw.dtw.TimeWarpInfo;
import net.sf.javaml.distance.fastdtw.dtw.WarpPath;
import net.sf.javaml.distance.fastdtw.dtw.WarpPathWindow;
import net.sf.javaml.distance.fastdtw.matrix.ColMajorCell;
import net.sf.javaml.distance.fastdtw.timeseries.PAA;
import net.sf.javaml.distance.fastdtw.timeseries.TimeSeries;
import net.sf.javaml.distance.fastdtw.timeseries.TimeSeriesPoint;
import org.spantus.math.VectorUtils;

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

    public TimeSeries toTimeSeries(List<Float> values) {
        TimeSeries ts = new TimeSeries(new DenseInstance(VectorUtils.toDoubleArray(values)));
        return ts;
    }

    protected TimeSeries toTimeSeries(List<List<Float>> matrix, int numOfDimensions) {
//        Instance instanceValues = new DenseInstance(toDoubleArray(values));
        TimeSeries ts = new TimeSeries(numOfDimensions);
        double i = 0;
        for (List<Float> list : matrix) {
            TimeSeriesPoint tsp = new TimeSeriesPoint(VectorUtils.toDoubleArray(list));
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
            PAA shrunkTarget = new PAA(tsTarget, (int) Math.round(Math.sqrt((double) tsTarget.size())));
            PAA shrunkSample = new PAA(tsSample, (int) Math.round(Math.sqrt((double) tsSample.size())));

            WarpPath coarsePath = DTW.getWarpPathBetween(shrunkTarget, shrunkSample);
            WarpPath expandedPath = expandPath(coarsePath, shrunkTarget, shrunkSample);
            return new WarpPathWindow(expandedPath, radius);
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
    

    private static WarpPath expandPath(WarpPath path, PAA tsI, PAA tsJ) {
        final ArrayList iPoints = new ArrayList();
        final ArrayList jPoints = new ArrayList();

        iPoints.add(new Integer(0));
        jPoints.add(new Integer(0));
        int startI = 0; // tsI.aggregatePtSize(0);
        int startJ = 0; // tsJ.aggregatePtSize(0);
        if (path.get(1).getCol() != 0)
            startI = tsI.aggregatePtSize(0) - 1;
        else
            startI = (tsI.aggregatePtSize(0) - 1) / 2;

        if (path.get(1).getRow() != 0)
            startJ = tsJ.aggregatePtSize(0) - 1;
        else
            startJ = (tsJ.aggregatePtSize(0) - 1) / 2;

        int lastI = 0;
        int lastJ = 0;

        for (int x = 1; x < path.size() - 1; x++) {
            int currentI = path.get(x).getCol();
            int currentJ = path.get(x).getRow();

            if ((lastI != currentI)) {
                if (lastI == 0)
                    startI = tsI.aggregatePtSize(0) - 1;

                if (currentI == path.get(path.size() - 1).getCol())
                    startI -= tsI.aggregatePtSize(currentI) / 2;
                {
                    iPoints.add(new Integer(startI + tsI.aggregatePtSize(currentI) / 2));
                    startI += tsI.aggregatePtSize(currentI);
                }

                lastI = currentI;
            } else {
                iPoints.add(new Integer(startI));
            }

            if ((lastJ != currentJ)) {
                if (lastJ == 0)
                    startJ = tsJ.aggregatePtSize(0) - 1;

                if (currentJ == path.get(path.size() - 1).getRow())
                    startJ -= tsJ.aggregatePtSize(currentJ) / 2;
                {
                    jPoints.add(new Integer(startJ + tsJ.aggregatePtSize(currentJ) / 2));
                    startJ += tsJ.aggregatePtSize(currentJ);
                }

                lastJ = currentJ;
            } else {
                jPoints.add(new Integer(startJ));
            }
        } // end for loop

        iPoints.add(new Integer(tsI.originalSize() - 1));
        jPoints.add(new Integer(tsJ.originalSize() - 1));

        // Interpolate between coarse warp path points.
        final WarpPath expandedPath = new WarpPath();

        startI = 0;
        startJ = 0;
        int endI;
        int endJ;

        for (int p = 1; p < iPoints.size(); p++) {
            endI = ((Integer) iPoints.get(p)).intValue();
            endJ = ((Integer) jPoints.get(p)).intValue();
            expandedPath.addLast(startI, startJ);

            if ((endI - startI) >= (endJ - startJ)) {
                for (int i = startI + 1; i < endI; i++)
                    expandedPath.addLast(i, (int) Math.round(startJ + ((double) i - startI) / ((double) endI - startI)
                            * (endJ - startJ)));
            } else {
                for (int j = startJ + 1; j < endJ; j++)
                    expandedPath.addLast((int) Math.round(startI + ((double) j - startJ) / ((double) endJ - startJ)
                            * (endI - startI)), j);
            } // end if

            startI = endI;
            startJ = endJ;
        } // end for loop

        expandedPath.addLast(tsI.originalSize() - 1, tsJ.originalSize() - 1);
        return expandedPath;
    }

    
}

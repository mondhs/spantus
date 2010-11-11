package org.spantus.math.dtw;

import java.awt.Point;
import java.util.List;
import net.sf.javaml.distance.fastdtw.dtw.DTW;
import net.sf.javaml.core.DenseInstance;
import net.sf.javaml.core.Instance;
import net.sf.javaml.distance.fastdtw.dtw.TimeWarpInfo;
import net.sf.javaml.distance.fastdtw.matrix.ColMajorCell;
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

    public Float calculateDistance(List<Float> targetVector,
            List<Float> sampleVector) {

        TimeSeries tsTarget = toTimeSeries(targetVector);
        TimeSeries tsSample = toTimeSeries(sampleVector);

        Double info = DTW.getWarpDistBetween(tsSample, tsTarget);
        return info.floatValue();
    }


    public Float calculateDistanceVector(List<List<Float>> targetMatrix,
            List<List<Float>> sampleMatrix) {
        TimeSeries tsSample = toTimeSeries(sampleMatrix, sampleMatrix.get(0).size());
        TimeSeries tsTarget = toTimeSeries(targetMatrix, targetMatrix.get(0).size());

        Double info = DTW.getWarpDistBetween(tsSample, tsTarget);
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

        TimeWarpInfo info = DTW.getWarpInfoBetween(tsSample, tsTarget);
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

        TimeWarpInfo info = DTW.getWarpInfoBetween(tsSample, tsTarget);
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

    protected TimeSeries toTimeSeries(List<Float> values) {
        Instance instanceValues = new DenseInstance(toDoubleArray(values));
        TimeSeries ts = new TimeSeries(instanceValues);
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

}

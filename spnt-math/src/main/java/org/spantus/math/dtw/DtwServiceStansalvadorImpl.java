package org.spantus.math.dtw;

import java.util.List;

import fastdtw.dtw.DTW;
import fastdtw.dtw.TimeWarpInfo;
import fastdtw.timeseries.ITimeSeries;
import fastdtw.timeseries.ScalarValuesTimeSeries;
import fastdtw.timeseries.VectorValuesTimeSeries;
/**
 * 
 * @author Mindaugas Greibus
 * 
 * @since 0.0.1
 * Created Apr 30, 2009
 *
 */
public class DtwServiceStansalvadorImpl implements DtwService {

	public Float calculateDistance(List<Float> targetVector,
			List<Float> sampleVector) {
        ITimeSeries tsI = new ScalarValuesTimeSeries(sampleVector);
        ITimeSeries tsJ = new ScalarValuesTimeSeries(targetVector);
        TimeWarpInfo info = new DTW().getWarpInfoBetween(tsI, tsJ);
		return info.getDistance().floatValue();
	}

	public Float calculateDistance(DtwInfo info) {
		return null;
	}

	public Float calculateDistanceVector(List<List<Float>> targetMatrix,
			List<List<Float>> sampleMatrix) {
        ITimeSeries tsI = new VectorValuesTimeSeries(sampleMatrix);
        ITimeSeries tsJ = new VectorValuesTimeSeries(targetMatrix);
        TimeWarpInfo info = new DTW().getWarpInfoBetween(tsI, tsJ);
		return info.getDistance().floatValue();

	}


}

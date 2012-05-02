package org.spantus.math.services.javaml;

import java.util.Collection;
import java.util.List;

import org.spantus.math.VectorUtils;
import org.spantus.math.dtw.abeel.timeseries.TimeSeries;
import org.spantus.math.dtw.abeel.timeseries.TimeSeriesPoint;

public final class JavaMLSupport {
	
	private JavaMLSupport() {
	}

	public static TimeSeries toTimeSeries(List<Double> values) {
		TimeSeries ts = new TimeSeries(values);
		return ts;
	}

	public static TimeSeries toTimeSeries(List<List<Double>> matrix,
			int numOfDimensions) {
		TimeSeries ts = new TimeSeries(numOfDimensions);
		double i = 1;
		for (List<Double> values : matrix) {
			Collection<Double> doubles = VectorUtils.toDoubleList(values);
			TimeSeriesPoint tsp = new TimeSeriesPoint(doubles);
			ts.addLast(i++, tsp);
		}
		
		return ts;
	}
}

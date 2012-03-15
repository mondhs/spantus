package org.spantus.math.services.javaml;

import java.util.Collection;
import java.util.List;

import org.spantus.math.MatrixUtils;
import org.spantus.math.VectorUtils;

import net.sf.javaml.core.DenseInstance;
import net.sf.javaml.core.Instance;
import net.sf.javaml.core.SparseInstance;
import net.sf.javaml.distance.fastdtw.timeseries.TimeSeries;
import net.sf.javaml.distance.fastdtw.timeseries.TimeSeriesPoint;

public abstract class JavaMLSupport {
	public static Instance createInstanceScalars(List<Double> scalars) {
		Instance instance = new SparseInstance(scalars.size());
		int i = 0;
		for (Double f1 : scalars) {
			instance.put(i++, f1.doubleValue());
		}
		return instance;

	}

	public static Instance createInstanceVectors(List<List<Double>> vectors,
			int depth) {
		SparseInstance instance = new SparseInstance(vectors.size() * depth);
		int i = 0;
		for (List<Double> scalars : vectors) {
			for (Double f1 : scalars) {
				instance.put(i++, f1.doubleValue());
			}
		}

		return instance;
	}

	public static TimeSeries toTimeSeries(List<Double> values) {
		Instance instance = new DenseInstance(values.size());
		int i = 0;
		for (Double f1 : values) {
			instance.put(i++, f1.doubleValue());
		}
		TimeSeries ts = new TimeSeries(instance);
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
		
//		System.out.println(ts);
		return ts;
	}
}

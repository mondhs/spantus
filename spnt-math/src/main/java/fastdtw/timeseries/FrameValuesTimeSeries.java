package fastdtw.timeseries;

import java.util.ArrayList;
import java.util.List;

public class FrameValuesTimeSeries implements ITimeSeries {
	List<Float> values;
	
	public FrameValuesTimeSeries(List<Float> values) {
		this.values = new ArrayList<Float>(values);
	}

	public void clear() {
	}

	public double[] getMeasurementVector(int pointIndex) {
		return new double[]{values.get(pointIndex).doubleValue()};
	}

	public Double getTimeAtNthPoint(int n) {
		return (double)n;
	}

	public int numOfDimensions() {
		return 1;
	}

	public int size() {
		return values.size();
	}

}

package fastdtw.timeseries;

import java.util.ArrayList;
import java.util.List;

public class VectorValuesTimeSeries implements ITimeSeries {
	List<List<Float>> values;
	
	public VectorValuesTimeSeries(List<List<Float>> values) {
		this.values = new ArrayList<List<Float>>(values);
	}

	public void clear() {
	}

	public List<Float> getMeasurementVector(int pointIndex) {
		return values.get(pointIndex);
	}

	public Float getTimeAtNthPoint(int n) {
		return (float)n;
	}

	public int numOfDimensions() {
		return values.iterator().next().size();
	}

	public int size() {
		return values.size();
	}

}

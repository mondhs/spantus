package fastdtw.timeseries;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class ScalarValuesTimeSeries implements ITimeSeries {
	List<Float> values;
	
	public ScalarValuesTimeSeries(List<Float> values) {
		this.values = new ArrayList<Float>(values);
	}

	public List<Float> getMeasurementVector(int pointIndex) {
		List<Float> flist = new LinkedList<Float>();
		flist.add(values.get(pointIndex));
		return flist;
	}

	public Float getTimeAtNthPoint(int n) {
		return (float)n;
	}

	public int numOfDimensions() {
		return 1;
	}

	public int size() {
		return values.size();
	}

}

package fastdtw.timeseries;

import java.util.List;

public interface ITimeSeries {

//	public abstract void clear();

	public abstract int size();
	
	public abstract List<Float> getMeasurementVector(int pointIndex);
	
	public abstract int numOfDimensions() ;
	
	public abstract Float getTimeAtNthPoint(int n);

}
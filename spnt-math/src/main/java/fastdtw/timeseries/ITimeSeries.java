package fastdtw.timeseries;

public interface ITimeSeries {

//	public abstract void clear();

	public abstract int size();
	
	public abstract double[] getMeasurementVector(int pointIndex);
	
	public abstract int numOfDimensions() ;
	
	public abstract Double getTimeAtNthPoint(int n);

}
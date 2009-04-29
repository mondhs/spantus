package fastdtw;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;
import fastdtw.dtw.DTW;
import fastdtw.dtw.FastDTW;
import fastdtw.dtw.TimeWarpInfo;
import fastdtw.timeseries.FrameValuesTimeSeries;
import fastdtw.timeseries.ITimeSeries;

public class FrameValueDTWTest extends TestCase {
	public void testDTW(){
		List<Float> sampleFV = createList(new Float[]{1F,1F,0F,1F,2F,3F,2F,1F,0F,0F});
		List<Float> targetFV = createList(new Float[]{0F,0F,1F,2F,3F,2F,1F,1F,1F,1F,1F});
        ITimeSeries tsI = new FrameValuesTimeSeries(sampleFV);
        ITimeSeries tsJ = new FrameValuesTimeSeries(targetFV);
        TimeWarpInfo info = new DTW().getWarpInfoBetween(tsI, tsJ);
        assertEquals(4D, info.getDistance());
	}
	public void _testFastDTW(){
		List<Float> sampleFV = createList(new Float[]{1F,1F,0F,1F,2F,3F,2F,1F,0F,0F});
		List<Float> targetFV = createList(new Float[]{0F,0F,1F,2F,3F,2F,1F,1F,1F,1F,1F});
        ITimeSeries tsI = new FrameValuesTimeSeries(sampleFV);
        ITimeSeries tsJ = new FrameValuesTimeSeries(targetFV);
        TimeWarpInfo info = new FastDTW().getWarpInfoBetween(tsI, tsJ, 2);
        assertEquals(4D, info.getDistance());
	}
	
	protected List<Float> createList(Float[] farr){
		List<Float> list=new ArrayList<Float>();
		for (Float float1 : farr) {
			list.add(float1);
		}
		return list;
		
	}
}

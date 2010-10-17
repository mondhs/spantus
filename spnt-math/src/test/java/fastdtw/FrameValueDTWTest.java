package fastdtw;


import junit.framework.Assert;
import net.sf.javaml.core.DenseInstance;
import net.sf.javaml.core.Instance;
import net.sf.javaml.distance.fastdtw.dtw.DTW;
import net.sf.javaml.distance.fastdtw.dtw.TimeWarpInfo;
import net.sf.javaml.distance.fastdtw.timeseries.TimeSeries;
import org.junit.Before;
import org.junit.Test;

public class FrameValueDTWTest{
    private TimeSeries sampleFV;
    private TimeSeries targetFV;
    @Before
    public void setup(){
        sampleFV = createTimeSeries(new double[]{1.0, 1.0, 0.0, 1.0, 2.0, 3.0, 2.0, 1.0, 0.0, 0.0});
        targetFV = createTimeSeries(new double[]{0.0, 0.0, 1.0, 2.0, 3.0, 2.0, 1.0, 1.0, 1.0, 1.0, 1.0});
    }
    @Test
    public void testDTW() {
        TimeWarpInfo info = DTW.getWarpInfoBetween(targetFV, sampleFV);
        Assert.assertEquals(4D, info.getDistance());
    }
    @Test
    public void testFastDTW() {
        TimeWarpInfo info = net.sf.javaml.distance.fastdtw.dtw.FastDTW.getWarpInfoBetween(targetFV, sampleFV, 2);
        Assert.assertEquals(4D, info.getDistance());
    }

    protected TimeSeries createTimeSeries(double[] values) {
        Instance instanceValues = new DenseInstance(values);
        TimeSeries ts = new TimeSeries(instanceValues);
        return ts;

    }
}

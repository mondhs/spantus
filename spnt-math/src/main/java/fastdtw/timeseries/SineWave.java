// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   SineWave.java

package fastdtw.timeseries;

import java.util.Random;

// Referenced classes of package timeseries:
//            TimeSeries, TimeSeriesPoint

public class SineWave extends TimeSeries
{

    public SineWave(int length, double cycles, double noise)
    {
        super(1);
        for(int x = 0; x < length; x++)
        {
            double nextPoint = Math.sin(((double)x / (double)length) * 2D * 3.1415926535897931D * cycles) + rand.nextGaussian() * noise;
            super.addLast(x, new TimeSeriesPoint(new double[] {
                nextPoint
            }));
        }

    }

    private static final Random rand = new Random();

}

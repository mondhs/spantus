// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   LinearWindow.java

package fastdtw.dtw;

import fastdtw.timeseries.ITimeSeries;

// Referenced classes of package dtw:
//            SearchWindow

public class LinearWindow extends SearchWindow
{

    public LinearWindow(ITimeSeries tsI, ITimeSeries tsJ, int searchRadius)
    {
        super(tsI.size(), tsJ.size());
        double ijRatio = (double)tsI.size() / (double)tsJ.size();
        boolean isIlargest = tsI.size() >= tsJ.size();
        for(int i = 0; i < tsI.size(); i++)
            if(isIlargest)
            {
                int j = Math.min((int)Math.round((double)i / ijRatio), tsJ.size() - 1);
                super.markVisited(i, j);
            } else
            {
                int maxJ = (int)Math.round((double)(i + 1) / ijRatio) - 1;
                int minJ = (int)Math.round((double)i / ijRatio);
                super.markVisited(i, minJ);
                super.markVisited(i, maxJ);
            }

        super.expandWindow(searchRadius);
    }

    public LinearWindow(ITimeSeries tsI, ITimeSeries tsJ)
    {
        this(tsI, tsJ, 0);
    }

    private static final int defaultRadius = 0;
}

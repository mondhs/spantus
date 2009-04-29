// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   FullWindow.java

package fastdtw.dtw;

import fastdtw.timeseries.ITimeSeries;

// Referenced classes of package dtw:
//            SearchWindow

public class FullWindow extends SearchWindow
{

    public FullWindow(ITimeSeries tsI, ITimeSeries tsJ)
    {
        super(tsI.size(), tsJ.size());
        for(int i = 0; i < tsI.size(); i++)
        {
            super.markVisited(i, minJ());
            super.markVisited(i, maxJ());
        }

    }
}

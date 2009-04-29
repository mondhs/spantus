// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   ParallelogramWindow.java

package fastdtw.dtw;

import fastdtw.timeseries.ITimeSeries;

// Referenced classes of package dtw:
//            SearchWindow

public class ParallelogramWindow extends SearchWindow
{

    public ParallelogramWindow(ITimeSeries tsI, ITimeSeries tsJ, int searchRadius)
    {
        super(tsI.size(), tsJ.size());
        double upperCornerI = Math.max((double)maxI() / 2D - (double)searchRadius * ((double)maxI() / (double)maxJ()), minI());
        double upperCornerJ = Math.min((double)maxJ() / 2D + (double)searchRadius * ((double)maxJ() / (double)maxI()), maxJ());
        double lowerCornerI = Math.min((double)maxI() / 2D + (double)searchRadius * ((double)maxI() / (double)maxJ()), maxI());
        double lowerCornerJ = Math.max((double)maxJ() / 2D - (double)searchRadius * ((double)maxJ() / (double)maxI()), minJ());
        for(int i = 0; i < tsI.size(); i++)
        {
            boolean isIlargest = tsI.size() >= tsJ.size();
            int maxJ;
            if((double)i < upperCornerI)
            {
                if(isIlargest)
                {
                    double interpRatio = (double)i / upperCornerI;
                    maxJ = (int)Math.round(interpRatio * upperCornerJ);
                } else
                {
                    double interpRatio = (double)(i + 1) / upperCornerI;
                    maxJ = (int)Math.round(interpRatio * upperCornerJ) - 1;
                }
            } else
            if(isIlargest)
            {
                double interpRatio = ((double)i - upperCornerI) / ((double)maxI() - upperCornerI);
                maxJ = (int)Math.round(upperCornerJ + interpRatio * ((double)maxJ() - upperCornerJ));
            } else
            {
                double interpRatio = ((double)(i + 1) - upperCornerI) / ((double)maxI() - upperCornerI);
                maxJ = (int)Math.round(upperCornerJ + interpRatio * ((double)maxJ() - upperCornerJ)) - 1;
            }
            int minJ;
            if((double)i <= lowerCornerI)
            {
                double interpRatio = (double)i / lowerCornerI;
                minJ = (int)Math.round(interpRatio * lowerCornerJ);
            } else
            {
                double interpRatio = ((double)i - lowerCornerI) / ((double)maxI() - lowerCornerI);
                minJ = (int)Math.round(lowerCornerJ + interpRatio * ((double)maxJ() - lowerCornerJ));
            }
            super.markVisited(i, minJ);
            super.markVisited(i, maxJ);
        }

    }
}

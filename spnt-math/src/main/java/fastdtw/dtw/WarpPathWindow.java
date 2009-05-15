// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   WarpPathWindow.java

package fastdtw.dtw;


// Referenced classes of package dtw:
//            SearchWindow, WarpPath

public class WarpPathWindow extends SearchWindow
{

    public WarpPathWindow(WarpPath path, int searchRadius)
    {
        super(path.get(path.size() - 1).getCol() + 1, path.get(path.size() - 1).getRow() + 1);
        for(int p = 0; p < path.size(); p++)
            super.markVisited(path.get(p).getCol(), path.get(p).getRow());

        super.expandWindow(searchRadius);
    }

//    private static final int defaultRadius = 0;
}

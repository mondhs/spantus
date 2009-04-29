// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   FastDTW.java

package fastdtw.dtw;

import fastdtw.timeseries.ITimeSeries;
import fastdtw.timeseries.PAA;
import fastdtw.timeseries.TimeSeries;

// Referenced classes of package dtw:
//            ExpandedResWindow, TimeWarpInfo, DTW, WarpPath

public class FastDTW {
	static final int DEFAULT_SEARCH_RADIUS = 1;
	private DTW dtw;

	public FastDTW() {
	}

	public double getWarpDistBetween(TimeSeries tsI, TimeSeries tsJ) {
		return fastDTW(tsI, tsJ, 1).getDistance();
	}

	public double getWarpDistBetween(TimeSeries tsI, TimeSeries tsJ,
			int searchRadius) {
		return fastDTW(tsI, tsJ, searchRadius).getDistance();
	}

	public WarpPath getWarpPathBetween(TimeSeries tsI, TimeSeries tsJ) {
		return fastDTW(tsI, tsJ, 1).getPath();
	}

	public WarpPath getWarpPathBetween(TimeSeries tsI, TimeSeries tsJ,
			int searchRadius) {
		return fastDTW(tsI, tsJ, searchRadius).getPath();
	}

	public TimeWarpInfo getWarpInfoBetween(ITimeSeries tsI, ITimeSeries tsJ,
			int searchRadius) {
		return fastDTW(tsI, tsJ, searchRadius);
	}

	private TimeWarpInfo fastDTW(ITimeSeries tsI, ITimeSeries tsJ,
			int searchRadius) {
		if (searchRadius < 0)
			searchRadius = 0;
		int minTSsize = searchRadius + 2;
		if (tsI.size() <= minTSsize || tsJ.size() <= minTSsize) {
			return getDtw().getWarpInfoBetween(tsI, tsJ);
		} else {
			// double resolutionFactor = 2D;
			PAA shrunkI = new PAA(tsI, (int) ((double) tsI.size() / 2D));
			PAA shrunkJ = new PAA(tsJ, (int) ((double) tsJ.size() / 2D));
			SearchWindow window = new ExpandedResWindow(tsI, tsJ, shrunkI,
					shrunkJ,
					getWarpPathBetween(shrunkI, shrunkJ, searchRadius),
					searchRadius);
			return getDtw().getWarpInfoBetween(tsI, tsJ, window);
		}
	}

	public DTW getDtw() {
		if (dtw == null) {
			dtw = new DTW();
		}
		return dtw;
	}

}

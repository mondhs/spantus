// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   TimeWarpInfo.java

package fastdtw.dtw;

// Referenced classes of package dtw:
//            WarpPath

public class TimeWarpInfo {
	
	private final Double distance;
	private final WarpPath path;

	TimeWarpInfo(Double dist, WarpPath wp) {
		distance = dist;
		path = wp;
	}

	public Double getDistance() {
		return distance;
	}

	public WarpPath getPath() {
		return path;
	}

	public String toString() {
		return "(Warp Distance=" + distance + ", Warp Path=" + path + ")";
	}

}

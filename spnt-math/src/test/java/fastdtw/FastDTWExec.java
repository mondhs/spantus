package fastdtw;

/*
 * @(#)FastDTW.java   Jul 14, 2004
 *
 * PROJECT DESCRIPTION
 */

import fastdtw.dtw.FastDTW;
import fastdtw.dtw.TimeWarpInfo;
import fastdtw.timeseries.ITimeSeries;
import fastdtw.timeseries.TimeSeries;

/**
 * @author Stan Salvador, stansalvador@hotmail.com
 * @since Jul 14, 2004
 */

public class FastDTWExec {

	// PUBLIC FUNCTIONS
	public static void main(String[] args) {
		if (args.length != 3) {
			System.out
					.println("USAGE:  java FastDTW timeSeries1 timeSeries2 radius");
			System.exit(1);
		} else {
			ITimeSeries tsI = new TimeSeries(args[0], false, false, ',');
			ITimeSeries tsJ = new TimeSeries(args[1], false, false, ',');
			final TimeWarpInfo info = new FastDTW().getWarpInfoBetween(
					tsI, tsJ, Integer.parseInt(args[2]));

			System.out.println("Warp Distance: " + info.getDistance());
			System.out.println("Warp Path:     " + info.getPath());
		} // end if

	} // end main()

} 

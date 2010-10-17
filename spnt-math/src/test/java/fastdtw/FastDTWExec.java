package fastdtw;

import net.sf.javaml.distance.fastdtw.dtw.TimeWarpInfo;
import net.sf.javaml.distance.fastdtw.timeseries.TimeSeries;

/*
 * @(#)FastDTW.java   Jul 14, 2004
 *
 * PROJECT DESCRIPTION
 */
/**
 * @author Stan Salvador, stansalvador@hotmail.com
 * @since Jul 14, 2004
 */
public class FastDTWExec {

    // PUBLIC FUNCTIONS
    public static void main(String[] args) {
        if (args.length != 3) {
            System.out.println("USAGE:  java FastDTW timeSeries1 timeSeries2 radius");
            System.exit(1);
        } else {
            TimeSeries tsI = new TimeSeries(args[0], false, false, ',');
            TimeSeries tsJ = new TimeSeries(args[1], false, false, ',');
            final TimeWarpInfo info = net.sf.javaml.distance.fastdtw.dtw.FastDTW.getWarpInfoBetween(
                    tsI, tsJ, Integer.parseInt(args[2]));

            System.out.println("Warp Distance: " + info.getDistance());
            System.out.println("Warp Path:     " + info.getPath());
        } // end if

    } // end main()
}

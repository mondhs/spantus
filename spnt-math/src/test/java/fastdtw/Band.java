package fastdtw;

import net.sf.javaml.distance.fastdtw.dtw.DTW;
import net.sf.javaml.distance.fastdtw.dtw.LinearWindow;
import net.sf.javaml.distance.fastdtw.dtw.TimeWarpInfo;
import net.sf.javaml.distance.fastdtw.timeseries.TimeSeries;

/*
 * @(#)Band.java   Jul 14, 2004
 *
 * PROJECT DESCRIPTION
 */


/**
 * @author Stan Salvador, stansalvador@hotmail.com
 * @since Jul 14, 2004
 */

public class Band
{

      // PUBLIC FUNCTIONS
      public static void main(String[] args)
      {
         if (args.length != 3)
         {
            System.out.println("USAGE:  java Band timeSeries1 timeSeries2 radius");
            System.exit(1);
         }
         else
         {
            final TimeSeries tsI = new TimeSeries(args[0], false, false, ',');
            final TimeSeries tsJ = new TimeSeries(args[1], false, false, ',');
            final TimeWarpInfo info = DTW.getWarpInfoBetween(tsI, tsJ, new LinearWindow(tsI, tsJ, Integer.parseInt(args[2])));

            System.out.println("Warp Distance: " + info.getDistance());
            System.out.println("Warp Path:     " + info.getPath());
         }  // end if

      }  // end main()


}  // end class Band

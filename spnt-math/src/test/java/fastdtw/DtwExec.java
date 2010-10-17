package fastdtw;
/*
 * @(#)DTW.java   Jul 14, 2004
 *
 * PROJECT DESCRIPTION
 */

import net.sf.javaml.distance.fastdtw.dtw.DTW;
import net.sf.javaml.distance.fastdtw.dtw.TimeWarpInfo;
import net.sf.javaml.distance.fastdtw.timeseries.TimeSeries;


public class DtwExec
{

   // PUBLIC FUNCTIONS
   public static void main(String[] args)
   {
      if (args.length != 2)
      {
         System.out.println("USAGE:  java DTW timeSeries1 timeSeries2");
         System.exit(1);
      }
      else
      {
         final TimeSeries tsI = new TimeSeries(args[0], false, false, ',');
         final TimeSeries tsJ = new TimeSeries(args[1], false, false, ',');
         final TimeWarpInfo info = DTW.getWarpInfoBetween(tsI, tsJ);

         System.out.println("Warp Distance: " + info.getDistance());
         System.out.println("Warp Path:     " + info.getPath());
      }  // end if

   }  // end main()

}  // end class DTW

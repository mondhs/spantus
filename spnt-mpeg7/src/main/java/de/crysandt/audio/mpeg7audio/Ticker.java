/*
  Copyright (c) 2002-2003, Holger Crysandt
 
  This file is part of the MPEG7AudioEnc project.
*/

package de.crysandt.audio.mpeg7audio;

import java.io.PrintStream;
import java.text.DecimalFormat;

/**
 * @author <a href="mailto:crysandt@ient.rwth-aachen.de">Holger Crysandt</a>
 */
public class Ticker
    implements TimeElapsedListener
{
  private final PrintStream out;
  private int time_old = 0;
  private final DecimalFormat format = new DecimalFormat( "00" );

  public Ticker(PrintStream out) {
    this.out = out;
    printTime(0);
  }

  public Ticker( ) {
    this(System.err);
  }

  public void timeElapsed(int time_elapsed) {
    time_elapsed /= 1000;
    if( time_old != time_elapsed )
      printTime( time_elapsed );
    time_old = time_elapsed;
  }

  private void printTime( int time ) {
    out.print("" +
              format.format(time / 60) + ":" +
              format.format(time % 60) + "\r");
  }
}

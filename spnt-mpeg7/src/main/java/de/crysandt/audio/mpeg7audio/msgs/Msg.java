/*
  Copyright (c) 2002-2003, Holger Crysandt

  This file is part of the MPEG7AudioEnc project.
*/
package de.crysandt.audio.mpeg7audio.msgs;

/**
 * @author <a href="mailto:crysandt@ient.rwth-aachen.de">Holger Crysandt</a>
 */

public class Msg
    implements Comparable<Msg>
{
  public final int time;
  public final int duration;
  public final int hopsize;

  public Msg(int time, int duration, int hopsize) {
    super();
    this.time     = time;
    this.duration = duration;
    this.hopsize  = hopsize;
  }

  public Msg(int time, int duration) {
    this(time, duration, duration);
  }

  final public int compareTo(Msg o) {
    return time - o.time;
  }

  public String toString() {
    return "; time: "     + time +
           "; duration: " + duration +
           "; hopsize: "  + hopsize;
  }
}
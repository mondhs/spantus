/*
  Copyright (c) 2002-2003, Holger Crysandt

  This file is part of the MPEG7AudioEnc project.
*/
package de.crysandt.audio.mpeg7audio.msgs;

/**
 * @author <a href="mailto:crysandt@ient.rwth-aachen.de">Holger Crysandt</a>
 */
public class MsgResizer
    extends Msg
{
  private final float[] signal;
  public int channels;

  public MsgResizer(int time        /** [ms] */,
                    int duration,   /** [ms] */
                    float[] signal /** audio signal */ )
  {
    super(time, duration);
    this.signal = signal;
  }

  public String toString() {
    return super.toString() + "; signal.length: (" + signal.length + ")";
  }

  public float[] getSignal() {
    return signal;
  }

  public int getSignalLength() {
    return signal.length;
  }

}
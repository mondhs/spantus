/*
  Copyright (c) 2002-2003, Holger Crysandt
  
  This file is part of the MPEG7AudioEnc project.
*/

package de.crysandt.audio.mpeg7audio.msgs;

/**
 * @author <a href="mailto:crysandt@ient.rwth-aachen.de">Holger Crysandt</a>
 */
public class MsgAudioWaveform
    extends Msg
{
  public final float min;
  public final float max;

  public MsgAudioWaveform( int time, int duration, float min, float max) {
    super(time, duration );
    this.min = min;
    this.max = max;
  }

  public String toString() {
    return super.toString() + "; minimum: " + min + "; maximum: " + max;
  }
}
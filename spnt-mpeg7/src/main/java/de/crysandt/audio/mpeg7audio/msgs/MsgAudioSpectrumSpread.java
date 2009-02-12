/*
  Copyright (c) 2002-2003, Holger Crysandt

  This file is part of the MPEG7AudioEnc project.
*/

package de.crysandt.audio.mpeg7audio.msgs;

/**
 * @author <a href="mailto:crysandt@ient.rwth-aachen.de">Holger Crysandt</a>
 */
public class MsgAudioSpectrumSpread
    extends Msg
{
  public final float spread;

  public MsgAudioSpectrumSpread(int time, int duration, float spread) {
    super(time, duration );
    this.spread = spread;
  }
}
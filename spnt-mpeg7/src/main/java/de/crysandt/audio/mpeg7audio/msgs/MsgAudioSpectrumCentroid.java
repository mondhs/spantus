/*
  Copyright (c) 2002-2003, Holger Crysandt

  This file is part of the MPEG7AudioEnc project.
*/

package de.crysandt.audio.mpeg7audio.msgs;

/**
 * @author <a href="mailto:crysandt@ient.rwth-aachen.de">Holger Crysandt</a>
 */
public class MsgAudioSpectrumCentroid
    extends Msg
{

  public final float centroid;

  public MsgAudioSpectrumCentroid(int time, int duration, float centroid) {
    super(time, duration);
    this.centroid = centroid;
  }

  public String toString() {
    return super.toString() + "; centroid: " + centroid;
  }
}
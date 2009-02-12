/*
  Copyright (c) 2002-2003, Holger Crysandt

  This file is part of the MPEG7AudioEnc project.
*/

package de.crysandt.audio.mpeg7audio.msgs;

public class MsgAudioSpectrumDistribution
    extends Msg
{
  private final float[] distribution;

  public final float lo_edge;
  public final float hi_edge;
  public final float resolution;

  public MsgAudioSpectrumDistribution(int time,
                                      int duration,
                                      int hopsize,
                                      float[] distribution,
                                      float   lo_edge,
                                      float   hi_edge,
                                      float   resolution )
  {
    super(time, duration, hopsize);
    this.distribution = distribution;
    this.lo_edge = lo_edge;
    this.hi_edge = hi_edge;
    this.resolution = resolution;
  }

  public int getDistributionLength() {
    return distribution.length;
  }

  public float[] getDistribution() {
    float[] tmp = new float[distribution.length];
    System.arraycopy(distribution, 0, tmp, 0, distribution.length);
    return tmp;
  }
}

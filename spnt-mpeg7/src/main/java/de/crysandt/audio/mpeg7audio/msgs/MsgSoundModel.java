/*
  Copyright (c) 2002-2006, Holger Crysandt

  This file is part of the MPEG7AudioEnc project.
*/
package de.crysandt.audio.mpeg7audio.msgs;

import de.crysandt.hmm.HMM;

/**
 * @author <a href="crysandt@ient.rwth-aachen.de>Holger Crysandt</a>
 */
public class MsgSoundModel
    extends Msg
{
  public final float lo_edge;
  public final float hi_edge;
  public final float resolution;
  public final float[][] audio_spectrum_basis;

  public final HMM hmm;
  public final String label;

  public MsgSoundModel(int time,
                       int duration,
							  int hop_size, 
                       float lo_edge,
                       float hi_edge,
                       float resolution,
                       float[][] audio_spectrum_basis,
                       HMM hmm,
                       String label)
  {
    super(time, duration, hop_size);
    this.lo_edge = lo_edge;
    this.hi_edge = hi_edge;
    this.resolution = resolution;
    this.audio_spectrum_basis = audio_spectrum_basis;
    this.hmm = hmm;
    this.label = label;
  }
}
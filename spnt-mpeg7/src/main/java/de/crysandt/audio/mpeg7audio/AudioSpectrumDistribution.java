/*
  Copyright (c) 2002-2004, Holger Crysandt

  This file is part of the MPEG7AudioEnc project.
*/

package de.crysandt.audio.mpeg7audio;

import java.util.*;

import de.crysandt.audio.mpeg7audio.msgs.*;

/**
 * Gives the distribution of the power between the subbands. Similar to
 * the AudioSpectrumEnvelope. (Not part of the MPEG-7 standard.
 *
 * @author <a href="mailto:crysandt@ient.rwth-aachen.de">Holger Crysandt</a>
 */
class AudioSpectrumDistribution
    extends MsgSpeaker
    implements MsgListener
{
  public void receivedMsg(Msg msg) {
    MsgAudioSpectrumEnvelope mase = (MsgAudioSpectrumEnvelope) msg;

    float[] envelope = mase.getEnvelope();

    double sum = 0.0;
    for (int i=0; i<envelope.length; ++i)
      sum += envelope[i];

    float[] distribution = new float[envelope.length];

    if (sum == 0.0) {
      Arrays.fill(distribution, 1.0f / envelope.length);
    }else {
      for (int i = 0; i < envelope.length; ++i)
        distribution[i] = (float) (envelope[i] / sum);
    }

    send(new MsgAudioSpectrumDistribution(mase.time,
                                          mase.duration,
                                          mase.hopsize,
                                          distribution,
                                          mase.lo_edge,
                                          mase.hi_edge,
                                          mase.resolution));
  }
}

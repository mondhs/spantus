/*
  Copyright (c) 2002-2003, Holger Crysandt

  This file is part of the MPEG7AudioEnc project.
*/

package de.crysandt.audio.mpeg7audio;

import de.crysandt.audio.mpeg7audio.msgs.*;
import de.crysandt.math.Function;

/**
 * @author <a href="mailto:crysandt@ient.rwth-aachen.de">Holger Crysandt</a>
 */
class AudioSpectrumFlatness
    extends MsgSpeaker
    implements MsgListener
{
  private static final float RESOLUTION = 0.25f;
  private static final float OVERLAP = 0.05f;

  private float LO_EDGE;
  private float HI_EDGE;

  public AudioSpectrumFlatness(float samplerate,
                               float lo_edge,
                               float hi_edge) {
    LO_EDGE = lo_edge;
    HI_EDGE = hi_edge;

    while (samplerate / 2.0 < HI_EDGE)
      HI_EDGE /= 2.0f;
  }

  public AudioSpectrumFlatness(float lo_edge, float hi_edge) {
    this.LO_EDGE = lo_edge;
    this.HI_EDGE = hi_edge;
  }
  public void receivedMsg(Msg msg){
	  if (msg instanceof MsgAudioSpectrum)
		  receivedMsg((MsgAudioSpectrum) msg);
	}

  public void receivedMsg(MsgAudioSpectrum mas) {
    float[] spectrum = mas.getAudioSpectrum();
    float delta_f = mas.deltaF;

    int num_bands = (int) (Function.log2(HI_EDGE / LO_EDGE) / RESOLUTION);
    float[] flatness = new float[num_bands];

    final float band_factor = (float)Math.pow(2.0, RESOLUTION);

    float freq;
    float freq_lo; // low freq of sub-band
    float freq_hi; // high freq of sub-band

    int i_lo; // low index
    int i_hi; // high index

    float am; // arithmetic mean
    float gm; // geometric mean

    float[] ps; // sub power spectrum

    int m = (int) (Function.log2(LO_EDGE / 1000.0f) / RESOLUTION);
    for (int l = 0; l < flatness.length; ++m, ++l) {
      freq = (float)(1000.0 * Math.pow(2.0, m * RESOLUTION));

      freq_lo = (1 - OVERLAP) * freq;
      freq_hi = Math.min((1 + OVERLAP) * freq * band_factor, HI_EDGE);

      i_lo = Math.round(freq_lo / delta_f);
      i_hi = Math.round(freq_hi / delta_f) + 1;

      if (m < 0) {
        ps = new float[i_hi - i_lo];
        System.arraycopy(spectrum, i_lo, ps, 0, ps.length);
      } else {
        int grp = (int) Math.pow(2.0, 1.0 + Math.floor(m / 4));
        int num = Math.round((float) (i_hi - i_lo) / (float) grp);

        ps = new float[num];
        for (int n = 0; n < ps.length; ++n) {
          ps[n] = spectrum[i_lo + n * grp];
          for (int g = 1; g < grp; ++g) {
            try {
              ps[n] += spectrum[i_lo + n * grp + g];
            } catch (ArrayIndexOutOfBoundsException e) {}
          }
        }
      }

      am = Function.mean_arith(ps);
      if (am > 0.0) {
        for (int n = 0; n < ps.length; ++n)
          ps[n] /= am;
        am = 1.0f;
        gm = Function.mean_geom(ps);
        flatness[l] = gm / am;
      } else {
        flatness[l] = 1.0f;
      }
    }

    send(new MsgAudioSpectrumFlatness(mas.time,
                                      mas.hopsize,
                                      flatness,
                                      LO_EDGE,
                                      HI_EDGE));
  }
}

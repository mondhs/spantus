/*
  Copyright (c) 2002-2006, Holger Crysandt
 
  This file is part of the MPEG7AudioEnc project.
*/


package de.crysandt.audio.mpeg7audio;

import de.crysandt.audio.mpeg7audio.msgs.*;

/**
 * @author <a href="mailto:crysandt@ient.rwth-aachen.de">Holger Crysandt</a>
 */
class Resizer
    extends MsgSpeaker
    implements MsgListener
{
  private final float samplerate;
  private final int duration;

  private int index = 0;
  private float[] signal_old = null;
  private int totalsignalprocessed = 0;

  /**
   * @param samplerate Samplerate of the audio source [Hz]
   * @param duration Length of each frame [ms]
   */
  public Resizer(float samplerate, int duration) {
    this.samplerate = samplerate;
    this.duration = duration;
  }

  public void receivedMsg(Msg msg) {
    try {
      put(((MsgRawSignal) msg).signal);
    } catch (ClassCastException e) {
      send(new MsgEndOfSignal(
          Math.round(1000 * totalsignalprocessed / samplerate), 0));
    }
  }

  private void put(float[] signal) {
    // keep the count of the samples sent, useful for the end of signal message
    totalsignalprocessed += signal.length;

    float[] s;
    if (signal_old != null) {
      s = new float[signal_old.length + signal.length];
      System.arraycopy(signal_old, 0, s, 0, signal_old.length);
      System.arraycopy(signal, 0, s, signal_old.length, signal.length);
    } else {
      s = signal;
    }

    int offset = 0;
    while (s.length - offset >= getBlockLength(index)) {
      float[] block = new float[getBlockLength(index)];
      System.arraycopy(s, offset, block, 0, block.length);
      offset += block.length;
      send(new MsgResizer(index * duration, duration, block));
      ++index;
    }

    int length = s.length - offset;
    if (length > 0) {
      signal_old = new float[length];
      System.arraycopy(s, offset, signal_old, 0, signal_old.length);
    } else {
      signal_old = null;
    }
  }

  private int getBlockLength(int i) {
    return (int) (Math.floor((i + 1) * samplerate * duration / 1000.0)
                  - Math.floor(i * samplerate * duration / 1000.0));
  }
}
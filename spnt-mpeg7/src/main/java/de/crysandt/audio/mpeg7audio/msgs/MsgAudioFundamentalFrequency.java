/*
  Copyright (c) 2003, Francesco Saletti

  This file is part of the MPEG7AudioEnc project.
*/
package de.crysandt.audio.mpeg7audio.msgs;

/**
 * @author Francesco Saletti
 */

public class MsgAudioFundamentalFrequency
    extends Msg
{
  public final float fundfreq;
  public final float confidence;
  public final float lolimit;
  public final float hilimit;
  public final float[] combedsignal;

  public MsgAudioFundamentalFrequency(int time,
                                      int duration,
                                      float lofreq,
                                      float hifreq,
                                      float f0,
                                      float conf,
                                      float[] combsignal) {
    super(time, duration);
    this.lolimit = lofreq;
    this.hilimit = hifreq;
    this.fundfreq = f0;
    this.confidence = conf;
    this.combedsignal = combsignal;
  }

  public String toString() {
    return super.toString() + "; fundamental frequency: " + fundfreq
                            + "; confidence measure: " + confidence
                            + "; lolimit: " + lolimit
                            + "; hilimit " + hilimit;
  }
  public float[] getCombSignal(){
    return (float[])combedsignal.clone();
  }
  public int getCombSignalLength(){
    return combedsignal.length;
  }

}


/*
  Copyright (c) 2002-2006, Holger Crysandt

  This file is part of the MPEG7AudioEnc project.
*/

package de.crysandt.audio;

import javax.sound.sampled.AudioFormat;



/**
 * @author <a href="mailto:crysandt@ient.rwth-aachen.de">Holger Crysandt</a>
 */
public abstract class AudioInFloat
{
  public boolean isStereo() {
    return !isMono();
  }

  static public float[] getLeft( float[] signal ) {
    float[] left = new float[signal.length/2];
    for (int i=0, j=0; i<left.length; ++i, j+=2)
      left[i] = signal[j];
    return left;
  }

  static public float[] getRight( float[] signal ) {
    float[] right = new float[(signal.length+1)/2];
    for (int i=0, j=1; i<right.length; ++i, j+=2)
      right[i] = signal[j];
    return right;
  }

  static public float[] getMono( float[] signal ) {
    float[] mono = new float[signal.length/2];
    for (int i=0, j=0; i<mono.length; ++i, j+=2 )
      mono[i] = 0.5f * (signal[j] + signal[j+1]);
    return mono;
  }

  public abstract float[] get();
  public abstract float getSampleRate();

  public abstract boolean isMono();

  public abstract AudioFormat getSourceFormat();
}

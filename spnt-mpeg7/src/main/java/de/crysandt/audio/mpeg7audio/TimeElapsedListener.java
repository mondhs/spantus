/*
  Copyright (c) 2002-2003, Holger Crysandt

  This file is part of the MPEG7AudioEnc project.
*/

package de.crysandt.audio.mpeg7audio;

/**
 * @author <a href="mailto:crysandt@ient.rwth-aachen.de">Holger Crysandt</a>
 */
public interface TimeElapsedListener {
  /**
   * @param time_elapsed length of audio signal (in ms) already encoded
   */
  public void timeElapsed(int time_elapsed);
}
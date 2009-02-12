/*
  Copyright (c) 2002-2003, Holger Crysandt

  This file is part of the MPEG7AudioEnc project.
*/

package de.crysandt.audio.mpeg7audio.msgs;

/**
 * At the end of the audio signal the method Encoder.flush() must be called to
 * indicate the end of the audio signal.<p/>
 *
 * A MsgSpeaker tries to call this method of every MsgListener. This is
 * possible if the listener is an instance of Flushable. So every class of the
 * encoder knows when the encoding is over.<p/>
 *
 * If a class depends on this information it overwrites the method flush(),
 * reacts on the function call and must not forget to call super.flush()!<p/>
 *
 * @author <a href="mailto:crysandt@ient.rwth-aachen.de">Holger Crysandt</a>
 */
public interface Flushable {
  /**
   * Indicates the end of the audio signal.
   */
  public void flush();
}
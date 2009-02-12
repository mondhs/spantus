/*
  Copyright (c) 2002-2003, Holger Crysandt
  
  This file is part of the MPEG7AudioEnc project.
*/

package de.crysandt.audio.mpeg7audio.msgs;

/**
 * @author <a href="mailto:crysandt@ient.rwth-aachen.de">Holger Crysandt</a>
 */
public interface MsgListener {
  /**
   * Invoked if when a message is received.
   *
   * @param msg received message
   */
  public void receivedMsg(Msg msg);
}
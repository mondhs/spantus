/*
  Copyright (c) 2002-2003, Holger Crysandt
  
  This file is part of the MPEG7AudioEnc project.
*/

package de.crysandt.audio.mpeg7audio.msgs;

/**
 * Lister/Speaker which receives messages from multiple speakers and sends the
 * messages to multiple listeners.
 *
 * @author <a href="mailto:crysandt@ient.rwth-aachen.de">Holger Crysandt</a>
 */
public class MsgListenerMultiplexer
    extends MsgSpeaker
    implements MsgListener
{
  /**
   * Sends received message to all listeners.
   *
   * @param msg received message which is sent to all listeners
   */
  public void receivedMsg(Msg msg) {
    send(msg);
  }

  public void flush() {

  }
}
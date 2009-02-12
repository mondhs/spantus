/*
  Copyright (c) 2002-2003, Holger Crysandt

  
  This file is part of the MPEG7AudioEnc project.
*/
package de.crysandt.audio.mpeg7audio.msgs;

import java.io.PrintStream;

/**
 * Prints all messages to the output specified by the constructor
 *
 * @author <a href="mailto:crysandt@ient.rwth-aachen.de">Holger Crysandt</a>
 */
public class MsgListenerPrintln
    implements MsgListener
{
  private final PrintStream out;

  public MsgListenerPrintln(PrintStream out) {
    this.out = out;
  }

  /**
   * Prints the message to the output specified by the constructor.
   *
   * @param msg message which has to be printed.
   */
  public void receivedMsg(Msg msg) {
    out.println(msg);
  }
}
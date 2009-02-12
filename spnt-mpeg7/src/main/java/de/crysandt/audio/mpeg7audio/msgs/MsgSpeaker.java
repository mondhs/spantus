/*
  Copyright (c) 2002-2003, Holger Crysandt

  This file is part of the MPEG7AudioEnc project.
*/
package de.crysandt.audio.mpeg7audio.msgs;

import java.util.*;

/**
 * @author <a href="mailto:crysandt@ient.rwth-aachen.de">Holger Crysandt</a>
 */
@SuppressWarnings("unchecked")
public class MsgSpeaker
    implements Flushable
{
  private Set listeners = Collections.synchronizedSet(new HashSet());

  /**
   * Adds a MsgListener to the MsgSpeaker
   *
   * @param listener the listener to be added
   */
  public void addMsgListener(MsgListener listener) {
    if (listener != null)
      listeners.add(listener);
  }

  /**
   * Removes a MsgListener from the MsgSpeaker. Has no effect if the listener
   * was not added before.
   *
   * @param listener the listener to be removed
   */
  public void removeMsgListener(MsgListener listener) {
    if (listener!=null)
      listeners.remove(listener);
  }

  public int getNumberOfListeners() {
    return listeners.size();
  }

  /**
   * calls flush method of all listener which are Flushable.
   */
  public void flush() {
    for (Iterator i=listeners.iterator(); i.hasNext();) {
      try {
        ((Flushable) i.next()).flush();
      } catch (ClassCastException e) {
        // do nothing; object is no instance of Flushable
      }
    }
    listeners.clear(); // remove listeners to help the gc
  }

  /**
   * Sends a message to all listeners of the MsgSpeaker.
   *
   * @param msg message which is sent to all listeners.
   */
  protected void send(Msg msg) {
    if ((msg != null) && (!listeners.isEmpty())) {
      for (Iterator i = listeners.iterator(); i.hasNext(); )
        ((MsgListener) i.next()).receivedMsg(msg);
    }
  }
}
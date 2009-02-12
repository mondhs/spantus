/*
 * Created on 30-giu-2004
 *
 * This file is part of the MPEG7AudioEnc project.
 */
package de.crysandt.audio.mpeg7audio.msgs;


/**
 * @author Guido Raparo
 *
 * To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
public class MsgDcOffset extends Msg{
    
    public int channel;
    public float dco;
   
    public MsgDcOffset(int time, int duration, int channel, float dco) {
      super(time, duration);
      this.channel = channel;
      this.dco = dco;
    }
}

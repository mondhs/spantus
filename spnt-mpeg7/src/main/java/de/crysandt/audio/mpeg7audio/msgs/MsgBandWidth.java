/*
 * Created on 2-lug-2004
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
  public class MsgBandWidth extends Msg{
    
      public float bw;
      public int channel;
    
      public MsgBandWidth(int time,
            int duration,
            float bw,int channel ){
         super(time, duration);
         this.bw=bw;
         this.channel=channel;
      }
  }

/*
 * Created on 28-giu-2004
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
public class MsgBackgroundNoiseLevel
       extends Msg {
  
    public double bnl;
    public int channel;
    public final float SAMPLE_RATE;
    
    public MsgBackgroundNoiseLevel(int time,int duration,int channel,float SAMPLE_RATE,double bnl) {
  
           super(time ,duration);
           this.bnl=bnl;
           this.channel=channel;
           this.SAMPLE_RATE=SAMPLE_RATE;
    }
}

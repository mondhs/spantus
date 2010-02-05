/*
 * Created on 11-giu-2004
 *
 * This file is part of the MPEG7AudioEnc project.
 */
package de.crysandt.audio.mpeg7audio.msgs;

import java.util.*;
import it.univpm.deit.audio.SampleHold;

/**
 * @author Guido Raparo
 *
 * To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
public class MsgSampleHold
       extends Msg{
   
    public ArrayList<?> s_holds;
    public int channel;
    public final float SAMPLE_RATE;
    
    public MsgSampleHold(int time,int duration,ArrayList<?> s_holds,int channel,float SAMPLE_RATE) {
        
        super(time ,duration);
        this.s_holds=s_holds;
        this.channel=channel;
        this.SAMPLE_RATE=SAMPLE_RATE;
        
    }
    
    public int getShnumber(){
        int how_many_s_holds=s_holds.size();
        return how_many_s_holds;
    }
    public int getshposition(int k){
        SampleHold.Sh samplehold =(SampleHold.Sh)s_holds.get(k);
       return samplehold.shposition;
    }
    public int getshlength(int k){
        SampleHold.Sh samplehold=(SampleHold.Sh) s_holds.get(k);
        return samplehold.shlength;
    }
}




/*
 * Created on 9-giu-2004
 *
 * This file is part of the MPEG7AudioEnc project.
 */
package de.crysandt.audio.mpeg7audio.msgs;

import java.util.*;
import it.univpm.deit.audio.DigitalZero;
/**
 * @author Guido Raparo
 *
 * To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
public class MsgDigitalZero
       extends Msg{
    
    public ArrayList<?> zeros;
    public int channel;
    public final float SAMPLE_RATE;
 
    public MsgDigitalZero(int time,int duration,ArrayList<?> zeros,int channel,float SAMPLE_RATE){
        
        super(time ,duration);
        this.zeros=zeros;
        this.channel=channel;
        this.SAMPLE_RATE=SAMPLE_RATE;
    }
    
    public int getZerosnumber(){
        int how_many_zeros=zeros.size();
        return how_many_zeros;
    }
    public int getZeroposition(int k){
       DigitalZero.Zero zero=(DigitalZero.Zero) zeros.get(k);
       return zero.zeroposition;
    }
    public int getZerolength(int k){
        DigitalZero.Zero zero=(DigitalZero.Zero)zeros.get(k);
        return zero.zerolength;
    }
}



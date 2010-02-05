/*
 * Created on 19-mag-2004
 *
 * This file is part of the MPEG7AudioEnc project.
 */
package de.crysandt.audio.mpeg7audio.msgs;
import java.util.*;
import it.univpm.deit.audio.DigitalClip;
/**
 * @author Guido Raparo
 *
 * To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
public class MsgDigitalClip
       extends Msg{
    
    public ArrayList<?> clips;
    public int channel;
    public final float SAMPLE_RATE;

    public MsgDigitalClip(int time,int duration,ArrayList<?> clips,int channel,float SAMPLE_RATE){
        
        super(time ,duration);
        this.clips=clips;
        this.channel=channel;
        this.SAMPLE_RATE=SAMPLE_RATE;
    }
    
    public int getClipsnumber(){
       int how_many_clips=clips.size();
        return how_many_clips;
    }
    public int getClipposition(int k){
        DigitalClip.ClipData clip=(DigitalClip.ClipData) clips.get(k);
       return clip.clipposition;
    }
    public int getCliplength(int k){
        DigitalClip.ClipData clip= (DigitalClip.ClipData)clips.get(k);
        return clip.cliplength;
    }
}



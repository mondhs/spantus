/*
 * Created on 22-giu-2004
 *
 * This file is part of the MPEG7AudioEnc project.
 */

package de.crysandt.audio.mpeg7audio.msgs;


import java.util.LinkedList;
import de.crysandt.audio.mpeg7audio.msgs.Msg;
/**
 * @author Guido Raparo
 *
 * To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
@SuppressWarnings("unchecked")
public class MsgClick
extends Msg{


public LinkedList clicks;
   public int channel;
   public final float SAMPLE_RATE;

    public MsgClick(int time,int duration,LinkedList clicks,int channel,float SAMPLE_RATE) {
 
                 super(time ,duration);
                 this.clicks=clicks;
                 this.channel=channel;
                 this.SAMPLE_RATE=SAMPLE_RATE;
 
    }
    public int getClicksnumber(){
        int how_many_clicks=clicks.size();
         return how_many_clicks;
     }
    public int getClickposition(int k){
         Integer click=(Integer) clicks.get(k);
        return click.intValue();
     }
}
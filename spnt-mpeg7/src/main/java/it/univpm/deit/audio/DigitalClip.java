/*
 * Created on 11-giu-2004
 *
 * This file is part of the MPEG7AudioEnc project.
 */
package it.univpm.deit.audio;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;

import de.crysandt.audio.mpeg7audio.msgs.Msg;
import de.crysandt.audio.mpeg7audio.msgs.MsgDigitalClip;
import de.crysandt.audio.mpeg7audio.msgs.MsgEndOfSignal;
import de.crysandt.audio.mpeg7audio.msgs.MsgListener;
import de.crysandt.audio.mpeg7audio.msgs.MsgResizer;
import de.crysandt.audio.mpeg7audio.msgs.MsgSpeaker;

/**
 * @author Guido Raparo
 */
@SuppressWarnings(value={"unchecked"})
public class DigitalClip
        extends MsgSpeaker
		implements MsgListener{
        
     private static final int LENGTH_FRAME=50;//ms    
     private final float SAMPLE_RATE;
     private LinkedList msglist=new LinkedList();
     private int channel=1;
     private ArrayList clips =new ArrayList();
     private int position =1;
     private int clipposition=1;
     private int length_remember=0;
     private boolean found=false;
     private double tresh= 0.000031;
        
     //l'oggetto ClipData contiene posizione e durata 
     public class ClipData{
            public int clipposition,cliplength;
            
            public  ClipData(int clipposition,int cliplength){
                   this.clipposition=clipposition;
                   this.cliplength=cliplength;
            }
        }
        
     public DigitalClip(float samplerate){
               super();
               this.SAMPLE_RATE=samplerate;
        }
    
     public void receivedMsg (Msg m){ 
         
         
        if (m instanceof MsgResizer)
 			receivedMsg((MsgResizer) m);
 		if (m instanceof MsgEndOfSignal)
 			receivedMsg((MsgEndOfSignal) m);
 		
     }  
     
     private void receivedMsg(MsgResizer m){
         
        msglist.addLast(m);
        if( msglist.size()*m.duration == LENGTH_FRAME ){
            
            //calcola lunghezza del segnale
     	    int length = 0;
     		Iterator iter = msglist.iterator();
     		while( iter.hasNext() )
     			length += ((MsgResizer)(iter.next())).getSignalLength();
     			
     	    // copia gli elementi di  MsgResizer consecutivi in un Array s
     		float[] s = new float[ length ];
     		iter = msglist.iterator();
     		int index = 0;
     		while( iter.hasNext() ) {
     				float[] source = ((MsgResizer) iter.next()).getSignal();
     				System.arraycopy(source, 0, s, index, source.length);
     				index += source.length;
     		}  		 
     			
    	    //trasforma in array di Objects Float
    	    Float[] o=new Float[s.length];
    	    Float uno=new Float(1);
    	    Float muno=new Float(-1);
    	    for(int j=0;j<s.length;j++){
    	         o[j]= new Float(s[j]);
    	         if(o[j].floatValue()>=1-tresh){
    	             o[j]=uno;
    	         }
    	         if(o[j].floatValue()<=-(1-tresh)){
    	             o[j]=muno;
    	         }
    	    }
            //trova i clip
    	    int k=0;
    	    int cliplength=length_remember;
	        
    	    while(k<=o.length-1){
    	        
                if(o[k].equals(uno) || o[k].equals(muno)){
                    if(found==true) cliplength++;
                    else {
                          cliplength++;
                          clipposition=position+k;
                          found=true;
                    }
                }
                else{
                    found=false;
                    if(cliplength>1){
                        clips.add(new ClipData(clipposition,cliplength));
                    }   
                    cliplength=0;length_remember=0;
                }
                if((found==true) && (k==o.length-1)) length_remember=cliplength;
                k++;
    	    }
    	    position=position+k;
			msglist.clear();  
        }
     }   
     //fine del segnale
     public void receivedMsg( MsgEndOfSignal meos ) {
 		int time, duration;
 		time = meos.time;
 		duration = meos.duration;
 		send(new MsgDigitalClip(time, duration,clips,channel,SAMPLE_RATE));
 		send(meos);
 	 }
 	
}

	

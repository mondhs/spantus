/*
 * Created on 11-giu-2004
 *
 * This file is part of the MPEG7AudioEnc project.
 */
package it.univpm.deit.audio;

import de.crysandt.audio.mpeg7audio.msgs.*;
import java.util.*;

/**
 * @author Guido Raparo
 */
@SuppressWarnings("unchecked")
public class DigitalZero
        extends MsgSpeaker
		implements MsgListener{
        
     private static final int LENGTH_FRAME=40;//ms    
     private final float SAMPLE_RATE;
     private LinkedList msglist=new LinkedList();
     private int channel=1;
     private ArrayList zeros =new ArrayList();
     private int position =1;
     private int zeroposition=1;
     private int length_remember=0;
     private boolean found=false;
     private double tresh= 0.000031; 
     //l'oggetto ClipData contiene posizione e durata 
     public class Zero{
            public int zeroposition,zerolength;
            
            public  Zero(int zeroposition,int zerolength){
                   this.zeroposition=zeroposition;
                   this.zerolength=zerolength;
            }
        }
        
     public DigitalZero(float samplerate){
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
     			
     	    // copia gli elementi di 4 MsgResizer consecutivi in un Array s
     		float[] s = new float[ length ];
     		iter = msglist.iterator();
     		int index = 0;
     		while( iter.hasNext() ) {
     				float[] source = ((MsgResizer) iter.next()).getSignal();
     				System.arraycopy(source, 0, s, index, source.length);
     				index += source.length;
     		}  		 
     			
     		 //trasforma in array di Objects Float
    	    Float zero=new Float(0);
    	    Float[] o=new Float[s.length];
    	    for(int j=0;j<=s.length-1;j++){
    	         o[j]= new Float(s[j]);
    	         if((o[j].floatValue()<=tresh)&& (o[j].floatValue()>=-tresh) ){
    	            o[j] = zero;
    	         }
    	    }
            //trova gli zero
    	    int k=0;
    	    int zerolength=length_remember;
	        
    	    while(k<=o.length-1){
    	        
                if(o[k].equals(zero)){
                    if(found==true) zerolength++;
                    else {
                          zerolength++;
                          zeroposition=position+k;
                          found=true;
                    }
                }
                else{
                    found=false;
                    if(zerolength>1){
                        zeros.add(new Zero(zeroposition,zerolength));
                    }   
                    zerolength=0;length_remember=0;
                }
                if((found==true) && (k==o.length-1)) length_remember=zerolength;
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
 		send(new MsgDigitalZero(time, duration, zeros,channel,SAMPLE_RATE));
 		send(meos);
 	 }
 	
}

	

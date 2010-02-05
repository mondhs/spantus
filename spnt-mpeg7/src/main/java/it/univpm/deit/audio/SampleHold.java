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
public class SampleHold
	extends MsgSpeaker
	implements MsgListener
{        
     private static final int LENGTH_FRAME=30;//ms    
     private final float SAMPLE_RATE;
     private LinkedList<MsgResizer> msglist=new LinkedList<MsgResizer>();
     private int channel=1;
     private ArrayList<Sh> s_holds =new ArrayList<Sh>();
     private int position =0;
     private int shposition=1;
     private int length_remember=1;
     private boolean found=false;
     private Float save_value=null;
     private double tresh=0.000031;
        
     //l'oggetto sample&hold contiene posizione e durata 
     public class Sh{
            public int shposition,shlength;
            
            public  Sh(int shposition,int shlength){
                   this.shposition=shposition;
                   this.shlength=shlength;
            }
        }
        
     public SampleHold(float samplerate){
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
     		Iterator<MsgResizer> iter = msglist.iterator();
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
    	    Float[] o=new Float[s.length];
    	    Float uno=new Float(1);
    	    Float muno=new Float(-1);
    	    Float zero=new Float(0);
    	    for(int j=0;j<s.length;j++){
    	         o[j]= new Float(s[j]);
    	         if(o[j].floatValue()>=1-tresh){
    	             o[j]=uno;
    	         }
    	         if(o[j].floatValue()<=-(1-tresh)){
    	             o[j]=muno;
    	         }
    	         if((o[j].floatValue()<=tresh) && (o[j].floatValue()>=-tresh) ){
     	             o[j] = zero;
     	         }
    	         
    	    }
            //trova i sh
    	    int k=0;
    	    int shlength=length_remember;
    	    if(position+k==0) save_value=o[0];
	        
    	    while(k<=o.length-1){
    	        if(position+k==0) k=1;
                if(o[k].equals(save_value)){
                    if(found==true) shlength++;
                    else {
                          shlength++;
                          shposition=position+k;
                          found=true;
                    }
                }
                else{
                    found=false;
                    if((shlength>2)
                      && (save_value.floatValue()!=uno.floatValue())
                      && (save_value.floatValue()!=uno.floatValue())
                      && (save_value.floatValue()!=zero.floatValue())){
                        s_holds.add(new Sh(shposition,shlength));
                    }   
                    shlength=1;length_remember=1;
                    save_value=o[k];
                }
                if((found==true) && (k==o.length-1)) length_remember=shlength;
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
 		send(new MsgSampleHold(time, duration, s_holds,channel,SAMPLE_RATE));
 		send(meos);
 	 }
 	
}

	

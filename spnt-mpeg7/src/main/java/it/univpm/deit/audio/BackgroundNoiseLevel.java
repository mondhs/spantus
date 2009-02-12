/*
 * Created on 28-giu-2004
 *
 * This file is part of the MPEG7AudioEnc project.
 */
package it.univpm.deit.audio;

import de.crysandt.audio.mpeg7audio.msgs.Msg;
import de.crysandt.audio.mpeg7audio.msgs.MsgBackgroundNoiseLevel;
import de.crysandt.audio.mpeg7audio.msgs.MsgEndOfSignal;
import de.crysandt.audio.mpeg7audio.msgs.MsgListener;
import de.crysandt.audio.mpeg7audio.msgs.MsgResizer;
import de.crysandt.audio.mpeg7audio.msgs.MsgSpeaker;
import de.crysandt.math.Function;

/**
 * @author Guido Raparo
 */
 public class BackgroundNoiseLevel extends MsgSpeaker
         implements MsgListener{
    
  
    private final float samplerate;
    private static final int BLOCK_SIZE=5;//ms
    private int channel=1;
    private double signal_max=0;
    private double maxpeak_dB=0;
    private double minpow_dB=0;
    private double minpow=0;
    private float bnl=1;
    
    public BackgroundNoiseLevel(float samplerate){
        super();
        this.samplerate=samplerate;
    }
    
    public void receivedMsg (Msg m){ 
        
       if (m instanceof MsgResizer)
			receivedMsg((MsgResizer) m);
	   if (m instanceof MsgEndOfSignal)
			receivedMsg((MsgEndOfSignal) m);
		
    }  
    
    private void receivedMsg(MsgResizer m){
        
            float[] s = m.getSignal();
       		//trasforma in double
       		double[] d=new double[s.length];
       		for(int i=0;i<s.length;i++){
       		    d[i]=(double)s[i];
       		}
       		
       		//calcola il numero di campioni per ogni blocco
       		int block_size=(int)((samplerate*BLOCK_SIZE)*0.001);
       		int resto=d.length%block_size;
       		while(resto>0){
       		      block_size--;
       		      resto=d.length%block_size;
       		}
       		double[] block=new double[block_size];
       		for(int k=0;k<s.length;k++){
       		    //calcola il massimo nel blocco di segnale s[k]
       		    d[k]=Math.abs(d[k]);
       		    if(d[k]>signal_max){
    		       signal_max=d[k];
    		    }
       		}
            //calcola a blocchi, la minima potenza nel blocco s[k]
   		    double local_minpow=0;
   		    for(int i=0;i<(d.length-block_size);i++){
   		        
   		        if((i==0 )|| ((i+1)%block_size==0)){
   		           System.arraycopy(d,i,block,0,block_size); 
   		           for(int j=0;j<block_size;j++){
   		              local_minpow+=(block[j]*block[j])/block_size;
   		           }  
   		        }   
   		        //fissa ad 1 i blocchi nulli
   		        if(local_minpow==0) local_minpow=1;
   		        //calcola il minimo della potenza dell' intero segnale
				if(local_minpow<minpow || minpow==0) minpow=local_minpow;
   		    } 
        
    }
    //fine del segnale
    public void receivedMsg( MsgEndOfSignal meos ) {
        
		   int time, duration;
		   time = meos.time;
		   duration = meos.duration;
		   if(minpow==0 || signal_max==0) bnl=100;
		   else{
                //calcola il picco del segnale intero in dB
	   	        maxpeak_dB=20*Function.log10(signal_max);
		        //calcola la potenza in dB del blocco con minore potenza 
		        minpow_dB=10*Function.log10(minpow);
		        //calcola il livello di rumore di sottofondo
		        bnl=(float)(minpow_dB-maxpeak_dB);
		   }     
		   send(new MsgBackgroundNoiseLevel(time, duration, channel,samplerate,bnl));
		   send(meos);
	 } 
 }

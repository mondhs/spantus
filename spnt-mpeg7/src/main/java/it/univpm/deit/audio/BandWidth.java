/*
 * Created on 1-lug-2004
 *
 * This file is part of the MPEG7AudioEnc project.
 */
package it.univpm.deit.audio;

import de.crysandt.audio.mpeg7audio.msgs.*;
import de.crysandt.math.*;
/**
 * @author Guido Raparo
 */
public class BandWidth extends MsgSpeaker
       implements MsgListener {
    
   public final  float samplerate ;
   private int channel=1;
   private float fs=0;
   private float bw=0;
   private int up_limit=0;
   private double border=0;
   private float[] maxpartspectrumarray=null;
   private int len=0;
   private int fftlen=0;
   
   public BandWidth(float samplerate){
     super();
     this.samplerate=samplerate;
   }
 
   public void receivedMsg(Msg msg) {
     if(msg instanceof MsgAudioSpectrum) receivedMsg((MsgAudioSpectrum) msg);
     if(msg instanceof MsgEndOfSignal)   receivedMsg((MsgEndOfSignal) msg);
   }
   
    public void receivedMsg( MsgAudioSpectrum m ){
	  
		float[] partspectrum = m.getAudioSpectrum();
		fftlen=m.lengthFFT;
		len=m.getAudioSpectrumLength();
		
		if (maxpartspectrumarray==null) {
		    maxpartspectrumarray=new float[len];
		    System.arraycopy(partspectrum,0,maxpartspectrumarray,0,len);
		} 
		else{
		     for(int k=0;k<len;k++){
			     if(partspectrum[k]>maxpartspectrumarray[k]){
			         maxpartspectrumarray[k]=partspectrum[k];
			     }
	          }
		}
    }
		    
   //fine del segnale
   public void receivedMsg( MsgEndOfSignal meos ) {
       
		int time, duration;
		float[] lmps=new float[len];
		for(int k=0;k<len;k++){
		     lmps[k]=(float)(10*(Function.log10(maxpartspectrumarray[k])));
		}
		
		//upper limit of bandwidth
		float max_lmps;float min_lmps=-3;
		max_lmps =(float)(Function.max(lmps));
		min_lmps =(float)(Function.min(lmps));
		border= min_lmps+((max_lmps-min_lmps)*0.3);
		
		for(int i=1;i<lmps.length-1;i++){
		    if(lmps[i+1]<border && lmps[i-1]>border){
		        up_limit=i;
		    }
		}
		fs=samplerate/fftlen;
		bw=up_limit*fs; //Hz
		
		time = meos.time;
		duration = meos.duration;
		send(new MsgBandWidth(time,duration,bw,channel));
		send(meos);
   }
}
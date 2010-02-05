/*
 * Created on 22-giu-2004
 * 
 * This file is part of the MPEG7AudioEnc project.
 */
package it.univpm.deit.audio;

import de.crysandt.audio.mpeg7audio.msgs.*;
import java.util.*;

/**
 * @author Guido Raparo
 *
 * To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
@SuppressWarnings(value={"unchecked","rawtypes"})
public class Click
	extends MsgSpeaker                
	implements MsgListener
{
        
    private int channel=1;
    private int position=4;
    private double tresh=25;
    private final float SAMPLE_RATE;
    // private LinkedList msglist=new LinkedList();
    private LinkedList clicks=new LinkedList();
    
    public Click(float samplerate){
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
        
            float[] s = m.getSignal();
       		//trasforma in double
       		double[] d=new double[s.length];
       		for(int i=0;i<s.length;i++){
       		    d[i]=(double)s[i];
       		}
       		//filtro passa-alto di Butterworth del 4� ordine
       		double[] highpass=new double[s.length];
       		for (int i=4;i<d.length;i++){
       		    highpass[i]=0.7821f*highpass[i-1]-0.67998f*highpass[i-2]+0.18268f*highpass[i-3]-0.030119f*highpass[i-4]+
       		                0.16718f*d[i]-0.66872f*d[i-1]+1.0031f*d[i-2]-0.66872f*d[i-3]+0.16718f*d[i-4];
       		}
       		//full wave rectification
       		for(int i=4;i<d.length;i++){
       		    if(highpass[i]<0){
       		     highpass[i]=Math.abs(highpass[i]);
       		    }
       		}
       		//median filtering
       		int n=11;
       		double[] windowslide=new double[n];
       		double[] medianfilter=new double[highpass.length];
       		//mdf � highpass[] aumentato degli zeri necessari per calcolare i mediani
            double[] mdf=new double[highpass.length+(n-1)];
       		System.arraycopy(highpass,0,mdf,(n-1)/2,highpass.length);
       		
       		for(int k=(n-1)/2;k<highpass.length;k++){
       		   System.arraycopy(mdf,k-(n-1)/2,windowslide,0,n);
       		   Arrays.sort(windowslide);
       		   medianfilter[k-((n-1)/2)]=windowslide[(windowslide.length-1)/2];
       		}
       		
       		//mean filtering
       		int len=11;
       		double[] meanfilter=new double[medianfilter.length];
       		double[] meanwindow=new double[len];
       		//l'array a[] contiene il segnale filtrato pi� degli zeri all inizio e alla fine per effettuare le medie
       		double [] a=new double[medianfilter.length+(len-1)];
       		System.arraycopy(medianfilter,0,a,(len+1)/2,medianfilter.length);
       		 //Un elemento mean filter[k] � il valore medio di un blocco di len campioni del segnale medianfilter[]
       		for(int k=0;k<meanfilter.length;k++){
       		     System.arraycopy(a,k,meanwindow,0,len);
       		     for(int i=0;i<len;i++){
       		            meanfilter[k]+=(meanwindow[i])/len;
       		     }
       		}
       		//Confronta il segnale con una soglia e trova i clicks
       		for(int k=0;k<highpass.length;k++){
       		    
       		    if(highpass[k]>tresh*meanfilter[k]){
       		        Integer clickposition=new Integer (position-5);
       		        if(!(clicks.isEmpty())){
       		                  Integer lastclick=(Integer)clicks.getLast();
       		                  if(clickposition.intValue()-lastclick.intValue()>200){
       		                       clicks.add(clickposition);
       		                  }
       		        }
       		        else clicks.add(clickposition);
       		    }
       		   position++;
       		}
 }    
 
//  fine del segnale
    public void receivedMsg( MsgEndOfSignal meos ) {
		int time, duration;
		time = meos.time;
		duration = meos.duration;
		send(new MsgClick(time, duration, clicks, channel, SAMPLE_RATE));
		send(meos);
	 }
	
}


   


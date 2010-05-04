/*
 * Created on 27-gen-2004
 * 
 * This file is part of the MPEG7AudioEnc project.
 */
package it.univpm.deit.audio;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;

import de.crysandt.audio.mpeg7audio.msgs.Msg;
import de.crysandt.audio.mpeg7audio.msgs.MsgAudioTempoType;
import de.crysandt.audio.mpeg7audio.msgs.MsgListener;
import de.crysandt.audio.mpeg7audio.msgs.MsgResizer;
import de.crysandt.audio.mpeg7audio.msgs.MsgSpeaker;

/**
 * @author Francesco Saletti
 */
@SuppressWarnings(value={"unchecked"})
public class AudioTempoType
  extends MsgSpeaker
  implements MsgListener
{
  private static LinkedList old_values = new LinkedList();	
  private static LinkedList maximums = new LinkedList();
  static float fs;
  final static int k_dec = 16;
  final float a = 0.99f;
  int loLimit;
  int hiLimit;
  int attHopsize;
  
  public AudioTempoType() {}
  
  public AudioTempoType(int attHopsize,int loLimit, int hiLimit, float samplerate) {
  	this.attHopsize = attHopsize;
  	this.loLimit = loLimit;
  	this.hiLimit = hiLimit;
  	fs=samplerate;
  }
  	
  class RawAndWeight implements Comparable{
  	float bpm;
  	float acf_value;
  	public RawAndWeight(float bpm, float acfval){
  		this.bpm = bpm;
  		this.acf_value = acfval;
  	}

	public int compareTo(Object rv) {
		float rvbpm = ((RawAndWeight)rv).bpm;	
		return (bpm < rvbpm ? -1 : (bpm == rvbpm ? 0 : 1)) ;
	}
	public float[] toArray() {
		float[] res = {this.bpm,this.acf_value};
		return res;
	}
  }
  
  static int minlag;
  static int maxlag;
  static float ceacfmax;
  float[] signal;
  	
  public void receivedMsg(Msg msg) {
  	if(msg instanceof MsgResizer)
  		receivedMsg((MsgResizer) msg);
  }
  public void receivedMsg(MsgResizer mr) {

  	signal = mr.getSignal();
    int length = mr.getSignalLength();
    int declength = (int)(length/k_dec);
    
    minlag = (int)Math.floor(((60 * fs) / hiLimit) / k_dec); 
    maxlag = (int)Math.floor(((60 * fs) / loLimit) / k_dec);
    
    float[][] sigbands = new float[6][length];
    float[][] siglp = new float[6][length];
    float[][] sigdec = new float[6][declength]; // contains the decimated signals
    float[] signalenvelope = new float[declength - 4];
    float[] sigmeans, bandmax, bandmean, bandwf;
    sigmeans= new float[6];
    bandmax = new float[6];
    bandmean = new float[6];
    bandwf = new float[6];

    float[][] bandacf = new float[6][maxlag - minlag];
    float[] ceacf = new float[signalenvelope.length];

 
    // filter the signal (2nd order butterworth filter)
    if (fs==44100){
    for (int n = 4; n < length; n++) {
      sigbands[0][n] = -0.0002f * sigbands[0][n] - 0.0004f * sigbands[0][n-1] - 0.0002f * sigbands[0][n-2] +
              signal[n] - 1.9597f * signal[n-1] + 0.9605f * signal[n-2];
      sigbands[1][n] = -0.0002f * sigbands[1][n] + 0.0004f * sigbands[1][n-2] - 0.0002f * sigbands[1][n-4] +
              signal[n] - 3.9565f * signal[n-1] + 5.8736f * signal[n-2] - 3.8776f * signal[n-3] + 0.9605f * signal[n-4];
      sigbands[2][n] = -0.0008f * sigbands[2][n] + 0.0016f * sigbands[2][n-2] - 0.0008f * sigbands[2][n-4] +
              signal[n] - 3.9076f * signal[n-1] + 5.7365f * signal[n-2] - 3.7524f * signal[n-3] + 0.9226f * signal[n-4];
      sigbands[3][n] = -0.003f * sigbands[3][n] + 0.006f * sigbands[3][n-2] - 0.003f * sigbands[3][n-4] +
              signal[n] - 3.7893f * signal[n-1] + 5.4342f * signal[n-2] - 3.4954f * signal[n-3] + 0.8511f * signal[n-4];
      sigbands[4][n] = -0.0112f * sigbands[4][n] + 0.0223f * sigbands[4][n-2] - 0.0112f * sigbands[4][n-4] +
              signal[n] - 3.4894f * signal[n-1] + 4.7409f * signal[n-2] - 2.9668f * signal[n-3] + 0.7244f * signal[n-4];
      sigbands[5][n] = -0.7236f * sigbands[5][n] + 1.4473f * sigbands[5][n-1] - 0.7236f * sigbands[5][n-2] +
              signal[n] - 1.3694f * signal[n-1] + 0.5252f * signal[n-2];
    }}
    else if (fs==22050){
    for (int n = 4; n < length; n++) {
        sigbands[0][n] = -0.0008f * sigbands[0][n] - 0.0016f * sigbands[0][n-1] - 0.0008f * sigbands[0][n-2] +
                signal[n] - 1.9196f * signal[n-1] + 0.9227f * signal[n-2];
        sigbands[1][n] = -0.0008f * sigbands[1][n] + 0.0016f * sigbands[1][n-2] - 0.0008f * sigbands[1][n-4] +
                signal[n] - 3.9065f * signal[n-1] + 5.7358f * signal[n-2] - 3.7516f * signal[n-3] + 0.9223f * signal[n-4];
        sigbands[2][n] = -0.003f * sigbands[2][n] + 0.006f * sigbands[2][n-2] - 0.003f * sigbands[2][n-4] +
                signal[n] - 3.7893f * signal[n-1] + 5.4342f * signal[n-2] - 3.4954f * signal[n-3] + 0.8511f * signal[n-4];
        sigbands[3][n] = -0.0112f * sigbands[3][n] + 0.0223f * sigbands[3][n-2] - 0.0112f * sigbands[3][n-4] +
                signal[n] - 3.4894f * signal[n-1] + 4.7409f * signal[n-2] - 2.9668f * signal[n-3] + 0.7244f * signal[n-4];
        sigbands[4][n] = -0.7236f * sigbands[4][n] + 1.4473f * sigbands[4][n-1] - 0.7236f * sigbands[4][n-2] +
                signal[n] - 1.3694f * signal[n-1] + 0.5252f * signal[n-2];
        sigbands[5][n] = -0.5171f * sigbands[5][n] + 1.0343f * sigbands[5][n-1] - 0.5171f * sigbands[5][n-2] +
				signal[n] - 0.7856f * signal[n-1] + 0.2829f * signal[n-2];
     }}
   
    // full wave rectification
    
   for (int i = 0; i < 6 ; i++) {
   	for (int n = 4; n < length ; n++) {
   		if (sigbands[i][n] < 0)
   			sigbands[i][n] = Math.abs(sigbands[i][n]);
   	}
   	// low-pass filtering
   	for (int n = 4; n < length ; n++) {
   		siglp[i][n] = (1 - a) * sigbands[i][n] + a * siglp[i][n-1]; 
   		
   	}
   	// decimation (reduce complexity)
   	for (int n = 4; n < declength; n++) {
   		sigdec[i][n] = siglp[i][k_dec * n];
   	}
   }
 
   // mean removal
   for (int i = 0; i < 6 ; i++) {
   	sigmeans[i] = findMean(sigdec[i]);
   	for (int n = 4; n < declength ; n++) {
   		sigdec[i][n] -= sigmeans[i];
   	}
   }


  // calculate ACF for each sub-band
    
    for(int i = 0; i < 6; i++) {
    	for(int k = minlag; k < maxlag; k++) {
    		float a, b, c;
    		a = b = c = 0;
    		for (int j = maxlag; j < declength; j++) {
    			a += sigdec[i][j] * sigdec[i][j - k];
    			b += sigdec[i][j] * sigdec[i][j];
    			c += sigdec[i][j-k] * sigdec[i][j-k];
    		}
    		bandacf[i][k - minlag] = (float)(a/Math.sqrt((b * c)));
    	}
    	bandmax[i] = findMax(bandacf[i]);
    	bandmean[i] = findMean(bandacf[i]);
    	// determine weight factor for each frequency band and make the multiplication
    	bandwf[i] = bandmax[i] - bandmean[i];
		for (int n = 4; n < declength; n++){
			sigdec[i][n] *= bandwf[i];
			// sum the envelopes weighted by the respective weighting factorsd to obtain a combined envelope signal

			signalenvelope[n-4] += sigdec[i][n]; 
		}
    }

	// Calculate the combined envelope ACF (CEACF)
    
    for(int k = minlag; k < maxlag; k++) {
    	float a, b, c;
    	a = b = c = 0;
    	for (int j = maxlag; j < signalenvelope.length; j++) {
    		a += signalenvelope[j] * signalenvelope[j - k];
    		b += signalenvelope[j] * signalenvelope[j];
    		c += signalenvelope[j-k] * signalenvelope[j-k];
    	}
    	ceacf[k] = (float)(a/Math.sqrt((b * c)));
    	
    }
   /* Grafico gr = new Grafico("prova", ceacf);
    gr.pack();
    gr.setVisible(true);*/
    
    ceacfmax = findMax(ceacf);
    float ceacfmean = findMean(ceacf);
    float ceacfwf = ceacfmax - ceacfmean;
    
    findPeaks(ceacf, ceacfwf); 
    float[] result =((RawAndWeight)newPeak()).toArray();
    send(new MsgAudioTempoType(mr.time, mr.duration, attHopsize, this.loLimit, this.hiLimit, result[0] , result[1], 1, 4)); 
  }
  
  
  
  static float findMax (float[] array) {
  	
  	float max = 0.0f;
  	for(int i =0;i < array.length - 1;i++) {
  		if(array[i+1] > max)
  			max = array[i+1];
  	}
  	return max;
  }
  
  static float findMax () {
  	if (!(maximums.isEmpty())){
  		float max = ((RawAndWeight)maximums.getFirst()).acf_value;
  		Iterator it  = maximums.iterator();
  		while (it.hasNext()) {
  			if (((RawAndWeight)it.next()).acf_value > max)
  				max = ((RawAndWeight)it.next()).acf_value; 
  		}
  		return max;
  	} 
  	else return 0.0f;
  }
  
  
  static float findMean (float[] array) {
  	int sum = 0;
  	int i = 0;
  	while(i < (array.length - 1)) {
  		sum += array[i];
  		i++;
  	}
  	
  	return (sum/array.length);
  }

  static void findPeaks (float[] array, float wf) {
	int minind = (int)Math.floor(((60 * fs) / 160) / k_dec);
	int maxind = (int)Math.floor(((60 * fs) / 60) / k_dec);
	maximums.removeAll(maximums);
	AudioTempoType att =new AudioTempoType();
	
	for (int index = minind; index < maxind; index++) {
		inner: for (int j = 1; j < 100; j++) {
			if ((array[index - j] < array[index]) && (array[index + j] < array[index])) {
				if (j == 99) {
					
					// add peaks to the list only if they are not sub-multiples of some of the already stored ones
					if (!maximums.isEmpty()){
						Iterator it = maximums.iterator();
						while (it.hasNext()) {
							float actual = (((RawAndWeight)it.next()).bpm) ;
							if (((60*fs)/(k_dec*(index))) - 3 < actual / 2 &&
								((60*fs)/(k_dec*(index))) + 3 > actual / 2){
									break inner;
								}
						}
					}
					maximums.add(att.new RawAndWeight((60*fs)/(k_dec*(index)), array[index] ));
				}
			}
			else break;
		}
	}
	
	Collections.sort(maximums);
	/*Iterator it = maximums.iterator();
	if (!maximums.isEmpty())
		while (it.hasNext()) 
			System.out.println(((RawAndWeight)it.next()).bpm + " " );*/
			
  }	
  
  static RawAndWeight peakDecision(){
	AudioTempoType att = new AudioTempoType();
  	int tollerance = 3;
  	@SuppressWarnings("unused")
	RawAndWeight result = null;
  	Iterator it = maximums.iterator();
	if (maximums.isEmpty())
		result = att.new RawAndWeight(0,0);
	if (!(old_values.isEmpty())) {
		while(it.hasNext()) {
			RawAndWeight peaknew = (RawAndWeight)it.next();
			Iterator ito = old_values.iterator();
			while (ito.hasNext()) {
				RawAndWeight peakold = (RawAndWeight)ito.next();
				if (peaknew.bpm > peakold.bpm - tollerance &&
					peaknew.bpm < peakold.bpm + tollerance)
						return peaknew;
			}
		}
	    return newPeak();

	}
	return newPeak();
	
	
	}
  static RawAndWeight newPeak() {
  	AudioTempoType att  = new AudioTempoType();
  	
  	float newpeaktollerance = 0.03f; 
  	RawAndWeight result = null;
  	if (maximums.isEmpty()) return att.new RawAndWeight(0,0); 
  	result = (RawAndWeight)maximums.getFirst();
  	Iterator it = maximums.iterator();
  	while(it.hasNext()) {
  		RawAndWeight actual = (RawAndWeight)it.next();
  		if (actual.acf_value > (result.acf_value) - newpeaktollerance){
  			result = actual;
  		}
  	}
  	old_values.add(result);

  	return (RawAndWeight)result;
  	
  }
 }
 
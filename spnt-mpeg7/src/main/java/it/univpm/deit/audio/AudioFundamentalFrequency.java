/*
  Copyright (c) 2003, Francesco Saletti

  This file is part of the MPEG7AudioEnc project.
*/

package it.univpm.deit.audio;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;

import de.crysandt.audio.mpeg7audio.msgs.Msg;
import de.crysandt.audio.mpeg7audio.msgs.MsgAudioFundamentalFrequency;
import de.crysandt.audio.mpeg7audio.msgs.MsgListener;
import de.crysandt.audio.mpeg7audio.msgs.MsgResizer;
import de.crysandt.audio.mpeg7audio.msgs.MsgSpeaker;
import de.crysandt.math.FFT2N;
import de.crysandt.math.Function;

/**
 * @author Francesco Saletti, Holger Crysandt
 */
public class AudioFundamentalFrequency
	extends MsgSpeaker
	implements MsgListener
{
	private final int WINDOW_LENGTH;
	private final float SAMPLE_RATE;
	@SuppressWarnings("unused")
	private final int HOP_SIZE;
	private final float LO_LIMIT; // Lower limit of search space [Hz]
	private final float HI_LIMIT; // Higher limit of search space [Hz]
	private final float MAX_HI_LIMIT = 44100.0f; // Maximum limit to search fundamental [Hz]
	private final LinkedList<MsgResizer> msglist = new LinkedList<MsgResizer>();
	
	private static LinkedList<RawAndWeight> maximums = new LinkedList<RawAndWeight>();
	private static float Fs;
	/*	
	 public AudioFundamentalFrequency(){
	 this.HI_LIMIT=0;
	 this.LO_LIMIT=0;
	 this.SAMPLE_RATE=0;
	 this.WINDOW_LENGTH=0;
	 }
	 */	
	public AudioFundamentalFrequency(
			int hopsize, 
			float samplerate, 
			float lolimit, 
			float hilimit) 
	{
		this.SAMPLE_RATE = samplerate;
		this.HOP_SIZE = hopsize;
		
		// set the maximum pitch period searchable
		this.WINDOW_LENGTH = (int)(Math.ceil(1000 * 2 / lolimit));
		this.LO_LIMIT = lolimit;
		if(hilimit > MAX_HI_LIMIT)
			this.HI_LIMIT = MAX_HI_LIMIT;
		else
			this.HI_LIMIT = hilimit;
		Fs = SAMPLE_RATE;
	}
	
	public void receivedMsg(Msg msg) {
		if(msg instanceof MsgResizer)
			receivedMsg((MsgResizer) msg);
	}
	
	public void receivedMsg(MsgResizer m) {
		//       MsgResizer m = (MsgResizer) msg;
		msglist.addLast(m);
		if( msglist.size()* m.duration == WINDOW_LENGTH) {
			
			// calculate length of signal
			int length = 0;
			Iterator<MsgResizer> i = msglist.iterator();
			while( i.hasNext() )
				length += ((MsgResizer)(i.next())).getSignalLength();
			
			float[] signal = new float[ length ];
			
			// merge msgs to signal
			i = msglist.iterator();
			int index = 0;
			while( i.hasNext() ) {
				float[] source = ((MsgResizer) i.next()).getSignal();
				System.arraycopy(source, 0, signal, index, source.length);
				index += source.length;
			}
			
			// array containing the maximum of crosscorrelation
			// (1st element) and the fundamental period estiamte (2nd element)
			float[] fundamental;
			
			int f0_lag;
			int minlag = (int)Math.floor(SAMPLE_RATE/HI_LIMIT);
			int maxlag = (int)Math.floor(SAMPLE_RATE/LO_LIMIT);
			/*			
			 // signal preprocessing with infinite centered clipping:
			  // every sample whose value is lower than a selected threshold is set to zero
			   float[] maxsig = arraymax(signal, true);
			   float signalmax = maxsig[0];
			   float cliptreshold = signalmax * 0.3f;
			   for(int j  = 0; j < signal.length; j++) {
			   if (Math.abs(signal[j]) < cliptreshold)
			   signal[j] = 0;
			   }
			   */		
			// in order to find the fundamental frequency the signal is cross-correlated with
			// a part of the same signal (its second half).
			float[] crosscorrelation = getCrossCorrelation(signal, minlag, maxlag);
			
			findPeaks(crosscorrelation,minlag);
			fundamental = newPeak().toArray();
			if (fundamental[0]!=0)
				f0_lag=(int)(SAMPLE_RATE/fundamental[0]);
			else f0_lag=0;
			MsgResizer mr = (MsgResizer) msglist.get(0);
			
			// get comb-filtered signal (useful for Audio Harmonicity descriptor)
			float[] combedsignal = new float[mr.getSignalLength()];
			combedsignal = getCombed(signal,f0_lag, mr.getSignalLength());
			send(new MsgAudioFundamentalFrequency(mr.time,mr.duration,LO_LIMIT,HI_LIMIT,fundamental[0],fundamental[1],combedsignal));
			// remove first element of list
			msglist.removeFirst();
		}
	}

	@SuppressWarnings("unused")
	static private float[] getCrossCorrelationOld(
			float[] signal, 
			int minlag, 
			int maxlag) 
	{
		float[] crosscorrelation = new float[(maxlag - minlag)];
		for(int k = minlag; k < maxlag; k++) {
			float a, b, c;
			a = b = c = 0;
			for (int j = maxlag; j < signal.length; j++) {
				a += signal[j] * signal[j - k];
				b += signal[j] * signal[j];
				c += signal[j-k] * signal[j-k];
			}
			if (b == 0 || c == 0)
				crosscorrelation[k - minlag] = 0;
			else
				crosscorrelation[k - minlag] = (float)(a/Math.sqrt((b * c)));
		}
		return crosscorrelation;
	}
	
	
	static private float[] getCrossCorrelation(
			float[] signal,
			int minlag,
			int maxlag)
	{
		int length_fft = 1 << (Function.getHighestBit(signal.length-1)+1);
		int length_window = signal.length - maxlag;
		
		FFT2N fft = new FFT2N(length_fft);
		
		float[] f1 = new float[length_fft];
		System.arraycopy(signal, signal.length-length_window, f1, 0, length_window);
		fft.fft(f1);
		
		float[] f2 = new float[length_fft];
		System.arraycopy(signal, 0, f2, 0, signal.length);
		fft.fft(f2);
		
		FFT2N.conj(f1);		
		f1 = FFT2N.mult(f1,f2);
		fft.ifft(f1);
		
		float[] power = new float[signal.length];
		for (int i=0; i<power.length; ++i)
			power[i] = signal[i] * signal[i];
		
		double power_0 = 0.0;
		for (int i=signal.length-length_window; i<signal.length; ++i)
			power_0 += power[i];
		float sqrt_power_0 = (float) Math.sqrt(power_0);
		
		float[] corr = new float[maxlag-minlag];
		double power_j = 0.0; 
		for (int j=maxlag-minlag+1, j_max = maxlag-minlag+length_window; j<j_max; ++j)
			power_j += power[j];
		
		for (int i=0, j=maxlag-minlag; i<corr.length; ++i, --j, power_j -= power[j+length_window]) {
			power_j += power[j];
			/*			
			 double power_k = 0.0; 
			 for (int k=j; k<j+length_window; ++k)
			 power_k += power[k];
			 
			 assert (float) power_k == (float) power_j;
			 */			
			corr[i] = (power_j>0.0) && (sqrt_power_0>0) ? 
					f1[j] / sqrt_power_0 / (float) Math.sqrt(power_j) : 0.0f;
					
		}
		
		return corr;
	}
	
	// this method returns the maximum of the array and its corrispondent index; if
	// the boolean abs_max is true it returns the absolute maximum;
	static float[] arraymax(float[] array, boolean abs_max) {
		if (abs_max) {
			float[] max = {Math.abs(array[0]), 0.0f};
			for (int index = 0; index < array.length; index++) {
				if (Math.abs(array[index]) > Math.abs(max[0])) {
					max[0] = Math.abs(array[index]);
					max[1] = index;
				}
			}
			return max;
		}
		else {
			float[] max = {array[0], 0.0f};
			for (int index = 0; index < array.length; index++) {
				if (array[index] > max[0]) {
					max[0] = array[index];
					max[1] = index;
				}
			}
			return max;
		}
	}
	
	static float[] getCombed(float[] signal, int K, int framelength) {
		// calculate optimal gain
		float a,b,d;
		float[] c = new float[framelength];
		a = b = 0;
		for (int i = signal.length - (framelength -1); i < signal.length; i++) {
			a += signal[i] * signal[i - K];
			b += signal[i - K] * signal[i - K];
		}
		d = a/b;    //optimal gain
		
		// calculate combed-signal
		for (int i = signal.length - (framelength-1); i < signal.length; i++) {
			c[i - (signal.length - (framelength - 1))] = signal[i] - d * signal[i - K];
		}
		return c;
	}
	
	static void findPeaks (float[] array, int minlag) {
		maximums.removeAll(maximums);
		//		AudioFundamentalFrequency aff = new AudioFundamentalFrequency();
		for (int index = 10; index < array.length-10; index++) {
			 for (int j = 1; j < 10; j++) {
				if ((array[index - j] < array[index]) && (array[index + j] < array[index])) {
					if (j == 9) {
						maximums.add(new RawAndWeight(
								(Fs)/((index+minlag)), 
								array[index]));
					}
				}
				else break;
			}
		}
		Collections.sort(maximums);
		/*Iterator it = maximums.iterator();
		 if (!maximums.isEmpty())
		 while (it.hasNext()) 
		 System.out.print(((RawAndWeight)it.next()).ff + " " );*/
		
	}	
	
	private static RawAndWeight newPeak() {
		//		AudioFundamentalFrequency aff = new AudioFundamentalFrequency();    	
		RawAndWeight result = null;
		float tollerance = 0.01f;
		
		if (maximums.isEmpty()) 
			return new RawAndWeight(0,0); 
		
		result = (RawAndWeight)maximums.getFirst();
		
		for (Iterator<RawAndWeight> it = maximums.iterator(); it.hasNext();) {
			RawAndWeight actual = (RawAndWeight)it.next();
			if (actual.weigth >= result.weigth - tollerance){
				result = actual;
			}
		}
		
		return result;		
	}
}

class RawAndWeight 
implements Comparable<Object>
{
	float ff;
	float weigth;
	public RawAndWeight(float ff, float weigth){
		this.ff = ff;
		this.weigth = weigth;
	}
	
	public int compareTo(Object rv) {
		float rvff = ((RawAndWeight)rv).ff;	
		return (ff < rvff ? -1 : (ff == rvff ? 0 : 1)) ;
	}
	public float[] toArray() {
		float[] res = {this.ff,this.weigth};
		return res;
	}
}


/*
 Copyright (c) 2002-2003, Holger Crysandt

 This file is part of the MPEG7AudioEnc project.
 */

package de.crysandt.audio.mpeg7audio;

import java.util.*;

import de.crysandt.audio.mpeg7audio.msgs.*;
import de.crysandt.math.Function;

import it.univpm.deit.FFT2N;
// import de.crysandt.math.FFT2N;

/**
 * @author <a href="mailto:crysandt@ient.rwth-aachen.de">Holger Crysandt</a>
 */
@SuppressWarnings("unchecked")
class AudioSpectrum
	extends MsgSpeaker
	implements MsgListener
{
	private static final int LENGTH_WINDOW = 30; /* ms */
	
	private final float samplerate;
	
	private LinkedList msglist = new LinkedList();
	private Map        hamming = new TreeMap();
	private Map        fft2n   = new TreeMap();
	
	public AudioSpectrum( float samplerate ) {
		super( );
		this.samplerate = samplerate;
	}
	
	private float[] getHamming( int length ) {
		Integer key = new Integer(length);
		float[] window = (float[]) hamming.get(key);
		if (window == null) {
			window = new float[length];
			for (int n = 0; n < window.length; ++n)
				window[n] =(float)(
						0.54 - 0.46 * Math.cos(n * 2.0 * Math.PI / (window.length - 1)));
			hamming.put(key, window);
		}
		return window;
	}
	
	private FFT2N getFFT2N(int length_fft) {
		Integer key = new Integer( length_fft );
		FFT2N fft = (FFT2N) fft2n.get( key );
		if (fft == null) {
			fft = new FFT2N(length_fft);
			fft2n.put(key, fft);
		}
		return fft;
	}
	
	public void receivedMsg( Msg mr ) {
		if (mr instanceof MsgResizer)
			receivedMsg((MsgResizer) mr);
	}
	
	public void receivedMsg( MsgResizer mr ) {
		msglist.addLast(mr);
		
		// check if one or more messages can be appended to one signal
		// with the length of LENGTH_WINDOW
		if (!(LENGTH_WINDOW % mr.duration == 0 ))
			throw new AssertionError ();
		
		if( msglist.size()*mr.duration == LENGTH_WINDOW ) {
			
			// calculate length of signal
			int length = 0;
			Iterator i = msglist.iterator();
			while( i.hasNext() )
				length += ((MsgResizer)(i.next())).getSignalLength();
			
			float[] s = new float[ length ];
			
			// merge msgs to signal
			i = msglist.iterator();
			int index = 0;
			while( i.hasNext() ) {
				float[] source = ((MsgResizer) i.next()).getSignal();
				System.arraycopy(source, 0, s, index, source.length);
				index += source.length;
			}
			
			// get or calculate hamming window
			float[] window = getHamming( s.length );
			
			// scale signal with hamming window
			for( int n=0; n<window.length; ++n )
				s[n] *= window[n];
			
			// calculate length of fft
			int length_fft = 1 << Function.getHighestBit(s.length-1)+1; 
			
			FFT2N fft = getFFT2N( length_fft );
			float[] signal;
			
			if( length_fft == s.length ) {
				signal = s;
			} else {
				signal = new float[length_fft];
				System.arraycopy(s, 0, signal, 0, s.length);
				Arrays.fill(signal,s.length, signal.length, 0.0f);
			}
			
			fft.fft(signal);
			float[] ps = FFT2N.PowerSpectrum(signal);
			
			MsgResizer msgresizer = (MsgResizer) msglist.get(0);
			send(new MsgAudioSpectrum(
					msgresizer.time,
					msgresizer.duration,
					window.length,
					fft.length,
					samplerate / fft.length,
					ps));
			
			// remove first element of list
			msglist.removeFirst();
		}
	}
}

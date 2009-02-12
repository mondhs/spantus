/*
 Copyright (c) 2002-2003, Holger Crysandt
 
 This file is part of the MPEG7AudioEnc project.
 */

package de.crysandt.audio.mpeg7audio;

import de.crysandt.audio.mpeg7audio.msgs.*;
import de.crysandt.math.Function;

/**
 * @author <a href="mailto:crysandt@ient.rwth-aachen.de">Holger Crysandt</a>
 */
class AudioSpectrumCentroidSpread 
	extends    MsgSpeaker
	implements MsgListener
{
	private static final float LIMIT_LO = 62.5f;
	
	private float[] f_log2 = null; 
	
	public void receivedMsg( Msg m ) {
		if (m instanceof MsgAudioSpectrum)
			receivedMsg((MsgAudioSpectrum) m);
	}
	
	public void receivedMsg(MsgAudioSpectrum mas) {
		float[] spectrum = mas.getAudioSpectrum();
		float   delta_f  = mas.deltaF;
		
		if (f_log2==null) {
			f_log2 = new float[spectrum.length];
			for (int i=0; i<f_log2.length; ++i)
				f_log2[i] = (float) Math.log(i*delta_f/1000.0f) / (float) Function.LOG2;
		}
		
		float power = 0.0f;
		for (int i=0; i<spectrum.length; ++i)
			power += spectrum[i];
		
		float centroid = 0.0f;
		float spread   = 0.0f;
		
		if (power > 0.0) {
			// Audio Spectrum Centoroid
			int   i_lo = 0;
			float f = 0.0f;
			
			double power_lo = 0.0;
			for( ; f<LIMIT_LO; ++i_lo, f+=delta_f )
				power_lo += spectrum[i_lo];
			
			double sum = power_lo * Function.log2(LIMIT_LO / 2.0f / 1000.0f);			
			for (int i = i_lo; i < spectrum.length; ++i, f += delta_f)
				sum += spectrum[i] * f_log2[i];
			
			centroid = (float) (sum / power);
			
			// Audio Spectrum Spread
			f = i_lo * delta_f;
			float tmp = Function.log2(LIMIT_LO / 2.0f / 1000.0f) - centroid;
			sum = power_lo * tmp * tmp;
			
			for( int i=i_lo ; i< spectrum.length; ++i, f += delta_f ) {
				tmp = f_log2[i] - centroid;
				sum += spectrum[i] * tmp * tmp;
			}
			
			spread = (float) Math.sqrt(sum /power);
		}
		
		send(new MsgAudioSpectrumCentroid(mas.time, mas.duration, centroid));
		send(new MsgAudioSpectrumSpread(mas.time, mas.duration, spread));
	}
}
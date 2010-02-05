/*
 * Copyright (c) 2004, Michele Bartolucci
 *
 * This file is part of the MPEG7AudioEnc project.
 */
package it.univpm.deit.audio;

import java.util.*;

import de.crysandt.audio.mpeg7audio.msgs.*;
import de.crysandt.math.Function;

/**
 * @author <a href="mailto:micky78@email.it">Michele Bartolucci</a>
 */
public class HarmonicSpectralDeviation 
	extends MsgSpeaker 
	implements MsgListener
{
	
	private float num_hsd, hsd = 0.0f;
	private int nb_frames = 0;
	
	public void receivedMsg( Msg msg ) {
		if (msg instanceof MsgHarmonicPeaks)
			receivedMsg((MsgHarmonicPeaks) msg);
		if(msg instanceof MsgEndOfSignal)
			receivedMsg((MsgEndOfSignal) msg);
	}
	
	public void receivedMsg( MsgHarmonicPeaks mhp )
	{
		// evaluate the spectral envelope as the mean amplitude of three
		// adiacent harmonic peaks

		float ihsd = 0.0f;
		float num = 0.0f;
		float[] pd1, pd2, pd3;
		
		ArrayList<?> peaks = mhp.getPeaks();
		int size = mhp.getHarmonicPeaksSize();
		float spectrumEnvelope[] = new float[size];
		
		if(size == 1)
			ihsd = 0;
		else if (size == 2)
		{
			pd1 = (float[])peaks.get(0);
			pd2 = (float[])peaks.get(1);
			spectrumEnvelope[0] = spectrumEnvelope[1] = (pd1[1] + pd2[1])/2;
			num = Math.abs(Function.log10(pd1[1])-Function.log10(spectrumEnvelope[0])) +
			Math.abs(Function.log10(pd2[1])-Function.log10(spectrumEnvelope[1]));
			// den = Function.log10(pd1[1])+Function.log10(pd2[1]);
			ihsd = num/size; //basing on the proposed corrigendum 2 of the part 4		
			
		}
		
		else {
			
			pd1 = (float[])peaks.get(0);
			pd2 = (float[])peaks.get(1);
			spectrumEnvelope[0] = (pd1[1] + pd2[1])/2;
			
			for( int i = 1; i < spectrumEnvelope.length -1; i ++)
			{
				pd1 = (float[])peaks.get(i-1);
				pd2 = (float[])peaks.get(i);
				pd3 = (float[])peaks.get(i+1);
				spectrumEnvelope[i] = (pd1[1] + pd2[1] + pd3[1])/3;
				
			}
			
			pd1 = (float[])peaks.get(size-2);
			pd2 = (float[])peaks.get(size-1);
			spectrumEnvelope[spectrumEnvelope.length-1] = (pd1[1] + pd2[1])/2;
			
			// evaluate the istantaneous HarmonicSpectralDeviation ihsc
			
			for( int j=0; j < size; j ++)
			{
				pd1 = (float[])peaks.get(j);
				num += Math.abs((Function.log10(pd1[1]))-(Function.log10(spectrumEnvelope[j])));
				
			}
			
			ihsd = num/size; //basing on the proposed corrigendum 2 of the part 4
			// System.out.println(ihsd);
		}
		
		num_hsd += ihsd;
		nb_frames += 1;
		hsd = num_hsd/nb_frames;
	}
	
	
	public void receivedMsg( MsgEndOfSignal meos ) {
		int time, duration;
		time = meos.time;
		duration = meos.duration;
		send( new MsgHarmonicSpectralDeviation(time, duration, hsd) );
		send(meos);
	}
	
	
	
}


/*
 * Copyright (c) 2004, Michele Bartolucci
 *
 * This file is part of the MPEG7AudioEnc project.
 */

package it.univpm.deit.audio;

import java.util.*;

import de.crysandt.audio.mpeg7audio.msgs.*;

/**
 * @author <a href="mailto:micky78@email.it">Michele Bartolucci</a>
 */
public class HarmonicSpectralCentroidSpread 	
	extends MsgSpeaker
	implements MsgListener
{
	
	private float num_hsc = 0.0f;
	private float num_hss;
	private float hsc = 0.0f;
	private float hss = 0.0f;
	private int nb_frames = 0;

	
	public HarmonicSpectralCentroidSpread(){
		super();
	}
	
	public void receivedMsg( Msg msg ) {
		if (msg instanceof MsgHarmonicPeaks){
			receivedMsg((MsgHarmonicPeaks) msg);
		}
		if(msg instanceof MsgEndOfSignal) {
			receivedMsg((MsgEndOfSignal) msg);
		}
	}
	
	public void receivedMsg( MsgHarmonicPeaks mhp ) {
		
		float num_ihsc = 0.0f, den_ihsc = 0.0f;
		float num_ihss = 0.0f, den_ihss = 0.0f;
		
		ArrayList<?> peaks = mhp.getPeaks();		
		Iterator<?> iterator = peaks.iterator();
		
		while( iterator.hasNext() ) {
			float[] pd = (float[])iterator.next();
			num_ihsc += pd[1] * pd[0];
			den_ihsc += pd[1];
		}
		
		float ihsc = num_ihsc/den_ihsc;

		iterator = peaks.iterator();
		
		while( iterator.hasNext() ) {
			float[] pd = (float[])iterator.next();
			num_ihss += Math.sqrt((Math.pow(pd[1],2))*(Math.pow((pd[0]-ihsc),2)));
			den_ihss += Math.sqrt(Math.pow(pd[1],2));
		}
		
		float ihss = (1/ihsc)*(num_ihss/den_ihss);
		
		num_hsc += ihsc;
		num_hss += ihss;
		nb_frames += 1;
		hss = num_hss/nb_frames;
		hsc = num_hsc/nb_frames;
		
	}
	
	public void receivedMsg( MsgEndOfSignal meos ) {
		int time, duration;
		time = meos.time;
		duration = meos.duration;
		
		send( new MsgHarmonicSpectralCentroid(time, duration, hsc) );
		send( new MsgHarmonicSpectralSpread(time, duration, hss));
		send(meos);
	}
	
}

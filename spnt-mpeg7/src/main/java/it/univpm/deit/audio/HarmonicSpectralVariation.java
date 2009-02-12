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
@SuppressWarnings("unchecked")
public class HarmonicSpectralVariation 	
	extends MsgSpeaker
	implements MsgListener
{
	
	private float num_hsv = 0.0f;
	private float hsv = 0.0f;
	private int nb_frames = 0;
	private ArrayList msgs = new ArrayList();
	
	public void receivedMsg( Msg msg ) {
		if (msg instanceof MsgHarmonicPeaks){
			receivedMsg((MsgHarmonicPeaks) msg);
		}
		if(msg instanceof MsgEndOfSignal) {
			receivedMsg((MsgEndOfSignal) msg);
		}
	}
	
	public void receivedMsg( MsgHarmonicPeaks mhp ) {
		
		float num = 0.0f, den1 = 0.0f, den2 = 0.0f;
		msgs.add(mhp.getPeaks());
		
		if( msgs.size() == 2)
		{		
			
			ArrayList peaks1 = (ArrayList)msgs.get(0);
			ArrayList peaks2 = (ArrayList)msgs.get(1);
			
			int size;
			
			if ( peaks1.size() >= peaks2.size() )
				size = peaks2.size();
			else
				size = peaks1.size();
			
			for( int i = 0; i < size; i++ )
			{
				num += (((float[])peaks1.get(i))[1])*(((float[])peaks2.get(i))[1]);
				den1 += Function.square((((float[])peaks1.get(i))[1]));
				den2 += Function.square((((float[])peaks2.get(i))[1]));
			}	
			
			
			float ihsv = 1-(num/(((float)Math.sqrt(den1))*((float)Math.sqrt(den2))));
			
			num_hsv += ihsv;
			nb_frames += 1;
			hsv = num_hsv/nb_frames;
			
			msgs.remove(0);
		}
	}
	
	
	
	public void receivedMsg( MsgEndOfSignal meos ) {
		int time, duration;
		time = meos.time;
		duration = meos.duration;
		
		send( new MsgHarmonicSpectralVariation(time, duration, hsv) );
		send(meos);
	}
	
}

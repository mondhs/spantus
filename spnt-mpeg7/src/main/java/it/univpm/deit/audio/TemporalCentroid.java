/*
  Copyright (c) 2004, Michele Bartolucci

  This file is part of the MPEG7AudioEnc project.
*/
package it.univpm.deit.audio;

import java.util.*;

import de.crysandt.audio.mpeg7audio.msgs.*;

/**
 * @author <a href="mailto:micky78@email.it">Michele Bartolucci</a>
 */
public class TemporalCentroid 	extends MsgSpeaker
implements MsgListener
{
	
	private final float samplerate;
	float num = 0;
	float den = 0;
	
	
	public TemporalCentroid( float samplerate){
		this.samplerate = samplerate;
	}
	
	
	public void receivedMsg(Msg m){
		if( m instanceof MsgSignalEnvelope)
			receivedMsg( (MsgSignalEnvelope) m );
	}
	
	public void receivedMsg( MsgSignalEnvelope m ){
		
		float temporalCentroid = 0;
		ArrayList<?> signalEnv = m.signalEnv;
		
		for( int i = 1; i < signalEnv.size(); i++)
		{
			num += (((float)i/samplerate)*m.slide)*((Float)signalEnv.get(i)).floatValue();
			den += ((Float)signalEnv.get(i)).floatValue();
		}
		
		temporalCentroid = num/den;
		
		send(new MsgTemporalCentroid(m.time,m.duration,temporalCentroid));
		
	}
	
}

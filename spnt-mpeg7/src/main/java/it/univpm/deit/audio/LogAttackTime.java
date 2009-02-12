/*
 * Copyright (c) 2004, Michele Bartolucci
 *
 * This file is part of the MPEG7AudioEnc project.
 */
package it.univpm.deit.audio;

import de.crysandt.audio.mpeg7audio.msgs.*;
import de.crysandt.math.Function;

/**
 * @author <a href="mailto:micky78@email.it">Michele Bartolucci</a>
 */
public class LogAttackTime 
	extends MsgSpeaker
	implements MsgListener 
{
	
	private float sampleRate, thresold;
	private float t0, t1, lat;
	
	public LogAttackTime(float sampleRate, float thresold){
		this.sampleRate = sampleRate;
		this.thresold = thresold;
	}
	
	public void receivedMsg( Msg msg ) {
		if (msg instanceof MsgSignalEnvelope)
			receivedMsg((MsgSignalEnvelope) msg);
	}
	
	public void receivedMsg( MsgSignalEnvelope msg ) {
		
		// find the maximum value of the float ArrayList signalEnv
		float max = ((Float)msg.signalEnv.get(0)).floatValue();
		t1 = 0;
		for( int i = 1; i < msg.signalEnv.size(); i++)
		{
			if( ((Float)msg.signalEnv.get(i)).floatValue() > max ) {
				max = ((Float)msg.signalEnv.get(i)).floatValue();
				t1 = i * (msg.slide/sampleRate);
			}
		}
		
		for( int i = 0; i < msg.signalEnv.size(); i++)
		{
			if( ((Float)msg.signalEnv.get(i)).floatValue() > (thresold*max) ) {
//				float min = ((Float)msg.signalEnv.get(i)).floatValue();
				t0 = i * (float)(msg.slide/sampleRate);
				break;
			}
		}
		
		if ( (t1-t0) < (msg.slide/sampleRate))
			lat = Function.log10(msg.slide/sampleRate);
		else 			
			lat = Function.log10(t1-t0);
		
		send(new MsgLogAttackTime(msg.time, msg.duration, lat));
		
		
	}
}

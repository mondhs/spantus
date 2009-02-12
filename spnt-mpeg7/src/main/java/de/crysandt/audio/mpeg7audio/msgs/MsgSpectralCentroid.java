/*
  Copyright (c) 2004, Michele Bartolucci
 
  This file is part of the MPEG7AudioEnc project.
*/
package de.crysandt.audio.mpeg7audio.msgs;

/**
 * @author <a href="mailto:micky78@email.it">Michele Bartolucci</a>
 */
public class MsgSpectralCentroid extends Msg
{
	
	public int time;
	public int duration;
	public float spectralCentroid;
	
	public MsgSpectralCentroid(int time, int duration, float spectralCentroid)
	{
		super(time, duration);
		this.spectralCentroid = spectralCentroid;
	}
	
}

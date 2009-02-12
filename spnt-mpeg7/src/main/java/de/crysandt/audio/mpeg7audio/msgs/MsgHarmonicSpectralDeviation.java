/*
  Copyright (c) 2004, Michele Bartolucci
  
  This file is part of the MPEG7AudioEnc project.
*/
package de.crysandt.audio.mpeg7audio.msgs;

/**
 * @author <a href="mailto:micky78@email.it">Michele Bartolucci</a>
 */
public class MsgHarmonicSpectralDeviation extends Msg {
	
	public float hsd;
	
	public MsgHarmonicSpectralDeviation(int time, int duration, float hsd) {
		super(time, duration);
		this.hsd = hsd;
		
	}
}

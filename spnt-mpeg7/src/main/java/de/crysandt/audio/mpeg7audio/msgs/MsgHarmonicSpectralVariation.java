/*
  Copyright (c) 2004, Michele Bartolucci
  
  This file is part of the MPEG7AudioEnc project.
*/
package de.crysandt.audio.mpeg7audio.msgs;

/**
 * @author <a href="mailto:micky78@email.it">Michele Bartolucci</a>
 */
public class MsgHarmonicSpectralVariation extends Msg{
	
	public float hsv;
	
	public MsgHarmonicSpectralVariation(int time, int duration, float hsv) {
		super(time, duration);
		this.hsv = hsv;
		
	}
	
}

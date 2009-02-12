/*
  Copyright (c) 2004, Michele Bartolucci
  
  This file is part of the MPEG7AudioEnc project.
*/
package de.crysandt.audio.mpeg7audio.msgs;

/**
 * @author <a href="mailto:micky78@email.it">Michele Bartolucci</a>
 */
public class MsgHarmonicSpectralSpread extends Msg {
	
	public float hss;
	
	public MsgHarmonicSpectralSpread(int time, int duration, float hss) {
		super(time, duration);
		this.hss = hss;
		
	}
}

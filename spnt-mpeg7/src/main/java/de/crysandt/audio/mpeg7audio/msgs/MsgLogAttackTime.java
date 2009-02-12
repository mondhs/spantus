/*
  Copyright (c) 2004, Michele Bartolucci
  
  This file is part of the MPEG7AudioEnc project.
*/
package de.crysandt.audio.mpeg7audio.msgs;

/**
 * @author <a href="mailto:micky78@email.it">Michele Bartolucci</a>
 */
public class MsgLogAttackTime extends Msg {
	
	public float lat;
	
	public MsgLogAttackTime(int time, int duration, float lat) {
		super(time, duration);
		this.lat = lat;
	}
	
}

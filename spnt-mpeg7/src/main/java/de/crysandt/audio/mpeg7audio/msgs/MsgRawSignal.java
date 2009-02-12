/*
 * Created on 27-gen-2004
 * 
 * This file is part of the MPEG7AudioEnc project.
 */
package de.crysandt.audio.mpeg7audio.msgs;

/**
 * @author Administrator
 *
 * To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
public class MsgRawSignal extends Msg {

	/**
	 * @param time
	 * @param duration
	 * @param hopsize
	 */
    
	public float[] signal;
	public MsgRawSignal(int time, int duration, int hopsize) {
		super(time, duration, hopsize);
		
		// TODO Auto-generated constructor stub
	}
	public MsgRawSignal(float[] segnale) {
		super(0, 0, 0); // i suck
		signal=segnale;
	}
	
	
}

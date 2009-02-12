/*
 * Created on 20-nov-2003
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
public class MsgEndOfSignal extends Msg {

	/**
	 * @param time
	 * @param duration
	 */
	public MsgEndOfSignal(int time, int duration) {
		super(time, duration);
	}
}

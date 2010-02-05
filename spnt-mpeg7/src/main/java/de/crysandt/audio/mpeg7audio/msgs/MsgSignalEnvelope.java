/*
  Copyright (c) 2004, Michele Bartolucci
 
  This file is part of the MPEG7AudioEnc project.
*/
package de.crysandt.audio.mpeg7audio.msgs;

import java.util.*;

/**
 * @author <a href="mailto:micky78@email.it">Michele Bartolucci</a>
 */
public class MsgSignalEnvelope 
extends Msg
{
	public ArrayList<?> signalEnv = new ArrayList<Object>();
	public int slide; 
	
	public MsgSignalEnvelope(int time, int duration, ArrayList<?> signalEnv, int slide){
		super(time, duration);
		this.signalEnv = signalEnv;
		this.slide = slide;
		
	}
	
}

/*
  Copyright (c) 2002-2003, Giuliano Marozzi - Mpeg 7 team

  This file is part of the MPEG7AudioEnc project.
*/
package de.crysandt.audio.mpeg7audio.msgs;

public class MsgSilence 
		extends Msg
{
	public final int min_dur;
	public final float conf;

  public MsgSilence(int time, int duration, int min_dur, float conf) 
  {
  	super(time, duration);
    this.min_dur =min_dur;
    this.conf =conf;
  }
}  
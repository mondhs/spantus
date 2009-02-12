/*
  Copyright (c) 2002-2003, Holger Crysandt
 
  This file is part of the MPEG7AudioEnc project.
 */

package de.crysandt.audio.mpeg7audio.msgs;

/**
 * @author <a href="mailto:crysandt@ient.rwth-aachen.de">Holger Crysandt</a>
 */
public class MsgAudioSpectrumFlatness
extends Msg
{
	
	private float[] flatness;
	
	public final float lo_edge;
	public final float hi_edge;
	
	public MsgAudioSpectrumFlatness( int time,
			int duration,
			float[] flatness,
			float   lo_edge,
			float   hi_edge )
	{
		super(time, duration );
		this.flatness = flatness;
		this.lo_edge  = lo_edge;
		this.hi_edge  = hi_edge;
	}
	
	public float[] getFlatness() {
		float[] tmp = new float[flatness.length];
		System.arraycopy(flatness, 0, tmp, 0, flatness.length);
		return tmp;
	}
	
	public int getFlatnessLength() {
		return flatness.length;
	}
}
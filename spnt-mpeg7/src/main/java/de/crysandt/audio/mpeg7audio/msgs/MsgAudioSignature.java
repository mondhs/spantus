/*
  Copyright (c) 2002-2003, Holger Crysandt
 
  This file is part of the MPEG7AudioEnc project.
*/

package de.crysandt.audio.mpeg7audio.msgs;

/**
 * @author <a href="mailto:crysandt@ient.rwth-aachen.de">Holger Crysandt</a>
 */
public class MsgAudioSignature
	extends Msg
{
	private float[] mean;
	private float[] var;
	
	public final float hi_edge;
	public final float lo_edge = 250.0f;
	public final int    decimation;
	
	public MsgAudioSignature(
			int time,
			int duration,
			int hopsize,
			float[] mean,
			float[] var,
			float hi_edge,
			int decimation)
	{
		super(time, duration, hopsize );
		this.mean = mean;
		this.var  = var;
		this.hi_edge = hi_edge;
		this.decimation = decimation;
	}
	
	public float[] getFlatnessMean() {
		return (float[]) mean.clone();
	}
	
	public float[] getFlatnessVariance() {
		return (float[]) var.clone();
	}
	
	public int getLength() {
		return mean.length;
	}
}
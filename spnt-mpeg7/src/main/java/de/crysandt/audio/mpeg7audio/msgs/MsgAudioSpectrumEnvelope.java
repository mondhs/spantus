/*
  Copyright (c) 2002-2003, Holger Crysandt

  This file is part of the MPEG7AudioEnc project.
*/

package de.crysandt.audio.mpeg7audio.msgs;

/**
 * @author <a href="mailto:crysandt@ient.rwth-aachen.de">Holger Crysandt</a>
 */
public class MsgAudioSpectrumEnvelope
extends Msg
{
	private final float[] envelope;
	
	public final float lo_edge;
	public final float hi_edge;
	public final float resolution;
	public final boolean db_scale;
	public final int normalize;
	
	public MsgAudioSpectrumEnvelope(
			int time,
			int duration,
			int hopsize,
			float[] envelope,
			float lo_edge,
			float hi_edge,
			float resolution,
			boolean db_scale, 
			int normalize)
	{
		super(time, duration, hopsize);
		this.envelope = envelope;
		this.lo_edge = lo_edge;
		this.hi_edge = hi_edge;
		this.resolution = resolution;
		this.db_scale = db_scale;
		this.normalize = normalize;
	}
	
	public int getEnvelopeLength() {
		return envelope.length;
	}
	
	public float[] getEnvelope() {
		float[] tmp = new float[envelope.length];
		System.arraycopy(envelope, 0, tmp, 0, envelope.length);
		return tmp;
	}
}

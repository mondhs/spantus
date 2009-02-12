/*
  Copyright (c) 2002-2003, Holger Crysandt
  Contributed by Felix Engel
 
 
  This file is part of the MPEG7AudioEnc project.
*/

/*
 * Created on Nov 7, 2003
 */
package de.crysandt.audio.mpeg7audio.msgs;

/**
 * @author Felix Engel,
 *        <a href="mailto:crysandt@ient.rwth-aachen.de">Holger Crysandt</a><p/>
 *
 * This message contains two matrices:
 * <ol>
 * <li>A basis function matrix of the AudioSpectrumBasisType</li>
 * <li>A projection matrix of the AudioSpectrumBasisType</li>
 * </ol>
 */
public class MsgAudioSpectrumBasisProjection 
	extends Msg 
{
	
	public final float lo_edge;
	public final float hi_edge;
	public final float resolution;
	
	public final float[][] AudioBasis;
	public final float[][] AudioProjection;
	
	public MsgAudioSpectrumBasisProjection(
			int time, 
			int duration,
			int hop_size, 
			float lo_edge,
			float hi_edge,
			float resolution,
			float[][] AudioBasis,
			float[][] AudioProjection)
	{
		super(time, duration, hop_size);
		this.lo_edge = lo_edge;
		this.hi_edge = hi_edge;
		this.resolution = resolution;
		this.AudioBasis = AudioBasis;
		this.AudioProjection = AudioProjection;
	};
	
	public float[][] getBasis() {
		return AudioBasis;
	}
	
	public float[][] getProjection() {
		return AudioProjection;
	}
}

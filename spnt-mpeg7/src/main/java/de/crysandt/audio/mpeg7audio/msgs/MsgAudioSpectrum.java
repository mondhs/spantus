/*
  Copyright (c) 2002-2003, Holger Crysandt

  This file is part of the MPEG7AudioEnc project.
 */

package de.crysandt.audio.mpeg7audio.msgs;

/**
 * @author <a href="mailto:crysandt@ient.rwth-aachen.de">Holger Crysandt</a>
 */
public class MsgAudioSpectrum
	extends Msg
{
	public final int lengthWindow;
	public final int lengthFFT;

	public final float deltaF;
	private final float[] as;

	public MsgAudioSpectrum(int time,
							int duration,
							int lengthWindow,
							int lengthFFT,
							float deltaF,
							float[] as) {
		super(time, duration);
		this.lengthWindow = lengthWindow;
		this.lengthFFT = lengthFFT;
		this.deltaF = deltaF;
		this.as = as;
	}

	public float[] getAudioSpectrum() {
		float[] tmp = new float[as.length];
		System.arraycopy(as, 0, tmp, 0, as.length);
		return tmp;
	}

	public int getAudioSpectrumLength() {
		return as.length;
	}

	public String toString() {
		return super.toString() + "; AudioSpectrum.length: " + as.length;
	}
}

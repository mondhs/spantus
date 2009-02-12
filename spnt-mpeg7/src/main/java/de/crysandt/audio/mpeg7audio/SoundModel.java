/*
  Copyright (c) 2002-2006, Holger Crysandt
 
  This file is part of the MPEG7AudioEnc project.
*/

package de.crysandt.audio.mpeg7audio;

import de.crysandt.audio.mpeg7audio.msgs.*;
import de.crysandt.hmm.HMM;

class SoundModel
extends MsgSpeaker
implements MsgListener
{
	private final int states;
	private final String label;
	
	public SoundModel(int states, String label) {
		this.states = states;
		this.label = label;
	}
	
	public void receivedMsg(Msg m) {
		MsgAudioSpectrumBasisProjection msg = (MsgAudioSpectrumBasisProjection) m;
		
		//    float[][] basis = msg.getBasis();
		float[][] projection = msg.getProjection();
		
		HMM hmm = HMM.createModel(
				states, projection[0].length, projection, HMM.INIT_RELATIVE_TIME);
		
		send(new MsgSoundModel(
				msg.time,
				msg.duration,
				msg.hopsize,
				msg.lo_edge,
				msg.hi_edge,
				msg.resolution,
				msg.AudioBasis,
				hmm,
				label));
	}
}
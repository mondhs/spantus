package org.spantus.work.ui.cmd;

import org.spantus.work.ui.dto.SpantusWorkInfo;
import org.spantus.work.wav.AudioManagerFactory;

public class PlayCmd extends AbsrtactCmd {
	
	
	public String execute(SpantusWorkInfo ctx) {
		AudioManagerFactory.createAudioManager().play(ctx.getProject().getSample().getCurrentFile(), 
				ctx.getProject().getFrom(),
				ctx.getProject().getLength()
				);	
		return null;
	}

}

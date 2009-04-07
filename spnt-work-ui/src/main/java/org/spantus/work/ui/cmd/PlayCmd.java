package org.spantus.work.ui.cmd;

import org.spantus.work.ui.audio.AudioManagerFactory;
import org.spantus.work.ui.dto.SpantusWorkInfo;

public class PlayCmd extends AbsrtactCmd {
	
	
	public String execute(SpantusWorkInfo ctx) {
		AudioManagerFactory.createAudioManager().play(ctx.getProject().getCurrentSample().getCurrentFile(), 
				ctx.getProject().getFrom(),
				ctx.getProject().getLength()
				);	
		return null;
	}

}

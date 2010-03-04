package org.spantus.work.ui.cmd;

import org.spantus.work.ui.dto.SelectionDto;
import org.spantus.work.ui.dto.SpantusWorkInfo;
import org.spantus.work.wav.AudioManagerFactory;

public class PlayCmd extends AbsrtactCmd {
	
	
	public PlayCmd(CommandExecutionFacade executionFacade) {
		super(executionFacade);
	}


	public String execute(SpantusWorkInfo ctx) {
		SelectionDto dto =  (SelectionDto) getCurrentEvent().getValue();
		AudioManagerFactory.createAudioManager().play(ctx.getProject().getSample().getCurrentFile(), 
				dto.getFrom(),
				dto.getLength()
				);	
		return null;
	}

}

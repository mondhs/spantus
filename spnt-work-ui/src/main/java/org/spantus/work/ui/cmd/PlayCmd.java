package org.spantus.work.ui.cmd;

import java.util.Set;

import org.spantus.work.ui.dto.SelectionDto;
import org.spantus.work.ui.dto.SpantusWorkInfo;
import org.spantus.work.wav.AudioManagerFactory;

public class PlayCmd extends AbsrtactCmd {
	
	
	public PlayCmd(CommandExecutionFacade executionFacade) {
		super(executionFacade);
	}

	public Set<String> getExpectedActions() {
		return createExpectedActions(GlobalCommands.sample.play);
	}

	public String execute(SpantusWorkInfo ctx) {
		SelectionDto dto =  (SelectionDto) getCurrentEvent().getValue();
		if(dto == null){
			dto = new SelectionDto();
		}
		AudioManagerFactory.createAudioManager().play(ctx.getProject().getSample().getCurrentFile(), 
				dto.getFrom(),
				dto.getLength()
				);	
		return null;
	}

}

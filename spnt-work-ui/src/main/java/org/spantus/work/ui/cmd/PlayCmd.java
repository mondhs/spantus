package org.spantus.work.ui.cmd;

import java.util.Set;

import org.spantus.exception.ProcessingException;
import org.spantus.work.ui.dto.SelectionDto;
import org.spantus.work.ui.dto.SpantusWorkInfo;
import org.spantus.work.wav.AudioManagerFactory;

public class PlayCmd extends AbsrtactCmd {
	private SelectionDto dto;
	
	public PlayCmd(CommandExecutionFacade executionFacade) {
		super(executionFacade);
		this.dto = new SelectionDto();
	}

	public Set<String> getExpectedActions() {
		return createExpectedActions(
				GlobalCommands.sample.play.name(),
				GlobalCommands.sample.selectionChanged.name()
				);
	}

	public String execute(SpantusWorkInfo ctx) {
		if(GlobalCommands.sample.selectionChanged.name().equals(getCurrentEvent().getCmd())){
			this.dto = ((SelectionDto)getCurrentEvent().getValue());
			return null;
		}
		
		if(dto == null){
			this.dto = new SelectionDto();
		}
		try{
		AudioManagerFactory.createAudioManager().play(ctx.getProject().getSample().getCurrentFile(), 
				dto.getFrom(),
				dto.getLength()
				);	
		}catch (ProcessingException e) {
			error(e.getLocalizedMessage(), ctx);
		}
		return null;
	}

}

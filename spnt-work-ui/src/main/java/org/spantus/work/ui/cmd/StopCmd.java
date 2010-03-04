package org.spantus.work.ui.cmd;

import org.spantus.work.ui.dto.SpantusWorkInfo;

public class StopCmd extends AbsrtactCmd {

	
	
	public StopCmd(CommandExecutionFacade executionFacade) {
		super(executionFacade);
	}

	public String execute(SpantusWorkInfo ctx) {
		ctx.setPlaying(false);
		return null;
	}

}

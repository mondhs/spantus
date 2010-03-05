package org.spantus.work.ui.cmd.file;

import java.util.Set;

import org.spantus.work.ui.cmd.AbsrtactCmd;
import org.spantus.work.ui.cmd.CommandExecutionFacade;
import org.spantus.work.ui.cmd.GlobalCommands;
import org.spantus.work.ui.dto.SpantusWorkInfo;

public class CurrentProjectChangedCmd extends AbsrtactCmd {
	

	
	public CurrentProjectChangedCmd(CommandExecutionFacade executionFacade) {
		super(executionFacade);
	}
	public Set<String> getExpectedActions() {
		return createExpectedActions(GlobalCommands.file.currentProjectChanged);
	}

	public String execute(SpantusWorkInfo ctx) {
		getExecutionFacade().newProject();
		return null;
	}

}

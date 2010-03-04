package org.spantus.work.ui.cmd;

import org.spantus.work.ui.container.ReloadableComponent;
import org.spantus.work.ui.dto.SpantusWorkInfo;

public class ReloadResourcesCmd extends AbsrtactCmd {
	
	

	private ReloadableComponent component;
	
	public ReloadResourcesCmd(CommandExecutionFacade executionFacade) {
		super(executionFacade);
		component = executionFacade;
	}


	
	public String execute(SpantusWorkInfo ctx) {
		component.reload();
//		return GlobalCommands.file.currentSampleChanged.name();
		return null;
	}

}

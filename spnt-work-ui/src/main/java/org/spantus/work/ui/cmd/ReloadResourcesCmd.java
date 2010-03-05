package org.spantus.work.ui.cmd;

import java.util.Set;

import org.spantus.work.ui.container.ReloadableComponent;
import org.spantus.work.ui.dto.SpantusWorkInfo;

public class ReloadResourcesCmd extends AbsrtactCmd {
	
	

	private ReloadableComponent component;
	
	public ReloadResourcesCmd(CommandExecutionFacade executionFacade) {
		super(executionFacade);
		component = executionFacade;
	}
	
	public Set<String> getExpectedActions() {
		return createExpectedActions(GlobalCommands.tool.reloadResources);
	}

	
	public String execute(SpantusWorkInfo ctx) {
		component.reload();
//		return GlobalCommands.file.currentSampleChanged.name();
		return null;
	}

}

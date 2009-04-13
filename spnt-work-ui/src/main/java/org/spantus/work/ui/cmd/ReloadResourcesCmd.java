package org.spantus.work.ui.cmd;

import org.spantus.work.ui.container.ReloadableComponent;
import org.spantus.work.ui.dto.SpantusWorkInfo;

public class ReloadResourcesCmd extends AbsrtactCmd {
	
	private ReloadableComponent component;
	
	public ReloadResourcesCmd(ReloadableComponent component) {
		this.component = component;
	}

	
	public String execute(SpantusWorkInfo ctx) {
		component.reload();
//		return GlobalCommands.file.currentSampleChanged.name();
		return null;
	}

}

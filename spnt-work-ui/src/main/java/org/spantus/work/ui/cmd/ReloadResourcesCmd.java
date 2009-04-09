package org.spantus.work.ui.cmd;

import org.spantus.work.ui.container.ReloadableComponent;
import org.spantus.work.ui.dto.SpantusWorkInfo;

public class ReloadResourcesCmd extends AbsrtactCmd {
	
	private ReloadableComponent component;
//	private CurrentSampleChangedCmd currentSampleChanged;
	
	public ReloadResourcesCmd(ReloadableComponent component) {
		this.component = component;
//		this.currentSampleChanged = currentSampleChanged;
	}

	
	public String execute(SpantusWorkInfo ctx) {
		component.reload();
//		currentSampleChanged.execute(ctx);
		return GlobalCommands.file.currentSampleChanged.name();
	}

}

package org.spantus.work.ui.cmd.file;

import org.spantus.work.ui.cmd.AbsrtactCmd;
import org.spantus.work.ui.cmd.GlobalCommands;
import org.spantus.work.ui.container.ReloadableComponent;
import org.spantus.work.ui.dto.SpantusWorkInfo;

public class CurrentProjectChangedCmd extends AbsrtactCmd {
	
	ReloadableComponent component;
	
	public CurrentProjectChangedCmd(ReloadableComponent component) {
		this.component = component;
	}

	
	public String execute(SpantusWorkInfo ctx) {
		component.reload();
		return GlobalCommands.sample.reloadSampleChart.name();
	}

}

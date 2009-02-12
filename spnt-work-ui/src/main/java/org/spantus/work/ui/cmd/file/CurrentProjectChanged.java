package org.spantus.work.ui.cmd.file;

import org.spantus.work.ui.cmd.AbsrtactCmd;
import org.spantus.work.ui.container.ReloadableComponent;
import org.spantus.work.ui.dto.SpantusWorkInfo;

public class CurrentProjectChanged extends AbsrtactCmd {
	
	ReloadableComponent component;
	
	public CurrentProjectChanged(ReloadableComponent component) {
		this.component = component;
	}

	
	public String execute(SpantusWorkInfo ctx) {
		component.reload();
		return null;
	}

}

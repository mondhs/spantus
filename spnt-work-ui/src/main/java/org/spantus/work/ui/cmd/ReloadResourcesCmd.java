package org.spantus.work.ui.cmd;

import org.spantus.work.ui.container.ReloadableComponent;
import org.spantus.work.ui.dto.SpantusWorkInfo;
import org.spantus.work.ui.dto.SpantusWorkProjectInfo.ProjectTypeEnum;

public class ReloadResourcesCmd extends AbsrtactCmd {
	
	private ReloadableComponent component;
	private CurrentSampleChangedCmd currentSampleChanged;
	
	public ReloadResourcesCmd(ReloadableComponent component, CurrentSampleChangedCmd currentSampleChanged) {
		this.component = component;
		this.currentSampleChanged = currentSampleChanged;
	}

	
	public String execute(SpantusWorkInfo ctx) {
		component.reload();
		currentSampleChanged.execute(ctx);
		return null;
	}

}

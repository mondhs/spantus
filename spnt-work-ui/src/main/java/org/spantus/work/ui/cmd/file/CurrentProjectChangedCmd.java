package org.spantus.work.ui.cmd.file;

import java.awt.Frame;

import org.spantus.work.ui.cmd.AbsrtactCmd;
import org.spantus.work.ui.container.SpantusWorkFrame;
import org.spantus.work.ui.dto.SpantusWorkInfo;

public class CurrentProjectChangedCmd extends AbsrtactCmd {
	
	private SpantusWorkFrame component;
	
	public CurrentProjectChangedCmd(Frame component) {
		this.component = (SpantusWorkFrame)component;
	}

	
	public String execute(SpantusWorkInfo ctx) {
		component.newProject();
		return null;
	}

}

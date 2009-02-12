package org.spantus.work.ui.cmd;

import org.spantus.work.ui.dto.SpantusWorkInfo;

public class StopCmd extends AbsrtactCmd {

	
	public String execute(SpantusWorkInfo ctx) {
		ctx.setPlaying(false);
		return null;
	}

}

package org.spantus.work.ui.cmd;

import org.spantus.work.ui.dto.SpantusWorkInfo;

public abstract class AbsrtactCmd implements SpantusWorkCommand {
	public String execute(String cmdName, SpantusWorkInfo ctx){
		return execute(ctx);
	}
	public abstract String execute(SpantusWorkInfo ctx);
	
}

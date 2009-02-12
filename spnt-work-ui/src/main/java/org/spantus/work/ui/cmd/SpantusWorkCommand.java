package org.spantus.work.ui.cmd;

import org.spantus.work.ui.dto.SpantusWorkInfo;

public interface SpantusWorkCommand {
	public String execute(String cmdName, SpantusWorkInfo ctx);
}

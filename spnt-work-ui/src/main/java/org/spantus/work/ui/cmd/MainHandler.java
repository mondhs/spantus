package org.spantus.work.ui.cmd;

import java.util.HashMap;
import java.util.Map;

import org.spantus.logger.Logger;
import org.spantus.work.ui.dto.SpantusWorkInfo;

public class MainHandler implements SpantusWorkCommand {
	
	Logger log = Logger.getLogger(getClass());
	
	private Map<String, SpantusWorkCommand> cmds;

	public Map<String, SpantusWorkCommand> getCmds() {
		if (cmds == null) {
			cmds = new HashMap<String, SpantusWorkCommand>();
		}
		return cmds;
	}

	
	public String execute(String cmdName, SpantusWorkInfo ctx) {
		log.debug("cmd: " + cmdName);
		if (cmdName == null) {
			return null;
		}
		String newCmdName = getCmds().get(cmdName).execute(cmdName, ctx);
		if (newCmdName == null) {
			return null;
		} else {
			return execute(newCmdName, ctx);
		}

	}

}
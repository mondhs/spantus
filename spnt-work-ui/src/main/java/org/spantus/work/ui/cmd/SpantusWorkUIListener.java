package org.spantus.work.ui.cmd;

import java.util.Map;
import java.util.Set;

import org.spantus.event.SpantusEvent;
import org.spantus.event.SpantusEventListener;
import org.spantus.work.ui.dto.SpantusWorkInfo;

public class SpantusWorkUIListener implements SpantusEventListener {

    Map<String, Set<SpantusWorkCommand>> cmds;
    SpantusWorkInfo info;

    public void onEvent(SpantusEvent event) {
        SpantusWorkUIEvent workUIEvent = new SpantusWorkUIEvent(
                event.getSource(),
                getInfo(), event.getCmd(),
                event.getValue());
        Set<SpantusWorkCommand> currentCmdSet = getCmds().get(event.getCmd());
        if(currentCmdSet==null) return;
        for (SpantusWorkCommand spantusWorkCommand : currentCmdSet) {
        	spantusWorkCommand.execute(workUIEvent);	
		}
        
    }

    public Map<String, Set<SpantusWorkCommand>> getCmds() {
        return cmds;
    }

    public void setCmds(Map<String, Set<SpantusWorkCommand>> cmds) {
        this.cmds = cmds;
    }
    public SpantusWorkInfo getInfo() {
        return info;
    }

    public void setInfo(SpantusWorkInfo info) {
        this.info = info;
    }

}

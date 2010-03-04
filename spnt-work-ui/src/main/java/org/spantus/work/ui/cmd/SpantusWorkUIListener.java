package org.spantus.work.ui.cmd;

import java.util.Map;
import org.spantus.event.SpantusEvent;
import org.spantus.event.SpantusEventListener;
import org.spantus.work.ui.dto.SpantusWorkInfo;

public class SpantusWorkUIListener implements SpantusEventListener {

    Map<String, SpantusWorkCommand> cmds;
    SpantusWorkInfo info;

    public void onEvent(SpantusEvent event) {
        SpantusWorkUIEvent workUIEvent = new SpantusWorkUIEvent(
                event.getSource(),
                getInfo(), event.getCmd(),
                event.getValue());
        getCmds().get(event.getCmd()).execute(workUIEvent);
    }

    public Map<String, SpantusWorkCommand> getCmds() {
        return cmds;
    }

    public void setCmds(Map<String, SpantusWorkCommand> cmds) {
        this.cmds = cmds;
    }
    public SpantusWorkInfo getInfo() {
        return info;
    }

    public void setInfo(SpantusWorkInfo info) {
        this.info = info;
    }

}

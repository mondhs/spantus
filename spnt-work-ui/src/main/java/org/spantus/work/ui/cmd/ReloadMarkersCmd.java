/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.spantus.work.ui.cmd;

import java.util.Set;
import org.spantus.work.ui.dto.SpantusWorkInfo;

/**
 *
 * @author mondhs
 */
public class ReloadMarkersCmd extends AbsrtactCmd {

    public ReloadMarkersCmd(CommandExecutionFacade executionFacade) {
        super(executionFacade);
    }

    @Override
    public String execute(SpantusWorkInfo ctx) {
        //TODO: hack
        ((CommandExecutionFacadeImpl) getExecutionFacade()).updateMarkers();
        return null;
    }

    public Set<String> getExpectedActions() {
        return createExpectedActions(GlobalCommands.sample.reloadMarkers);
    }
}

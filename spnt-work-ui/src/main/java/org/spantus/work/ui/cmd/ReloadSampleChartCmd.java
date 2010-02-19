package org.spantus.work.ui.cmd;

import org.spantus.work.ui.dto.SpantusWorkInfo;

public class ReloadSampleChartCmd extends AbsrtactCmd {
	
	
	private CommandExecutionFacade executionFacade;
	
	public ReloadSampleChartCmd(CommandExecutionFacade executionFacade) {
		this.executionFacade = executionFacade;
	}

	
	public String execute(SpantusWorkInfo ctx) {
		if(ctx.getProject().getSample().getCurrentFile()==null){
			executionFacade.setReader(null);
		}
		executionFacade.updateContent();
		
		return null;
	}

}

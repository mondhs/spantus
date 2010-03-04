package org.spantus.work.ui.cmd;

import org.spantus.work.ui.dto.SpantusWorkInfo;

public class ReloadSampleChartCmd extends AbsrtactCmd {
	
	
	
	public ReloadSampleChartCmd(CommandExecutionFacade executionFacade) {
		super(executionFacade);
	}
	
	public String execute(SpantusWorkInfo ctx) {
		if(ctx.getProject().getSample().getCurrentFile()==null){
			//TODO: hack
			((CommandExecutionFacadeImpl)getExecutionFacade()).setReader(null);
		}
		//TODO: hack
		((CommandExecutionFacadeImpl)getExecutionFacade()).updateContent();
		
		return null;
	}

}

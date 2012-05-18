package org.spantus.work.ui.cmd;

import java.util.Set;

import org.spantus.work.ui.dto.SpantusWorkInfo;

public class ReloadSampleChartCmd extends AbsrtactCmd {
	
	
	
	public ReloadSampleChartCmd(CommandExecutionFacade executionFacade) {
		super(executionFacade);
	}
	
	public Set<String> getExpectedActions() {
		return createExpectedActions(GlobalCommands.sample.reloadSampleChart);
	}
	
	public String execute(SpantusWorkInfo ctx) {
		if(ctx.getProject().getSample().getCurrentFile()==null){
			 //FIXME: hack cast CommandExecutionFacadeImpl#setReader
			((CommandExecutionFacadeImpl)getExecutionFacade()).setReader(null);
		}
		 //FIXME: hack cast CommandExecutionFacadeImpl#updateContent
		((CommandExecutionFacadeImpl)getExecutionFacade()).updateContent();
		
		return null;
	}

}

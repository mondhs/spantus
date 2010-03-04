package org.spantus.work.ui.cmd;

import org.spantus.work.ui.container.panel.OptionDialog;
import org.spantus.work.ui.dto.SpantusWorkInfo;

public class OptionCmd extends AbsrtactCmd{

	private OptionDialog optionDialog;
	private boolean initialized = false;
	

	public OptionCmd(CommandExecutionFacade executionFacade) {
		super(executionFacade);
	}
	
	public String execute(SpantusWorkInfo ctx){
		getOptionDialog().setInfo(ctx);
		if(!initialized){
			getOptionDialog().initialize();	
			initialized = true;
		}else{
			getOptionDialog().reload();
		}
		
		getOptionDialog().setVisible(true);
		return GlobalCommands.tool.reloadResources.name();
	}

	private OptionDialog getOptionDialog(){
		if(optionDialog == null){
			optionDialog = new OptionDialog(null);
		}
		return optionDialog;
	}
}

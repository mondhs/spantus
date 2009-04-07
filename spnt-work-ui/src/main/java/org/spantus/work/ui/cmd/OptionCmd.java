package org.spantus.work.ui.cmd;

import java.awt.Frame;

import org.spantus.work.ui.container.panel.OptionDialog;
import org.spantus.work.ui.dto.SpantusWorkInfo;

public class OptionCmd extends AbsrtactCmd{
	private OptionDialog optionDialog;
	private Frame frame;
	private boolean initialized = false;
	
	public OptionCmd(Frame frame){
		this.frame = frame;
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
//		getOptionDialog().setSize(getOptionDialog().getSize());
		getOptionDialog().invalidate();
		getOptionDialog().repaint(500);
		
		
		return GlobalCommands.tool.reloadResources.name();
	}

	private OptionDialog getOptionDialog(){
		if(optionDialog == null){
			optionDialog = new OptionDialog( frame);
		}
		return optionDialog;
	}
}

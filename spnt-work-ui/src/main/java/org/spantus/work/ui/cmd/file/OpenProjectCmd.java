package org.spantus.work.ui.cmd.file;

import java.io.File;

import javax.swing.JFileChooser;

import org.spantus.logger.Logger;
import org.spantus.work.ui.cmd.AbsrtactCmd;
import org.spantus.work.ui.cmd.CommandExecutionFacade;
import org.spantus.work.ui.cmd.GlobalCommands;
import org.spantus.work.ui.dto.SpantusWorkInfo;
import org.spantus.work.ui.dto.SpantusWorkProjectInfo;
import org.spantus.work.ui.services.WorkUIServiceFactory;

import de.crysandt.util.FileFilterExtension;

public class OpenProjectCmd extends AbsrtactCmd {
	
	

	Logger log = Logger.getLogger(getClass());
	public static final String[] FILES = {"spnt.xml"};
	
	
	public OpenProjectCmd(CommandExecutionFacade executionFacade) {
		super(executionFacade);
	}

	public boolean openProject(SpantusWorkInfo ctx) {
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setFileFilter(new FileFilterExtension(FILES));
		fileChooser.setAcceptAllFileFilterUsed(false);
		fileChooser.setCurrentDirectory(ctx.getProject().getWorkingDir());
		
		int returnValue = fileChooser.showOpenDialog(null);
		if (returnValue == JFileChooser.APPROVE_OPTION) {
			File selectedFile = fileChooser.getSelectedFile();
				if(!selectedFile.exists()) return false;
				SpantusWorkProjectInfo project = 
					WorkUIServiceFactory.createInfoManager().openProject(
						selectedFile.getAbsolutePath());
				ctx.setProject(project);
			return true;
		}
		return false;
	}
	
	public String execute(SpantusWorkInfo ctx) {
		if(openProject(ctx)){
			return GlobalCommands.file.currentProjectChanged.name();
		}
		return null;
	}

}

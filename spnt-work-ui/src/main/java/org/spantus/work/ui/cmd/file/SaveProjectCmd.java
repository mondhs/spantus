package org.spantus.work.ui.cmd.file;

import java.awt.Component;
import java.io.File;
import java.util.Set;

import javax.swing.JFileChooser;

import org.spantus.logger.Logger;
import org.spantus.work.ui.cmd.AbsrtactCmd;
import org.spantus.work.ui.cmd.CommandExecutionFacade;
import org.spantus.work.ui.cmd.GlobalCommands;
import org.spantus.work.ui.dto.SpantusWorkInfo;
import org.spantus.work.ui.services.WorkUIServiceFactory;

import de.crysandt.util.FileFilterExtension;

public class SaveProjectCmd extends AbsrtactCmd {
	
	
	Logger log = Logger.getLogger(getClass());
	public static final String[] FILES = {"spnt.yaml"};

	public SaveProjectCmd(CommandExecutionFacade executionFacade) {
		super(executionFacade);
	}
	
	private Component parent;
	
	public Set<String> getExpectedActions() {
		return createExpectedActions(GlobalCommands.file.saveProject);
	}
	
	public String execute(SpantusWorkInfo ctx) {
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setFileFilter(new FileFilterExtension(FILES));
		fileChooser.setAcceptAllFileFilterUsed(false);
		fileChooser.setCurrentDirectory(ctx.getProject().getWorkingDir());
		
		int returnValue = fileChooser.showSaveDialog(parent);
		if (returnValue == JFileChooser.APPROVE_OPTION) {
			fileChooser.getSelectedFile().getName();
			File selectedFile = fileChooser.getSelectedFile();
			selectedFile = addExtention(selectedFile);
			WorkUIServiceFactory.createInfoManager().saveProject(
					ctx.getProject(),
					selectedFile.getAbsolutePath());
		}
		return null;
	}
	public File addExtention(File selectedFile){
		String fileName = selectedFile.getName();
		File rtnFile = selectedFile;
		if(!fileName.endsWith("."+FILES[0])){
			rtnFile = new File(selectedFile.getParent(), fileName+"."+FILES[0]);
		}
		return rtnFile;
	}

}

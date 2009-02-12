package org.spantus.work.ui.cmd;

import java.io.File;
import java.net.MalformedURLException;

import javax.swing.JFileChooser;

import org.spantus.work.ui.dto.SpantusWorkInfo;
import org.spantus.work.ui.i18n.I18nFactory;

import de.crysandt.util.FileFilterExtension;

public class OpenCmd extends AbsrtactCmd {
	public static final String[] FILES = {"wav"};
	
	public static final String OPEN_DIALOG_TITLE ="spantus.work.ui.sample.open-dialog-title";

	
	public String execute(SpantusWorkInfo ctx) {
		if(importSample(ctx)){
			return GlobalCommands.file.currentSampleChanged.name();
		}
		return null;
		
	}

	public boolean importSample(SpantusWorkInfo ctx) {
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setDialogTitle(getMessage(OPEN_DIALOG_TITLE));
		fileChooser.setFileFilter(new FileFilterExtension(FILES));
		fileChooser.setAcceptAllFileFilterUsed(false);
		fileChooser.setCurrentDirectory(ctx.getProject().getWorkingDir());
		
		int returnValue = fileChooser.showOpenDialog(null);
		if (returnValue == JFileChooser.APPROVE_OPTION) {
			File selectedFile = fileChooser.getSelectedFile();
			try {
				ctx.getProject().getCurrentSample().setCurrentFile(
						selectedFile.toURI().toURL());
				ctx.getProject().setWorkingDir(selectedFile.getParentFile());
			} catch (MalformedURLException e1) {
				e1.printStackTrace();
			}
			return true;
		}
		return false;
	}
	
	public String getMessage(String key){
		return I18nFactory.createI18n().getMessage(key);
	}
	
	

}

/*
 * Part of program for analyze speech signal 
 * Copyright (c) 2008 Mindaugas Greibus (spantus@gmail.com)
 * http://spantus.sourceforge.net
 * 
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the
 * Free Software Foundation; either version 2 of the License, or (at your
 * option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 675 Mass Ave, Cambridge, MA 02139, USA.
 * 
 */
package org.spantus.work.ui.cmd;

import java.io.File;
import java.net.MalformedURLException;

import javax.swing.JFileChooser;

import org.spantus.exception.ProcessingException;
import org.spantus.work.ui.dto.SpantusWorkInfo;
import org.spantus.work.ui.i18n.I18nFactory;
import org.spantus.work.ui.services.WorkInfoManager;
import org.spantus.work.ui.services.WorkUIServiceFactory;

import de.crysandt.util.FileFilterExtension;

public class OpenCmd extends AbsrtactCmd {
	public static final String[] FILES = {"wav", "txt"};
	
	public static final String OPEN_DIALOG_TITLE ="spantus.work.ui.sample.open-dialog-title";
	
	private WorkInfoManager workInfoManager;
	private JFileChooser fileChooser;
	private File defaulDir;
	
	
	public String execute(SpantusWorkInfo ctx) {
		if(importSample(ctx)){
			getWorkInfoManager().increaseExperimentId(ctx);
			return GlobalCommands.file.currentSampleChanged.name();
		}
		return null;
		
	}

	public boolean importSample(SpantusWorkInfo ctx) {
		defaulDir = ctx.getProject().getWorkingDir();
		
		
		
		int returnValue = getFileChooser().showOpenDialog(null);
		if (returnValue == JFileChooser.APPROVE_OPTION) {
			File selectedFile = getFileChooser().getSelectedFile();
			try {
				ctx.getProject().getSample().setCurrentFile(
						selectedFile.toURI().toURL());
				ctx.getProject().setWorkingDir(selectedFile.getParentFile());
			} catch (MalformedURLException e1) {
				throw new ProcessingException(e1);
			}
			return true;
		}
		return false;
	}
	
	protected JFileChooser getFileChooser(){
		if(fileChooser == null){
			fileChooser = new JFileChooser();
			fileChooser.setDialogTitle(getMessage(OPEN_DIALOG_TITLE));
			fileChooser.setFileFilter(new FileFilterExtension(FILES));
			fileChooser.setAcceptAllFileFilterUsed(false);
			fileChooser.setCurrentDirectory(defaulDir);

			fileChooser.setAcceptAllFileFilterUsed(false);
			if(defaulDir != null){
				fileChooser.setCurrentDirectory(defaulDir);
			}
		}
		return fileChooser;
	}
	
	public String getMessage(String key){
		return I18nFactory.createI18n().getMessage(key);
	}
	
	public WorkInfoManager getWorkInfoManager() {
		if(workInfoManager == null){
			workInfoManager = WorkUIServiceFactory.createInfoManager();
		}
		return workInfoManager;
	}

	

}

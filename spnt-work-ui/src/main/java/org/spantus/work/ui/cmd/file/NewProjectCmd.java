package org.spantus.work.ui.cmd.file;

import java.awt.Frame;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.swing.JOptionPane;

import org.spantus.logger.Logger;
import org.spantus.work.ui.cmd.AbsrtactCmd;
import org.spantus.work.ui.cmd.GlobalCommands;
import org.spantus.work.ui.dto.SpantusWorkInfo;
import org.spantus.work.ui.dto.SpantusWorkProjectInfo.ProjectTypeEnum;
import org.spantus.work.ui.i18n.I18nFactory;
import org.spantus.work.ui.services.WorkUIServiceFactory;

public class NewProjectCmd extends AbsrtactCmd {
	
	Logger log = Logger.getLogger(getClass());
	
	enum labels{newProjectTitle,newProjectMessage};
	
	public static final String RESOURCE_PREFIX  = "spantus.work.ui.project.type.";

	private Frame frame;
	
	public NewProjectCmd(Frame frame){
		this.frame = frame;
	}
	
	public boolean newProject(SpantusWorkInfo info) {
		Map<String,ProjectTypeEnum> selectionMap = new LinkedHashMap<String, ProjectTypeEnum>();
		for (ProjectTypeEnum projectType : ProjectTypeEnum.values()) {
			selectionMap.put(getMessage(RESOURCE_PREFIX + projectType.name()), projectType);
		}
		String current = getMessage(RESOURCE_PREFIX + info.getProject().getType());
		
	    String projectType = (String)JOptionPane.showInputDialog(
	                        frame,
	                        getMessage(labels.newProjectMessage.name()),
	                        getMessage(labels.newProjectTitle.name()),
	                        JOptionPane.QUESTION_MESSAGE,
	                        null,
	                        selectionMap.keySet().toArray(),
	                        current);

	    if ((projectType != null) && (projectType.length() > 0)) {
//	    	ctx.setProjectType(selectionMap.get(projectType).name());
//	    	ctx.setWorkingDir(info.getProject().getWorkingDir());
//	    	ctx.setExperimentId(info.getProject().getExperimentId());
	    	info.setProject(WorkUIServiceFactory.createInfoManager().newProject(info.getProject(),
	    			selectionMap.get(projectType).name() ));
	    	return true;
	    }

		return false;
	}
	
	public String execute(SpantusWorkInfo ctx) {
		if(newProject(ctx)){
			return GlobalCommands.file.currentProjectChanged.name();
		}
		return null;
	}
	
	public String getMessage(String key){
		return I18nFactory.createI18n().getMessage(key);
	}

}

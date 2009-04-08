package org.spantus.work.ui.services;

import org.spantus.work.ui.dto.SpantusWorkInfo;
import org.spantus.work.ui.dto.SpantusWorkProjectInfo;

public interface WorkInfoManager {
	public SpantusWorkInfo newWorkInfo();
	public SpantusWorkInfo openWorkInfo();
	public void saveWorkInfo(SpantusWorkInfo info);
	public SpantusWorkProjectInfo newProject(SpantusWorkProjectInfo oldProject, String type);
	public void saveProject(SpantusWorkProjectInfo project, String path);
	public SpantusWorkProjectInfo openProject(String path);
	public String increaseExperimentId(SpantusWorkInfo info);
}

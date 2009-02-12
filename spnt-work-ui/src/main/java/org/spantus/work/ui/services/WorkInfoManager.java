package org.spantus.work.ui.services;

import org.spantus.work.ui.dto.NewProjectContext;
import org.spantus.work.ui.dto.SpantusWorkInfo;
import org.spantus.work.ui.dto.SpantusWorkProjectInfo;

public interface WorkInfoManager {
	public SpantusWorkInfo newWorkInfo();
	public SpantusWorkInfo openWorkInfo();
	public void saveWorkInfo(SpantusWorkInfo info);
	public SpantusWorkProjectInfo newProject(NewProjectContext ctx);
	public void saveProject(SpantusWorkProjectInfo project, String path);
	public SpantusWorkProjectInfo openProject(String path);
}

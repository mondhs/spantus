
/**
 * Part of program for analyze speech signal 
 * Copyright (c) 2008 Mindaugas Greibus (spantus@gmail.com)
 * http://code.google.com/p/spantus/
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
package org.spantus.work.ui.services;

import java.io.File;
import java.util.Locale;

import org.spantus.work.WorkReadersEnum;
import org.spantus.work.ui.dto.NewProjectContext;
import org.spantus.work.ui.dto.SpantusWorkInfo;
import org.spantus.work.ui.dto.SpantusWorkProjectInfo;
import org.spantus.work.ui.dto.WorkUIExtractorConfig;
/**
 * 
 * 
 * @author Mindaugas Greibus
 *
 * @since 0.0.1
 * 
 * Created Aug 26, 2008
 *
 */
public abstract class AbstractWorkInfoManager implements WorkInfoManager {

	
	public SpantusWorkInfo openWorkInfo() {
		return newWorkInfo();
	}

	
	public void saveWorkInfo(SpantusWorkInfo info) {

	}


	public SpantusWorkInfo newWorkInfo() {
		SpantusWorkInfo info = new SpantusWorkInfo();
		info.setLocale(Locale.getDefault());
		SpantusWorkProjectInfo project = createProject();
		info.setProject(project);
		return info;
	}


	public SpantusWorkProjectInfo newProject(NewProjectContext ctx) {
		SpantusWorkProjectInfo project = createProject();
		project.setWorkingDir(ctx.getWorkingDir());
		project.setCurrentType(ctx.getProjectType());
		return project;
	}
	
	protected SpantusWorkProjectInfo createProject(){
		SpantusWorkProjectInfo project = new SpantusWorkProjectInfo();
		project.setWorkingDir(new File("."));
		project.getFeatureReader().setReaderPerspective(WorkReadersEnum.multiFeature);
		project.getFeatureReader().setWorkConfig(new WorkUIExtractorConfig());
		return project;
	}

}

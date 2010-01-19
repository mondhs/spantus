
/**
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
package org.spantus.work.ui.services;

import java.io.File;
import java.text.MessageFormat;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.spantus.extractor.impl.ExtractorEnum;
import org.spantus.logger.Logger;
import org.spantus.utils.StringUtils;
import org.spantus.work.WorkReadersEnum;
import org.spantus.work.reader.SupportableReaderEnum;
import org.spantus.work.ui.dto.SpantusWorkInfo;
import org.spantus.work.ui.dto.SpantusWorkProjectInfo;
import org.spantus.work.ui.dto.WorkUIExtractorConfig;
import org.spantus.work.ui.dto.SpantusWorkProjectInfo.ProjectTypeEnum;
import org.spantus.work.ui.i18n.I18n;
import org.spantus.work.ui.i18n.I18nFactory;
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
	public static final String EXPERIMENT = "Experiment";
	private Logger log = Logger.getLogger(this.getClass());
	
	
	public SpantusWorkInfo openWorkInfo() {
		return newWorkInfo();
	}

	
	public void saveWorkInfo(SpantusWorkInfo info) {

	}


	public SpantusWorkInfo newWorkInfo() {
		SpantusWorkInfo info = new SpantusWorkInfo();
		Locale currentLocale = null;
		for (Locale ilocale : I18n.LOCALES) {
			if(ilocale.equals(Locale.getDefault())){
				currentLocale = ilocale;
				break;
			}
		}
		currentLocale = currentLocale == null? I18n.LOCALES[1]:currentLocale;
		info.setLocale(currentLocale);
		SpantusWorkProjectInfo project = createProject();
		info.setProject(project);
		return info;
	}


	public SpantusWorkProjectInfo newProject(SpantusWorkProjectInfo oldProject, String type) {
		SpantusWorkProjectInfo project = createProject();
		project.setWorkingDir(oldProject.getWorkingDir());
		project.setExperimentId(oldProject.getExperimentId());
		project.setCurrentType(type);
		project.setThresholdType(oldProject.getThresholdType());
		switch (ProjectTypeEnum.valueOf(type)) {
		case feature:
		case segmenation:
		case recordSegmentation:
			project.getFeatureReader().getExtractors().clear();
			project.getFeatureReader().getExtractors().add(
					SupportableReaderEnum.spantus.name() + ":" + 
					ExtractorEnum.WAVFORM_EXTRACTOR.name());
			project.getFeatureReader().getExtractors().add(
					SupportableReaderEnum.spantus.name() + ":" + 
					ExtractorEnum.ENERGY_EXTRACTOR.name());
			project.getFeatureReader().getExtractors().add(
					SupportableReaderEnum.spantus.name() + ":" + 
					ExtractorEnum.SIGNAL_ENTROPY_EXTRACTOR.name());
			break;
		default:
			break;
		}
		return project;
	}
	
	protected SpantusWorkProjectInfo createProject(){
		SpantusWorkProjectInfo project = new SpantusWorkProjectInfo();
		project.setWorkingDir(new File("."));
		project.getFeatureReader().setReaderPerspective(WorkReadersEnum.multiFeature);
		project.getFeatureReader().setWorkConfig(new WorkUIExtractorConfig());
		initializeExperimentId(project);
		return project;
	}
	/**
	 * 
	 * @param info
	 */
	protected void updateOnLoad(SpantusWorkInfo info){
		initializeExperimentId(info.getProject());
//		if(!StringUtils.hasText(info.getProject().getThresholdType())){
//			info.getProject().setThresholdType(ThresholdEnum.online.name());
//		}

	}
	
	protected void initializeExperimentId(SpantusWorkProjectInfo project){
		if(!StringUtils.hasText(project.getExperimentId())){
			String experiment = getI18n().getMessage(EXPERIMENT);
			project.setExperimentId(MessageFormat.format("{0}_{1}", experiment,1));
		}
	}
	
	public String increaseExperimentId(SpantusWorkInfo info){
		String experimentId = info.getProject().getExperimentId();
		if(!StringUtils.hasText(experimentId)){
			experimentId = I18nFactory.createI18n().getMessage("Experiment");
		}
		Pattern pattern = Pattern.compile("(.*?)(\\d+)(.*?)");
		Matcher matcher = pattern.matcher(experimentId);
		if(matcher.matches()){
			String idStr = matcher.group(2);
			Integer id = 0;
			try{
				id= Integer.valueOf(idStr);
			}catch (NumberFormatException e) {
				log.error(e);
			}
//			log.error("id: "+id);
			id++;
			experimentId = matcher.replaceAll("$1"+id.toString()+"$3");
			info.getProject().setExperimentId(experimentId); 
		}else{
			info.getProject().setExperimentId(experimentId+"_1");
		}
		
		return info.getProject().getExperimentId();
		
	}
	
	protected I18n getI18n(){
		return I18nFactory.createI18n();
	}

}

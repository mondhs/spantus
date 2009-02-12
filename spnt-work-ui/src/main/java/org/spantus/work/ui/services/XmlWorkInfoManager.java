package org.spantus.work.ui.services;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import org.spantus.logger.Logger;
import org.spantus.work.ui.dto.FeatureReader;
import org.spantus.work.ui.dto.SpantusWorkInfo;
import org.spantus.work.ui.dto.SpantusWorkProjectInfo;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.enums.EnumConverter;

public class XmlWorkInfoManager extends AbstractWorkInfoManager{
	
	Logger log = Logger.getLogger(getClass()); 
	

	public static final String FILE_NAME = ".spnt.config.xml"; 
	public static final String WORKING_DIR = "workingDir"; 
	public static final String CONFIG = "config"; 
	public static final String PROJECT = "project";
	public static final String SPANTUS_WORK_INFO = "spantusWorkInfo";
	public String configPath;

	
	public SpantusWorkInfo openWorkInfo() {
		SpantusWorkInfo info = new SpantusWorkInfo();
		try {
			FileReader inFile = new FileReader(getConfigPath()+FILE_NAME);
			getXsteam().fromXML(inFile, info);
			log.debug("Config file read correctly. info: " + info.getProject().getWorkingDir());
		} catch (NullPointerException e) {
			log.debug("Problem with loading " + e.getMessage());
			log.debug("Config file not read. Create new one.");
			info = newWorkInfo();
		} catch (RuntimeException e) {	
			log.debug("Problem with loading " + e.getMessage());
			log.debug("Config file not read. Create new one.");
			info = newWorkInfo();
		} catch (IOException e) {
			log.debug("Problem with loading " + e.getMessage());
			log.debug("Config file not read. Create new one.");
			info = newWorkInfo();
		}
		return info;
	}

	public void saveWorkInfo(SpantusWorkInfo info) {
		try {
			FileWriter outputFile = new FileWriter(getConfigPath()+FILE_NAME,false);	
			getXsteam().toXML(info, outputFile);
			log.debug("Config file is saved.");
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	XStream xstream = null;
	private XStream getXsteam(){
		if(xstream == null){
			xstream = new XStream();
			xstream.alias(SPANTUS_WORK_INFO, SpantusWorkInfo.class);
			xstream.alias(CONFIG, FeatureReader.class);
			xstream.alias(WORKING_DIR, File.class);
			xstream.alias(PROJECT, SpantusWorkProjectInfo.class);
			xstream.registerConverter(new EnumConverter());
		}
		return xstream;
	}

	public String getConfigPath() {
		if(configPath == null){
			 configPath = "";
		}
		return configPath;
	}

	public void setConfigPath(String configPath) {
		this.configPath = configPath;
	}

	public SpantusWorkProjectInfo openProject(String path) {
		SpantusWorkProjectInfo project = new SpantusWorkProjectInfo();
		try {
			FileReader inFile = new FileReader(path);
			getXsteam().fromXML(inFile, project);
			log.debug("Project file read correctly. info: " + project.getWorkingDir());
		} catch (FileNotFoundException e) {
			log.debug("Project file not found: " + path);
			project = null;
		}

		return project;
	}

	public void saveProject(SpantusWorkProjectInfo project, String path) {
		try {
			FileWriter outputFile = new FileWriter(path, false);	
			getXsteam().toXML(project, outputFile);
			log.debug("Project file is saved.");
		} catch (IOException e) {
			e.printStackTrace();
		}		
	}
}

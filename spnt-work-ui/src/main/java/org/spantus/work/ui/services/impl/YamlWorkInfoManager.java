package org.spantus.work.ui.services.impl;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import org.spantus.logger.Logger;
import org.spantus.work.ui.dto.SpantusWorkInfo;
import org.spantus.work.ui.dto.SpantusWorkProjectInfo;

import javax.sound.sampled.AudioFormat;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

// import com.thoughtworks.xstream.XStream;
// import com.thoughtworks.xstream.converters.enums.EnumConverter;

public class YamlWorkInfoManager extends AbstractWorkInfoManager{
	
	Logger log = Logger.getLogger(getClass()); 
	

	public static final String FILE_NAME = ".spnt.config.yaml";
	public static final String WORKING_DIR = "workingDir"; 
	public static final String CONFIG = "config"; 
	public static final String PROJECT = "project";
	public static final String SPANTUS_WORK_INFO = "spantusWorkInfo";
	public String configPath;

	
	public SpantusWorkInfo openWorkInfo() {

            SpantusWorkInfo info = new SpantusWorkInfo();
		try {
			FileReader inFile = new FileReader(getConfigPath()+FILE_NAME);
			info = getObjectMapper().readValue(inFile, SpantusWorkInfo.class);
			log.debug("Config file read correctly. info: " + info.getProject().getWorkingDir());
		} catch (NullPointerException e) {
			log.debug("Problem with loading " + e.getMessage());
			log.debug("Config file not read. Create new one.");
			info = newWorkInfo();
		} catch (RuntimeException e) {	
                        log.error(e);
			log.debug("Problem with loading " + e.getMessage());
			log.debug("Config file not read. Create new one.");
			info = newWorkInfo();
		} catch (IOException e) {
			log.error(e);
			log.debug("Problem with loading " + e.getMessage());
			log.debug("Config file not read. Create new one.");
			info = newWorkInfo();
		}
		updateOnLoad(info);
		return info;
	}

	public void saveWorkInfo(SpantusWorkInfo info) {
		try {
			FileWriter outputFile = new FileWriter(getConfigPath()+FILE_NAME,false);
			info.getProject().getSample().getMarkerSetHolder().getMarkerSets().clear();
			getObjectMapper().writeValue(outputFile, info);
			log.debug("Config file is saved.");
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	// XStream xstream = null;
	// private XStream getXsteam(){
	// 	if(xstream == null){
	// 		xstream = new XStream();
	// 		xstream.alias(SPANTUS_WORK_INFO, SpantusWorkInfo.class);
	// 		xstream.alias(CONFIG, FeatureReader.class);
	// 		xstream.alias(WORKING_DIR, File.class);
	// 		xstream.alias(PROJECT, SpantusWorkProjectInfo.class);
	// 		xstream.registerConverter(new EnumConverter());
    //                     xstream.registerConverter(new EncodingConverter());
	// 	}
	// 	return xstream;
	// }

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
			project = getObjectMapper().readValue(inFile, SpantusWorkProjectInfo.class);
			// getXsteam().fromXML(inFile, project);
			log.debug("Project file read correctly. info: " + project.getWorkingDir());
		} catch (FileNotFoundException e) {
			log.debug("Project file not found: " + path);
			project = null;
		} catch (JsonParseException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return project;
	}

	public void saveProject(SpantusWorkProjectInfo project, String path) {
		try {
			FileWriter outputFile = new FileWriter(path, false);
			getObjectMapper().writeValue(outputFile, project);
			log.debug("Project file is saved.");
		} catch (IOException e) {
			e.printStackTrace();
		}		
	}

	private ObjectMapper getObjectMapper(){
		ObjectMapper om = new ObjectMapper(new YAMLFactory());
		SimpleModule module = new SimpleModule();
		module.addSerializer(AudioFormat.Encoding.class, new EncodingSerializer());
		module.addDeserializer(AudioFormat.Encoding.class, new EncodingDeserializer());
		om.registerModule(module);
		return om;
	}
}

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
package org.spantus.exp.segment.exec.multi;

import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

import org.spantus.core.extractor.IGeneralExtractor;
import org.spantus.exp.segment.beans.ProcessReaderInfo;
import org.spantus.exp.segment.services.ExpServiceFactory;
import org.spantus.exp.segment.services.ExperimentDao;
import org.spantus.exp.segment.services.ProcessReader;
import org.spantus.exp.segment.services.impl.ExperimentStaticDao;
import org.spantus.logger.Logger;

public class ExperimentResourceMonitor {
	
	public final static String[] testPaths = new String[] {
//		 "",
		"org_",
		 "plane_",
		 "rain_",
		 "shower_",
		 "trafic_",
		 "white_",
		};
	
	public final static String expertMarksPath = "_on_off_up_down.mspnt.xml";
	
	public final static String sufix = "on_off_up_down.sspnt.xml";
	public final static Long expID = 1L;
	public final static Double thresholdCoef = 1.6D;

	
	private Iterator<String> storage;
	private Map<String, Set<String>> compbinations;
	private ProcessReader processReader;
	private ExperimentDao experimentDao;
	private String localPathToResources = "C:/home/studijos/wav/on_off_up_down_wav/exp/";
	private Integer combinationDepth = null;
	protected Logger log = Logger.getLogger(getClass());
	
	public ExperimentResourceMonitor() {
		LinkedHashSet<String> storageSet = new LinkedHashSet<String>();
		storageSet.addAll(Arrays.asList(testPaths));
		storage = storageSet.iterator();
	}
	
	public String constructExpertMarksPath(String resourceName){
		return localPathToResources + expertMarksPath;
	}
	public String constructTestPath(String resourceName){
		log.info("constructTestPath: " + resourceName);
		return localPathToResources + resourceName + sufix;
	}
	
	public String constructExperimentName(String resourceName){
		return resourceName;
	}
	public Long constructExperimentID(String resourceName){
		return 1L;
	}
	
	
	public ProcessReaderInfo createProcessReaderInfo(String resourceName){
		return new ProcessReaderInfo(thresholdCoef);
	}
	public Map<String, Set<String>> createCombinations(String resourceName, Set<? extends IGeneralExtractor> extractors){
		if(compbinations == null){
			compbinations = 
				getProcessReader().generateAllCompbinations(extractors, combinationDepth);

		}
		return compbinations;
	}
	
	
	
	public synchronized String popResource(){
		try{
			return getStorageSet().next();
		}catch (NoSuchElementException e) {
			log.info("[popResource] nothing to process");
			return null;
		}
	}
	public  boolean isEmpty(){
		return getStorageSet().hasNext();
	}

	public synchronized Iterator<String> getStorageSet() {
		return storage;
	}

	public ProcessReader getProcessReader() {
		if(processReader == null){
			processReader = ExpServiceFactory.createProcessReader();
		}
		return processReader;
	}

	public ExperimentDao getExperimentDao() {
		if(experimentDao == null){
			experimentDao = new ExperimentStaticDao();
		}
		return experimentDao;
	}

	public void setExperimentDao(ExperimentDao experimentDao) {
		this.experimentDao = experimentDao;
	}

	public String getLocalPathToResources() {
		return localPathToResources;
	}

	public void setLocalPathToResources(String localPathToResources) {
		this.localPathToResources = localPathToResources;
	}

	public Integer getCombinationDepth() {
		return combinationDepth;
	}

	public void setCombinationDepth(Integer combinationDepth) {
		this.combinationDepth = combinationDepth;
	}
	
}

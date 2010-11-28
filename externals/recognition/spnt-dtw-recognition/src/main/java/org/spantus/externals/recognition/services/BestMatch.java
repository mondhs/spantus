/*
 	Copyright (c) 2009 Mindaugas Greibus (spantus@gmail.com)
 	Part of program for analyze speech signal 
 	http://spantus.sourceforge.net

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>
*/
package org.spantus.externals.recognition.services;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.spantus.core.FrameVectorValues;
import org.spantus.logger.Logger;

public class BestMatch {
	
	private CompareFeatures compareFeatures;
	
	private List<File> sampleRepo;
	private Logger log = Logger.getLogger(getClass());
	private String rootPath = 
//		"./ijunk_ishjunk/";
		"./sviesa_isjunk_viskas/";

	public void findBestMatch(File targetFile){
		Map<Float, String> result = new TreeMap<Float, String>();
		Float minDistance = Float.MAX_VALUE;
		String minDisntanceName = "";
		for (File sampleFile : getSampleRepo()) {
			Float distance = getCompareFeatures().compareValues(sampleFile, targetFile);
			result.put(distance, sampleFile.getName());
			if(Math.min(minDistance, distance) == distance && distance != 0){
				minDistance = distance;
				minDisntanceName = sampleFile.getName();
			}
		}
		log.debug("[findBestMatch] sample: {0};[{1}]", minDisntanceName, result);
	}
	
	public String findBestMatch(FrameVectorValues targetValues){
		Map<Float, String> result = new TreeMap<Float, String>();
		Float minDistance = Float.MAX_VALUE;
		String minDisntanceName = "";
		for (File sampleFile : getSampleRepo()) {
			Float distance = getCompareFeatures().compareValues(targetValues, sampleFile);
			result.put(distance, sampleFile.getName());
			if(Math.min(minDistance, distance) == distance && distance != 0){
				minDistance = distance;
				minDisntanceName = sampleFile.getName();
			}
		}
		log.debug("[findBestMatch] sample: {0};[{1}]", minDisntanceName, result);
		return minDisntanceName;
	}
	
		
	public CompareFeatures getCompareFeatures() {
		if(compareFeatures == null){
			compareFeatures = new CompareFeaturesCachable();
		}
		return compareFeatures;
	}

	public String getRootPath() {
		return rootPath;
	}


	public void setRootPath(String rootPath) {
		this.rootPath = rootPath;
	}


	public List<File> getSampleRepo() {
		if(sampleRepo == null){
			sampleRepo = new ArrayList<File>();
			File repoDir = new File(getRootPath());
			if(repoDir.isDirectory()){
				for (String fileName : repoDir.list()) {
					if(fileName.endsWith(".sspnt.xml")){
						sampleRepo.add(new File(repoDir,fileName));
					}
				}
			}
		}
		return sampleRepo;
	}

}

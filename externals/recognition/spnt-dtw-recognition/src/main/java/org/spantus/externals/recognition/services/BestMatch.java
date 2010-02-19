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
	
	
	public static void main(String[] args) {
		BestMatch bestMatch = new BestMatch();
		File targetFile = new File("./target/11118.sspnt.xml");
		bestMatch.findBestMatch(targetFile);
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

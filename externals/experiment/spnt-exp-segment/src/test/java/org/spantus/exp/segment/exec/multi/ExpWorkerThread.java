package org.spantus.exp.segment.exec.multi;

import java.util.Map;
import java.util.Set;

import org.spantus.core.beans.SampleInfo;

public class ExpWorkerThread extends Thread {
	private boolean process = true;
	
	ExperimentResourceMonitor monitor;
	
	
	public ExpWorkerThread(ExperimentResourceMonitor monitor) {
		super();
		this.monitor = monitor;
	}

	public void run(){
		while(process){
			String resourceName = monitor.popResource();
			if(resourceName == null){
				process = false;
				continue;
			}
			MultiFeatureSelectionExp exp = new MultiFeatureSelectionExp();
			exp.setExpertMarksPath(monitor.constructExpertMarksPath(resourceName));
			exp.setTestPath(monitor.constructTestPath(resourceName));
			exp.setExpertMS(exp.getWordMarkerSet(exp.getExpertMarkerSet()));
			SampleInfo sampleInfo = exp.getProcessReader().processReader(
					exp.getTestReader(),monitor.createProcessReaderInfo(resourceName));
			
			Map<String, Set<String>> compbinations = monitor.createCombinations(
					resourceName, sampleInfo.getThresholds());
			exp.setCompbinations(compbinations);
			exp.setInfo(sampleInfo);
			//bulk
			exp.setExperimentDao(monitor.getExperimentDao());
			exp.setExperimentName(monitor.constructExperimentName(resourceName));
			exp.setExperimentID(monitor.constructExperimentID(resourceName));
			exp.setGenerateCharts(false);

			
			exp.process();
		}
	}

}

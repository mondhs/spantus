package org.spantus.exp.segment.exec.multi;

import org.spantus.exp.segment.services.impl.ExperimentHsqlDao;

public class BulkFeatureSelectionExp  {


	public static void main(final String[] args) {
		ExperimentResourceMonitor monitor = new ExperimentResourceMonitor();
		if (args.length > 0) {
			monitor.setLocalPathToResources(args[0]);
		}
		
		ExperimentHsqlDao experimentDao = new ExperimentHsqlDao(); 
		experimentDao.init();
		monitor.setExperimentDao(experimentDao);
		monitor.setCombinationDepth(6);
		
		for (int i = 0; i < 1; i++) {
			ExpWorkerThread workerThread = new ExpWorkerThread(monitor);
			workerThread.start();
			
		}
	
		System.out.println("done");
		
//		experimentDao.destroy();
	}

	
	
	
}

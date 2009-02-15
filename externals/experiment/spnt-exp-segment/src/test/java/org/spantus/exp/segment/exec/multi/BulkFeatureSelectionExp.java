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
		monitor.setCombinationDepth(2);
		
		for (int i = 0; i < 1; i++) {
			ExpWorkerThread workerThread = new ExpWorkerThread(monitor);
			workerThread.start();
			
		}
	
		System.out.println("done");
		
//		experimentDao.destroy();
	}

	
	
	
}

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
package org.spantus.math.services;

import org.spantus.math.cluster.ClusterService;
import org.spantus.math.cluster.KNNServiceImpl;
import org.spantus.math.dtw.DtwService;
import org.spantus.math.dtw.DtwServiceJavaMLImpl;
import org.spantus.math.dtw.DtwServiceJavaMLImpl.JavaMLSearchWindow;
import org.spantus.math.services.impl.ConvexHullServiceImpl;

import edu.cmu.sphinx.frontend.frequencywarp.PLPCepstrumProducer;

/**
 * 
 * @author Mindaugas Greibus
 * 
 * @since 0.0.1
 * 
 *        Created 2008.09.28
 * 
 */
public abstract class MathServicesFactory {
	static FFTService fftService;
	static MFCCService mfccService;
	static LPCService lpcService;
	static DtwService dtwService;
	static ClusterService knnService;
	static ConvexHullService convexHullService;

	public static FFTService createFFTService() {
		if (fftService == null) {
//			 fftService = new FFTServiceImpl();
			fftService = new FFTServiceSphinxImpl();
		}
		return fftService;
	}

	public static MFCCService createMFCCService() {
		// if(mfccService == null){
		// // mfccService = new MFCCKlautauServiceImpl();
		// mfccService = new MFCCServiceSphinxImpl();
		// // return new MFCCServiceImpl();
		// }
//		return new MFCCServiceImpl();
//		return new MFCCKlautauServiceImpl();
		return new MFCCServiceSphinxImpl();
	}

	public static PLPService createPLPService() {
		// if(mfccService == null){
		// // mfccService = new MFCCKlautauServiceImpl();
		// mfccService = new MFCCServiceSphinxImpl();
		// // return new MFCCServiceImpl();
		// }
		int numbersOfFilters = 32;
		PLPServiceSphinxImpl plpServiceSphinxImpl = new PLPServiceSphinxImpl(
				numbersOfFilters);
		plpServiceSphinxImpl.setPlpCepstrumProducer(new PLPCepstrumProducer(
				numbersOfFilters, 13, 14));
		return new PLPServiceSphinxImpl();
	}

	public static DtwService createDtwService() {
		if (dtwService == null) {
			DtwServiceJavaMLImpl dtwServiceImpl = new DtwServiceJavaMLImpl();

			dtwService = dtwServiceImpl;
			// return new DtwServiceImpl();
		}
		return dtwService;
	}

	public static DtwService createDtwService(Integer searchRadius, JavaMLSearchWindow javaMLSearchWindow) {
		DtwServiceJavaMLImpl dtwServiceImpl = new DtwServiceJavaMLImpl();
		dtwServiceImpl.setSearchRadius(searchRadius);
		dtwServiceImpl.setSearchWindow(javaMLSearchWindow);
		return dtwServiceImpl;
	}

	public static LPCService createLPCService() {
		if (lpcService == null) {
			lpcService = new LPCServiceImpl();
		}
		return lpcService;
	}

	public static ClusterService createKnnService() {
		if (knnService == null) {
			knnService = new KNNServiceImpl();
		}
		return knnService;
	}
	
	public static ConvexHullService createConvexHullService() {
		if (convexHullService == null) {
			convexHullService = new ConvexHullServiceImpl();
		}
		return convexHullService;
	}
	
}

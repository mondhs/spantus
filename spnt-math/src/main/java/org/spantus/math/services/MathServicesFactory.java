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

import org.spantus.math.dtw.DtwService;
import org.spantus.math.dtw.DtwServiceStansalvadorImpl;

/**
 * 
 * @author Mindaugas Greibus
 *
 * @since 0.0.1
 * 
 * Created 2008.09.28
 *  
 */
public abstract class MathServicesFactory {
	static FFTService fftService;
	static MFCCService mfccService;
	static LPCService lpcService;
	static DtwService dtwService;
	
	public static FFTService createFFTService(){
		if(fftService == null){
			fftService = new FFTServiceImpl();
		}
		return fftService;
	}
	public static MFCCService createMFCCService(){
		if(mfccService == null){
			mfccService = new MFCCKlautauServiceImpl();
//			return new MFCCServiceImpl();
		}
		return mfccService;
	}

	public static DtwService createDtwService(){
		if(dtwService == null){
			dtwService = new DtwServiceStansalvadorImpl();
//			return new DtwServiceImpl();
		}
		return dtwService;
	}
	
	public static LPCService createLPCService(){
		if(lpcService == null){
			lpcService = new LPCServiceImpl();
		}
		return lpcService;
	}
	
}

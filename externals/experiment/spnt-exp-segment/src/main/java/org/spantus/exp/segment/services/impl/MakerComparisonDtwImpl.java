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
package org.spantus.exp.segment.services.impl;

import org.spantus.core.FrameValues;
import org.spantus.exp.segment.beans.ComparisionResult;
import org.spantus.math.DTW;
import org.spantus.math.dtw.DtwInfo;
import org.spantus.math.dtw.DtwInfo.DtwType;
/**
 * 
 * @author Mindaugas Greibus
 * 
 * @since 0.0.1
 * Created Feb 12, 2009
 *
 */
public class MakerComparisonDtwImpl extends MakerComparisonImpl{
	/** 
	 * DTW
	 */
	
	protected FrameValues compare(ComparisionResult result){
		FrameValues seq = super.compare(result);
		DtwInfo info =  DTW.createDtwInfo(result.getOriginal(), result.getTest());
		info.setType(DtwType.typeII);
//		DrawDtw dtwDraw = new DrawDtw(info);
//		dtwDraw.showChart();
//		
		double resultDtw = DTW.estimate(info);
		double shortestPath = Math.sqrt(
				Math.pow(result.getTest().size(), 2)+ 
				Math.pow(result.getOriginal().size(), 2));
		Double totalResult = (resultDtw-shortestPath)/shortestPath; 
		result.setTotalResult(totalResult);
		log.debug("DTW comparition result: " + result.getTotalResult());
		return seq;
	}
}

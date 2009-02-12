package org.spantus.exp.segment.services.impl;

import org.spantus.core.FrameValues;
import org.spantus.exp.segment.beans.ComparisionResult;
import org.spantus.math.DTW;
import org.spantus.math.dtw.DtwInfo;
import org.spantus.math.dtw.DtwInfo.DtwType;

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
		result.setTotalResult(totalResult.floatValue());
		log.debug("DTW comparition result: " + result.getTotalResult());
		return seq;
	}
}

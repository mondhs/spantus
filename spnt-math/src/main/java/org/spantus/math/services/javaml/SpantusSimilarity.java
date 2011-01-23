package org.spantus.math.services.javaml;

import java.util.logging.Logger;

import net.sf.javaml.core.Instance;
import net.sf.javaml.distance.AbstractDistance;
import net.sf.javaml.distance.fastdtw.dtw.DTW;
import net.sf.javaml.distance.fastdtw.dtw.TimeWarpInfo;
import net.sf.javaml.distance.fastdtw.timeseries.TimeSeries;

public class SpantusSimilarity extends AbstractDistance{
	Logger log = Logger.getLogger(SpantusSimilarity.class.getCanonicalName());
	
	public double measure(Instance x, Instance y) {
		TimeSeries tsTarget = ((VectorInstnace)x).getTimeSeries();
		TimeSeries tsSample = ((VectorInstnace)y).getTimeSeries();
//		double twi = DTW.getWarpDistBetween(tsTarget, tsSample);
		double twi = DTW.getWarpDistBetween(tsSample,tsTarget, org.spantus.math.dtw.DtwServiceJavaMLImpl.createSearchWindow(tsSample,tsTarget,3,
				org.spantus.math.dtw.DtwServiceJavaMLImpl.JavaMLSearchWindow.ExpandedResWindow));
		log.severe("[measure] " + x.classValue() +"<>" + y.classValue() + ": " +
				twi);
		return twi;
	}

	@Override
	public boolean compare(double x, double y) {
		// TODO Auto-generated method stub
		return super.compare(x, y);
	}
}

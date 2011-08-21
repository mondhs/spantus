package org.spantus.core.threshold;

import org.spantus.core.FrameValues;
import org.spantus.math.services.ConvexHullService;
import org.spantus.math.services.MathServicesFactory;

public class ConvexHullThreshold extends DynamicThreshold {
	
	private Double signalThreshold = null;
	/**
	 * recalculate threshold for all the signal
	 */
	@Override
	public void flush() {
		super.flush();
		
		ConvexHullService convexHullService = MathServicesFactory.createConvexHullService();
		//find threshold for all the signal 
		getThresholdValues().clear();
		getThresholdValues().addAll(convexHullService.calculateConvexHullTreshold(getOutputValues()));
		signalThreshold = null;
	}
	/**
	 * Just return calculated value during flush
	 */
	@Override
	protected Double recacluclateCurrentThreashold(FrameValues result){
		return signalThreshold;
	}
}

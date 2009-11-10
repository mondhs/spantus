package org.spantus.core.threshold;

import java.util.Map;
import java.util.Vector;

import org.spantus.core.FrameValues;
import org.spantus.logger.Logger;

public class ExtremeThreshold extends DynamicThreshold {

	Logger log = Logger.getLogger(ExtremeThreshold.class);

	Vector<Float> lastMaxs;

	ExtremeThresholdServiceImpl extremeThresholdService = new ExtremeThresholdServiceImpl();

	@Override
	public void flush() {
		super.flush();
		getThresholdValues().clear();
		getState().clear();
		Map<Integer, ExtremeEntry> extremes = null;
		extremes = extremeThresholdService.calculateExtremes(getOutputValues());
		FrameValues stateValues = extremeThresholdService
				.calculateExtremesStates(extremes, getOutputValues());
		getState().addAll(stateValues);
	}

	@Override
	protected boolean isTrained() {
		return true;
	}

}

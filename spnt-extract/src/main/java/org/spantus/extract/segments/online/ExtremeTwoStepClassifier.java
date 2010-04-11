package org.spantus.extract.segments.online;

import org.spantus.core.threshold.ExtremeOfflineClassifier;
import org.spantus.logger.Logger;

public class ExtremeTwoStepClassifier extends ExtremeOnlineClassifier {

	private Logger log = Logger.getLogger(ExtremeTwoStepClassifier.class);

	public ExtremeTwoStepClassifier() {
		super();
	}


	/**
	 * 
	 */
	@Override
	public void flush() {
		super.flush();
		getThresholdValues().clear();
		getOnlineCtx().setIndex(0);
		getMarkSet().getMarkers().clear(); 
		getOnlineCtx().getExtremeSegments().clear();

		for (Float value : getOutputValues()) {
			processValue(value);
		}
		getThresholdValues().addAll(ExtremeOfflineClassifier.refreshThreasholdInfo(getMarkSet(), getOutputValues()));

	}

}

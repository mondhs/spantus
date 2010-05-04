package org.spantus.extract.segments.online;

import org.spantus.extract.segments.offline.ExtremeOfflineClassifier;
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
		
		log.debug("[flush][[[[[[[[[[[[[[[[[[[[[[[[[ recalculating ]]]]]]]]]]]]]]]]]]]]]]]]]]]]");
		
		getThresholdValues().clear();
		getOnlineCtx().setIndex(0);
		getMarkSet().getMarkers().clear(); 
		getOnlineCtx().getExtremeSegments().clear();
		getOnlineCtx().setCurrentSegment(null);

		getOnlineCtx().setPreviousValue(null);
		getOnlineCtx().setSkipLearn(Boolean.TRUE);
		for (Float value : getOutputValues()) {
			processValue(value);
		}
		endupPendingSegments(getOnlineCtx());
		getThresholdValues().addAll(ExtremeOfflineClassifier.refreshThreasholdInfo(getMarkSet(), getOutputValues()));

	}

}

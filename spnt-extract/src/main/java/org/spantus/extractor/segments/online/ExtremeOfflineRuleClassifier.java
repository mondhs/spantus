package org.spantus.extractor.segments.online;

import java.util.Iterator;

import org.spantus.core.marker.Marker;
import org.spantus.extractor.segments.offline.ExtremeOfflineClassifier;
import org.spantus.logger.Logger;

public class ExtremeOfflineRuleClassifier extends ExtremeOnlineRuleClassifier {

	private Logger log = Logger.getLogger(ExtremeOfflineRuleClassifier.class);

	public ExtremeOfflineRuleClassifier() {
		super();
	}


	/**
	 * 
	 */
	@Override
	public void flush() {
		super.flush();
		
		log.debug("[flush] recalculating ");
		
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
		for (Iterator<Marker> iterator = getMarkSet().iterator(); iterator.hasNext();) {
			Marker marker = (Marker) iterator.next();
			if(marker.getLength()<30){
				log.error("[refreshThreasholdInfo]Removing not valid marker" + 
						marker.getLabel() + ": " + marker.getLength());
				iterator.remove();
				continue;
			}
			
		}
		getThresholdValues().addAll(ExtremeOfflineClassifier.refreshThreasholdInfo(getMarkSet(), getOutputValues()));

	}

}

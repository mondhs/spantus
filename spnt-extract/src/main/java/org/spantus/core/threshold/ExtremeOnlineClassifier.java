package org.spantus.core.threshold;

import java.util.LinkedList;

import org.spantus.core.FrameValues;
import org.spantus.core.threshold.ExtremeEntry.SignalStates;
import org.spantus.logger.Logger;

public class ExtremeOnlineClassifier extends AbstractClassifier{

	private Logger log = Logger.getLogger(ExtremeOnlineClassifier.class); 
	private Float previous;
	private Long index=0L;
	private LinkedList<ExtremeEntry> extremeEntries;
	private SignalStates lastState = null;

	public void afterCalculated(Long sample, FrameValues result) {
		for (Float value : result) {
			processValue(value);
		}
	}
	/**
	 * 
	 * @param sample
	 * @param value
	 */
	private void processValue(Float value) {
		index++;
		if (previous == null) {
			previous = value;
			log.debug("[processValue]first: {0} on {1}",previous,index);
			return;
		}
		if(lastState == null){
			if (value > previous) {
				log.debug("[processValue]found 1st min on {0} value {1}",
						index - 1, previous);
				lastState = SignalStates.max;
			}
		}else if(SignalStates.min.equals(lastState)){
			if(value > previous){
				log.debug("[processValue]found max on {0} value {1}",
						index - 1, previous);
				lastState = SignalStates.max;
			}
		}else if(SignalStates.max.equals(lastState)){
			if(value > previous){
				log.debug("[processValue]found min on {0} value {1}",
						index - 1, previous);
				lastState = SignalStates.min;
			}
		}
		previous = value;
	}
	

}

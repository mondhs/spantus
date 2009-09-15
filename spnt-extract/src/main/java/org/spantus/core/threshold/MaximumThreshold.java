package org.spantus.core.threshold;

import java.text.MessageFormat;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.Vector;

import org.spantus.core.threshold.ExtreamEntry.SignalStates;
import org.spantus.logger.Logger;
import org.spantus.math.VectorUtils;

public class MaximumThreshold extends DynamicThreshold {

	Logger log = Logger.getLogger(MaximumThreshold.class);

	Vector<Float> lastMaxs;

	@Override
	public void flush() {
		super.flush();
		getThresholdValues().clear();
		getState().clear();
		Float previous = null;
		Map<Integer, ExtreamEntry> extriems = new TreeMap<Integer, ExtreamEntry>();

		Float min = VectorUtils.min(getOutputValues());
		
		SignalStates maxState = SignalStates.stable;
		SignalStates minState = SignalStates.stable;
		int index = 0;
		for (Float value : getOutputValues()) {
			if (previous == null)
				previous = value;
			int entryIndex = index - 1;
			
			if (value > previous) {
				maxState = SignalStates.maxExtream;
			} else {
				if (SignalStates.maxExtream.equals(maxState)) {
					ExtreamEntry currentExtreamEntry = new ExtreamEntry(
							entryIndex, previous, maxState);
					extriems.put(entryIndex, currentExtreamEntry);
				}
				maxState = SignalStates.decreasing;
			}

			if (value < previous) {
				minState = SignalStates.minExtream;
			} else {
				if (SignalStates.minExtream.equals(minState)) {
					ExtreamEntry currentExtreamEntry = new ExtreamEntry(
							entryIndex, previous, minState);
					extriems.put(entryIndex, currentExtreamEntry);
				}
				minState = SignalStates.increasing;
			}

			previous = value;
			index++;
		}

		
		
		
		ExtreamSequences allExtriemesSequence = new ExtreamSequences(extriems
				.values(), getOutputValues());
		Set<Integer> maximas = new HashSet<Integer>();
		Set<Integer> minimas = new HashSet<Integer>();

		int minDistance = getOutputValues().toIndex(.15f);
		log.info("minDistance: " + minDistance);
		
		for (ExtreamsListIterator iter = allExtriemesSequence
				.extreamsListIterator(); iter.hasNext();) {
			ExtreamEntry entry = iter.next();
			if(iter.isCurrentMaxExtream()){
				ExtreamEntry nextMax = iter.getNext(SignalStates.maxExtream);
				if(nextMax != null && nextMax.getIndex()-entry.getIndex()<minDistance){
					if(entry.getValue()<=nextMax.getValue()){
						iter.remove();
					}else{
						iter.remove(nextMax);
					}
					if(iter.hasPrevious()){
						iter.previous();
					}
					continue;
				}
			}
		}
		for (ExtreamsListIterator iter = allExtriemesSequence
				.extreamsListIterator(); iter.hasNext();) {
			ExtreamEntry entry = iter.next();
			if(!iter.isCurrentMaxExtream() && iter.isNextMinExtream()){
				if(entry.getValue()>iter.getNextEntry().getValue()){
					iter.remove();
				}else{
					iter.removeNext();
				}
//				log.info("both min" + entry.getIndex() + "; " + iter.getNextEntry().getIndex());
				if(iter.hasPrevious() && iter.hasNext()){
					iter.previous();
				}
				continue;
			}
			if(iter.isCurrentMaxExtream() && !iter.isNextMinExtream()){
				log.info("both max" + entry.getIndex() + "; " + iter.getNextEntry().getIndex());
			}

		}
		
		Double avgArea = null;
		Double minArea = Double.MAX_VALUE;
		Double maxArea = -Double.MAX_VALUE;
		for (ExtreamsListIterator iter = allExtriemesSequence
				.extreamsListIterator(); iter.hasNext();) {
			ExtreamEntry entry = iter.next();

			if (iter.isPreviousMinExtream()
					&& iter.isCurrentMaxExtream()
					&& iter.isNextMinExtream()) {
				Double area = iter.getArea();
				avgArea = avgArea == null?area:avgArea;
				avgArea = (avgArea + area)/2;
				minArea = Math.min(minArea, area);
				maxArea = Math.max(maxArea, area);
			}
		}
		log.info(MessageFormat.format("Area statistic: min:{0}; avg:{1}; max:{2}", minArea, avgArea, maxArea)  );

		avgArea *=.5;

		log.info("using area for discrimination: " + avgArea );


		for (ExtreamsListIterator iter = allExtriemesSequence
				.extreamsListIterator(); iter.hasNext();) {
			ExtreamEntry entry = iter.next();

			if (iter.isPreviousMinExtream()
					&& iter.isCurrentMaxExtream()
					&& iter.isNextMinExtream()) {
//				maximas.add(entry.getIndex());
				if (iter.getArea() > avgArea
				) {
//					maximas.add(entry.getIndex());
					// minimas.add(iterator.getPreviousEntry().getIndex());
					// minimas.add(iterator.getNextEntry().getIndex());
					for (int i = iter.getPreviousEntry().getIndex() + 1; i < iter
							.getNextEntry().getIndex() - 1; i++) {
						maximas.add(i);
					}

					iter.logCurrent();
				}
				


			} else if (entry.getSignalStates().equals(SignalStates.minExtream)) {
			}
		}

		index = 0;
		for (Float value : getOutputValues()) {
			if (maximas.contains(index)) {
				getState().add(1F);
				getThresholdValues().add(value);
			} else if (minimas.contains(index)) {
				getState().add(.0F);
				getThresholdValues().add(value);
			} else {
				getState().add(0F);
				getThresholdValues().add(min);

			}
			index++;
		}
	}

	// @Override
	// protected void processDiscriminator(Long sample, Float value) {
	// if(previous == null){
	// previous = value;
	// }
	// getThresholdValues().add(0F);
	//		
	// if(value>previous){
	// getState().add(1F);
	// }else {
	// getState().add(0F);
	// }
	// previous = value;
	// }

	@Override
	protected boolean isTrained() {
		return true;
	}

}

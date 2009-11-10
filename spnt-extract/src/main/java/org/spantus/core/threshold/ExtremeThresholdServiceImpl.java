package org.spantus.core.threshold;

import java.text.MessageFormat;
import java.util.HashSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.spantus.core.FrameValues;
import org.spantus.core.threshold.ExtremeEntry.SignalStates;
import org.spantus.logger.Logger;

public class ExtremeThresholdServiceImpl {
	
	Logger log = Logger.getLogger(ExtremeThresholdServiceImpl.class);
	/**
	 * 
	 * @param values
	 * @return
	 */
	public Map<Integer, ExtremeEntry> calculateExtremes(FrameValues values) {
		Map<Integer, ExtremeEntry> extremes = null;
		extremes = extractExtremes(values);
		extremes = processExtremes(extremes, values);
		return extremes;

	}
	/**
	 * extract min/max from signal
	 * 
	 * @param values
	 * @return
	 */
	public Map<Integer, ExtremeEntry> extractExtremes(List<Float> values) {
		Map<Integer, ExtremeEntry> extremes = new TreeMap<Integer, ExtremeEntry>();
		
		if(values.size() == 0){
			return extremes;
		}

		int index = 0;
		Float previous = null;
		SignalStates maxState = SignalStates.stable;
		SignalStates minState = SignalStates.stable;
		ListIterator<Float> listIter = values.listIterator();
		//search for the first minimum
		while (listIter.hasNext()) {
			Float value = (Float) listIter.next();
			if (previous == null){
				previous = value;
				index++;
				continue;
			}
			if(value>previous){
				log.debug("found 1st min on {0} value {1}", index-1, previous);
				break;
			}
			previous = value;
			index++;
			
		}

		//found min. revert 
		listIter.previous();
		index--;
		ExtremeEntry firstMinExtreamEntry = new ExtremeEntry(
				index, previous, SignalStates.minExtream);
		extremes.put(index, firstMinExtreamEntry);
		log.debug("adding 1st min  {0} ", firstMinExtreamEntry.toString());

		//process all the signal for min/max extremes
		while (listIter.hasNext()) {
			Float value = (Float) listIter.next();
			int entryIndex = index;

			if (value > previous) {
				maxState = SignalStates.maxExtream;
			} else {
				if (SignalStates.maxExtream.equals(maxState)) {
					ExtremeEntry currentExtreamEntry = new ExtremeEntry(
							entryIndex, previous, maxState);
					extremes.put(entryIndex, currentExtreamEntry);
					log.debug("adding max  {0} ", currentExtreamEntry);
				}
				maxState = SignalStates.decreasing;
			}

			if (value < previous) {
				minState = SignalStates.minExtream;
			} else {
				if (SignalStates.minExtream.equals(minState)) {
					ExtremeEntry currentExtreamEntry = new ExtremeEntry(
							entryIndex, previous, minState);
					extremes.put(entryIndex, currentExtreamEntry);
					log.debug("adding min  {0} ", currentExtreamEntry);
				}
				minState = SignalStates.increasing;
			}

			previous = value;
			index++;
		}
		
		

		return extremes;
	}
	/**
	 * 
	 * @param originalExtremes
	 * @param values
	 * @return
	 */
	public Map<Integer, ExtremeEntry> processExtremes(
			Map<Integer, ExtremeEntry> originalExtremes, FrameValues values) {
		if(originalExtremes.size() == 0){
			return originalExtremes;
		}
		Map<Integer, ExtremeEntry> extremes = new TreeMap<Integer, ExtremeEntry>(
				originalExtremes);
		ExtremeSequences allExtriemesSequence = new ExtremeSequences(extremes
				.values(), values);
		int minDistance = values.toIndex(.15f);
		log.info("minDistance: " + minDistance);

		for (ExtremeListIterator iter = allExtriemesSequence
				.extreamsListIterator(); iter.hasNext();) {
			ExtremeEntry entry = iter.next();
			if (iter.isCurrentMaxExtream()) {
				ExtremeEntry nextMax = iter.getNext(SignalStates.maxExtream);
				if (nextMax != null
						&& nextMax.getIndex() - entry.getIndex() < minDistance) {
					if (entry.getValue() <= nextMax.getValue()) {
						iter.remove();
					} else {
						iter.remove(nextMax);
					}
					if (iter.hasPrevious()) {
						iter.previous();
					}
					continue;
				}
			}
		}
		for (ExtremeListIterator iter = allExtriemesSequence
				.extreamsListIterator(); iter.hasNext();) {
			ExtremeEntry entry = iter.next();
			if (!iter.isCurrentMaxExtream() && iter.isNextMinExtream()) {
				if (entry.getValue() > iter.getNextEntry().getValue()) {
					iter.remove();
				} else {
					iter.removeNext();
				}
				// log.info("both min" + entry.getIndex() + "; " +
				// iter.getNextEntry().getIndex());
				if (iter.hasPrevious() && iter.hasNext()) {
					iter.previous();
				}
				continue;
			}
			if (iter.isCurrentMaxExtream() && !iter.isNextMinExtream()) {
				log.info("both max" + entry.getIndex() + "; "
						+ iter.getNextEntry().getIndex());
			}
		}
		return extremes;
	}
	/**
	 * 
	 * @param extremeSequences
	 */
	public void logExtremes(ExtremeSequences extremeSequences) {
		if (!log.isDebugMode()) {
			return;
		}
		Double avgArea = null;
		Double minArea = Double.MAX_VALUE;
		Double maxArea = -Double.MAX_VALUE;
		for (ExtremeListIterator iter = extremeSequences.extreamsListIterator(); iter
				.hasNext();) {
			if (iter.isPreviousMinExtream() && iter.isCurrentMaxExtream()
					&& iter.isNextMinExtream()) {
				Double area = iter.getArea();
				avgArea = avgArea == null ? area : avgArea;
				avgArea = (avgArea + area) / 2;
				minArea = Math.min(minArea, area);
				maxArea = Math.max(maxArea, area);
			}
		}
		log.info(MessageFormat.format(
				"Area statistic: min:{0}; avg:{1}; max:{2}", minArea, avgArea,
				maxArea));

	}
	/**
	 * 
	 * @param extremes
	 * @param values
	 * @return
	 */
	public FrameValues calculateExtremesStates(
			Map<Integer, ExtremeEntry> extremes, FrameValues values) {
		ExtremeSequences allExtriemesSequence = new ExtremeSequences(extremes
				.values(), values);
		// avgArea *=.5;
		//
		// log.info("using area for discrimination: " + avgArea );

		Set<Integer> maximas = new HashSet<Integer>();
		Set<Integer> minimas = new HashSet<Integer>();

		Double avgArea = 0D;
		FrameValues extremesStates = new FrameValues();

		for (ExtremeListIterator iter = allExtriemesSequence
				.extreamsListIterator(); iter.hasNext();) {
			ExtremeEntry entry = iter.next();

			if (iter.isPreviousMinExtream() && iter.isCurrentMaxExtream()
					&& iter.isNextMinExtream()) {
				// maximas.add(entry.getIndex());
				if (iter.getArea() > avgArea) {
					// maximas.add(entry.getIndex());
					// minimas.add(iterator.getPreviousEntry().getIndex());
					// minimas.add(iterator.getNextEntry().getIndex());
					for (int i = iter.getPreviousEntry().getIndex() + 1; i < iter
							.getNextEntry().getIndex() - 1; i++) {
						maximas.add(i);
					}

					iter.logCurrent();
				}

			} else if (entry.getSignalState().equals(SignalStates.minExtream)) {
			}
		}


		for (int index = 0; index < values.size(); index++) {
			if (maximas.contains(index)) {
				extremesStates.add(1F);
				// getThresholdValues().add(value);
			} else if (minimas.contains(index)) {
				extremesStates.add(.0F);
				// getThresholdValues().add(value);
			} else {
				extremesStates.add(0F);
				// getThresholdValues().add(min);
			}
		}
		return extremesStates;
	}
}

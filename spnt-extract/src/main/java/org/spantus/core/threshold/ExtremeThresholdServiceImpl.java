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
//		extremes = processExtremes(extremes, values);
		extremes = filtterExremeOffline(extremes, values);
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
				log.debug("[extractExtremes]found 1st min on {0} value {1}", index-1, previous);
				break;
			}
			previous = value;
			index++;
			
		}

		//found min. revert 
		listIter.previous();
		index--;
		ExtremeEntry firstMinExtreamEntry = new ExtremeEntry(
				index, previous, SignalStates.min);
		extremes.put(index, firstMinExtreamEntry);
		log.debug("[extractExtremes]adding 1st min  {0} ", firstMinExtreamEntry.toString());

		//process all the signal for min/max extremes
		while (listIter.hasNext()) {
			Float value = (Float) listIter.next();
			int entryIndex = index;
			//track if values is increasing
			if (value > previous) {
				maxState = SignalStates.max;
			} else {
				//Changed point if values are equals or decreasing.
				if (SignalStates.max.equals(maxState)) {
					ExtremeEntry currentExtreamEntry = new ExtremeEntry(
							entryIndex, previous, maxState);
					extremes.put(entryIndex, currentExtreamEntry);
					log.debug("[extractExtremes]adding max  {0} ", currentExtreamEntry);
				}
				maxState = SignalStates.decreasing;
			}

			if (value < previous) {
				minState = SignalStates.min;
			} else {
				if (SignalStates.min.equals(minState)) {
					ExtremeEntry currentExtreamEntry = new ExtremeEntry(
							entryIndex, previous, minState);
					extremes.put(entryIndex, currentExtreamEntry);
					log.debug("[extractExtremes]adding min  {0} ", currentExtreamEntry);
				}
				minState = SignalStates.increasing;
			}

			previous = value;
			index++;
		}
		
		

		return extremes;
	}
	/**
	 * filter extremes
	 * 
	 * @param extremes
	 * @param values
	 * @return
	 */
	public Map<Integer, ExtremeEntry> filtterExremeOffline(
			Map<Integer, ExtremeEntry> extremes, FrameValues values) {
		if(extremes.size() == 0){
			return extremes;
		}
		
		ExtremeSequences allExtriemesSequence = new ExtremeSequences(extremes
				.values(), values);
		
		for (ExtremeListIterator iter = allExtriemesSequence
				.extreamsListIterator(); iter.hasNext();) {
			ExtremeEntry entry = iter.next();
			if (iter.isCurrentMaxExtream()) {
				//filter max
				ExtremeEntry nextMax = iter.getNext(SignalStates.max);
				if (nextMax != null){
//						&& nextMax.getIndex() - entry.getIndex() < minDistance) {
					if (entry.getValue() <= nextMax.getValue()) {
						iter.remove();
						ExtremeEntry nextMin = iter.getNext(SignalStates.min);
						iter.remove(nextMin);
						log.debug("[filtterExremeOffline]removed current");
					} else {
//						iter.remove(nextMax);
//						log.debug("removed next");
					}
//					if (iter.hasPrevious()) {
//						iter.previous();
//					}
					continue;
				}
			}
			//filter min
//			if(iter.isCurrentMinExtream() && iter.isNextMinExtream()){
//				ExtremeEntry nextMin = iter.getNext(SignalStates.min);
//				if(nextMin == null) continue;
//				if(entry.getValue()>nextMin.getValue()){
////					iter.remove();
//					log.debug("[filtterExremeOffline]removed current");
//				}else{
////					iter.remove(nextMin);
//					log.debug("[filtterExremeOffline]removed next");
//				}
////				if (iter.hasPrevious()) {
////					iter.previous();
////				}
//				continue;
//			}
			
			
		}
		Map<Integer, ExtremeEntry> rtnExtremes = allExtriemesSequence.toMap();
		log.debug("[filtterExremeOffline]extremes  {0} ", extremes);
		log.debug("[filtterExremeOffline]rtnExtremes  {0} ", rtnExtremes);
		
		return rtnExtremes;
	}
	/**
	 * 
	 * @param originalExtremes
	 * @param values
	 * @return
	 */
	public Map<Integer, ExtremeEntry> processExtremes(
			Map<Integer, ExtremeEntry> extremes, FrameValues values) {
		if(extremes.size() == 0){
			return extremes;
		}
		ExtremeSequences allExtriemesSequence = new ExtremeSequences(extremes
				.values(), values);
//		int minDistance = values.toIndex(.15f);
//		log.info("minDistance: " + minDistance);

		for (ExtremeListIterator iter = allExtriemesSequence
				.extreamsListIterator(); iter.hasNext();) {
//			ExtremeEntry entry = iter.next();
			if (iter.isCurrentMaxExtream()) {
				ExtremeEntry nextMax = iter.getNext(SignalStates.max);
//				if (nextMax != null
//						&& nextMax.getIndex() - entry.getIndex() < minDistance) {
//					if (entry.getValue() <= nextMax.getValue()) {
//						iter.remove();
//					} else {
//						iter.remove(nextMax);
//					}
//					if (iter.hasPrevious()) {
//						iter.previous();
//					}
//					continue;
//				}
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
				log.info("[processExtremes]both max" + entry.getIndex() + "; "
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
				"[logExtremes]Area statistic: min:{0}; avg:{1}; max:{2}", minArea, avgArea,
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
//		 avgArea *=.5;
		//
		// log.info("using area for discrimination: " + avgArea );

		Set<Integer> maximas = new HashSet<Integer>();
		Set<Integer> minimas = new HashSet<Integer>();

		Double avgArea = 80D;//22000000D;
		FrameValues extremesStates = new FrameValues();
		
		int length = 0;

		for (ExtremeListIterator iter = allExtriemesSequence
				.extreamsListIterator(); iter.hasNext();) {
			ExtremeEntry entry = iter.next();

			if (iter.isPreviousMinExtream() && iter.isCurrentMaxExtream()
					&& iter.isNextMinExtream()) {
				// maximas.add(entry.getIndex());
				if (iter.getArea() > avgArea && iter.getPeakLength()>length) {
					// maximas.add(entry.getIndex());
					// minimas.add(iterator.getPreviousEntry().getIndex());
					// minimas.add(iterator.getNextEntry().getIndex());
					log.debug("[calculateExtremesStates]area {0}", iter.getArea());
					for (int i = iter.getPreviousEntry().getIndex(); i < iter
							.getNextEntry().getIndex() ; i++) {
						maximas.add(i);
					}

					iter.logCurrent();
				}

			} else if (entry.getSignalState().equals(SignalStates.min)) {
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

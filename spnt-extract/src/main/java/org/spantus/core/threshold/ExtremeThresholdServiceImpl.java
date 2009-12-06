package org.spantus.core.threshold;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.TreeMap;
import java.util.Map.Entry;

import org.spantus.core.FrameValues;
import org.spantus.core.marker.Marker;
import org.spantus.core.marker.MarkerSet;
import org.spantus.core.marker.MarkerSetHolder;
import org.spantus.core.marker.service.IMarkerService;
import org.spantus.core.marker.service.MarkerServiceFactory;
import org.spantus.core.threshold.ExtremeEntry.SignalStates;
import org.spantus.logger.Logger;
import org.spantus.math.cluster.ClusterCollection;
import org.spantus.math.services.MathServicesFactory;

public class ExtremeThresholdServiceImpl {

	Logger log = Logger.getLogger(ExtremeThresholdServiceImpl.class);

	IMarkerService markerService;

	public ExtremeThresholdServiceImpl() {
		markerService = MarkerServiceFactory.createMarkerService();
	}

	/**
	 * 
	 * @param values
	 * @return
	 */
	public Map<Integer, ExtremeEntry> calculateExtremes(FrameValues values) {
		Map<Integer, ExtremeEntry> extremes = null;
		extremes = extractExtremes(values);
		extremes = filtterExremeOffline(extremes, values);
		return extremes;

	}

	/**
	 * extract min/max from signal
	 * 
	 * @param values
	 * @return
	 */
	public Map<Integer, ExtremeEntry> extractExtremes(FrameValues values) {
		Map<Integer, ExtremeEntry> extremes1 = new TreeMap<Integer, ExtremeEntry>();
		ExtremeSequences sequence = new ExtremeSequences(extremes1.values(),
				values);
		ExtremeListIterator iterator = sequence.extreamsListIterator();

		if (values.size() == 0) {
			return sequence.toMap();
		}

		int index = 0;
		Float previous = null;
		SignalStates maxState = SignalStates.stable;
		SignalStates minState = SignalStates.stable;
		ListIterator<Float> listIter = values.listIterator();
		// search for the first minimum
		while (listIter.hasNext()) {
			Float value = (Float) listIter.next();
			if (previous == null) {
				previous = value;
				index++;
				continue;
			}
			if (value > previous) {
				log.debug("[extractExtremes]found 1st min on {0} value {1}",
						index - 1, previous);
				break;
			}
			previous = value;
			index++;

		}

		// found min. revert
		listIter.previous();
		index--;
		ExtremeEntry firstMinExtreamEntry = new ExtremeEntry(index, previous,
				SignalStates.min);
		iterator.add(firstMinExtreamEntry);
		log.debug("[extractExtremes]adding min  {0} ", firstMinExtreamEntry
				.toString());

		// process all the signal for min/max extremes
		while (listIter.hasNext()) {
			Float value = (Float) listIter.next();
			int entryIndex = index;
			// track if values is increasing
			if (value > previous) {
				maxState = SignalStates.max;
			} else {
				// Changed point if values are equals or decreasing.
				if (SignalStates.max.equals(maxState)) {
					ExtremeEntry currentExtreamEntry = new ExtremeEntry(
							entryIndex, previous, maxState);
					iterator.add(currentExtreamEntry);
					log.debug("[extractExtremes]adding max  {0} ",
							currentExtreamEntry);
				}
				maxState = SignalStates.decreasing;
			}

			if (value < previous) {
				minState = SignalStates.min;
			} else {
				if (SignalStates.min.equals(minState)) {
					ExtremeEntry currentExtreamEntry = new ExtremeEntry(
							entryIndex, previous, minState);
					log.debug("[extractExtremes]adding min  {0} ",
							currentExtreamEntry);
					iterator.add(currentExtreamEntry);
				}
				minState = SignalStates.increasing;
			}

			previous = value;
			index++;
		}
		if (iterator.isCurrentMaxExtream()) {
			ExtremeEntry lastMinExtreamEntry = new ExtremeEntry(index,
					previous, SignalStates.min);
			iterator.add(lastMinExtreamEntry);
			log.debug("[extractExtremes]adding min  {0} ", lastMinExtreamEntry
					.toString());
		}

		return sequence.toMap();
	}

	/**
	 * 
	 * @param sequence
	 */
	public void initialCleanup(ExtremeSequences sequence) {
		if (log.isDebugMode()) {
			log.debug("[extractExtremes]before intial cleanup {0} ", sequence
					.toMap());
		}
		Float prevLengthTime = 0F;
		for (ExtremeListIterator iter = sequence.extreamsListIterator(); iter
				.hasNext();) {

			ExtremeEntry entry = iter.next();
			if (iter.isCurrentMaxExtream()) {
				ExtremeEntry prevMax = iter.getPrevious(SignalStates.max);
				Long length = iter.getPeakLength();
				Float lengthTime = sequence.allValues.toTime(length.intValue());
				Double area = iter.getArea();

				// join inc part
				if (prevMax != null) {
					boolean prevChunkIncrease = prevMax.getPrevious()
							.getValue() < prevMax.getNext().getValue();
					boolean currentChunkIncrease = entry.getPrevious()
							.getValue() < entry.getNext().getValue();
					if (prevChunkIncrease && currentChunkIncrease) {
						if (lengthTime + prevLengthTime < getMaxLength()) {
							log.debug("prev join as increase for {0}", entry);
							iter.remove(prevMax);
							iter.remove(prevMax.getNext());
						}
					}
					boolean prevChunkDec = prevMax.getPrevious().getValue() > prevMax
							.getNext().getValue();
					boolean currentChunkDec = entry.getPrevious().getValue() > entry
							.getNext().getValue();
					if (prevChunkDec && currentChunkDec) {
						if (lengthTime + prevLengthTime < getMaxLength()) {
							log.debug("prev join as decrease for {0}", entry);
							iter.remove(entry);
							iter.remove(entry.getPrevious());
						}
					}
				}
				prevLengthTime = lengthTime;
			}
		}
		if (log.isDebugMode()) {
			log.debug("[extractExtremes]after intial cleanup {0} ", sequence
					.toMap());
		}
	}

	/**
	 * 
	 * @param vectors
	 * @param centers
	 */
	public void writeDebug(List<List<Float>> vectors, ClusterCollection centers) {
		try {
			FileOutputStream fos = new FileOutputStream(new File(
					"./target/result.csv"));
			DataOutputStream oos = new DataOutputStream(fos);
			for (List<Float> list : vectors) {
				oos.writeBytes(MessageFormat.format("{0};{1}\n", ""
						+ list.get(0), "" + list.get(1)));
			}
			for (Entry<Integer, List<Float>> entry : centers.entrySet()) {
				List<Float> list = entry.getValue();
				if (list.size() == 0)
					continue;
				oos.writeBytes(MessageFormat.format("{0};{1}\n", ""
						+ list.get(0), "" + list.get(1)));
				log.debug("cluster {2} center: area:{0},  lenght:{1};", ""
						+ list.get(0), "" + list.get(1), entry.getKey());
			}

			oos.close();
		} catch (FileNotFoundException e) {
			log.error(e);
		} catch (IOException e) {
			log.error(e);
		}

	}

	/**
	 * 
	 * 
	 * 
	 * 
	 */
	protected ClusterCollection calculateCenters(
			ExtremeSequences allExtriemesSequence) {

		List<List<Float>> vectors = new ArrayList<List<Float>>();

		for (ExtremeListIterator iter = allExtriemesSequence
				.extreamsListIterator(); iter.hasNext();) {
			iter.next();
			if (iter.isCurrentMaxExtream()) {
				List<Float> point = createLearnVector(iter.getPeakLength(),
						iter.getArea());
				vectors.add(point);
			}
		}

		if (vectors.size() == 0) {
			throw new IllegalArgumentException("No data found for clustering.");
		}
		ClusterCollection centers = MathServicesFactory.createKnnService()
				.cluster(vectors, getClusterSize());

		writeDebug(vectors, centers);

		return centers;

	}

	protected List<Float> createLearnVector(Long length, Double area) {
		return createVector(length, area);
	}

	protected List<Float> createMatchVector(Long length, Double area) {
		return createVector((length * 8) / 10, area);
	}

	/**
	 * 
	 * @param lenght
	 * @param area
	 * @return
	 */
	protected List<Float> createVector(Long length, Double area) {
		List<Float> vector = new ArrayList<Float>();
		vector.add(area.floatValue());
		vector.add(length.floatValue());
		return vector;
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
		if (extremes.size() == 0) {
			return extremes;
		}

		ExtremeSequences extriemesSequence = new ExtremeSequences(extremes
				.values(), values);
		ClusterCollection clusterCollection = calculateCenters(extriemesSequence);

		initialCleanup(extriemesSequence);

		if (log.isDebugMode()) {
			log.debug("data before filterring");

			for (ExtremeListIterator iter = extriemesSequence
					.extreamsListIterator(); iter.hasNext();) {
				ExtremeEntry entry = iter.next();
				if (iter.isCurrentMaxExtream()) {
					// if( entry.getNext() != null &&
					// entry.getNext().getSignalState().equals(SignalStates.min)){
					log.debug("{2}; area: {0}; lenght {1}; cluster: {3}", iter
							.getArea(), iter.getPeakLength(), entry.toString(),
							clusterCollection
									.matchClusterClass(createMatchVector(iter
											.getPeakLength(), iter.getArea())));
					// }
				} else {
					log.debug(entry.toString());
				}
			}
		}

		Integer prevClusterId = 0;
		Float prevLengthTime = 0F;
		log.debug("start clean up");
		for (ExtremeListIterator iter = extriemesSequence
				.extreamsListIterator(); iter.hasNext();) {
			ExtremeEntry entry = iter.next();
			if (iter.isCurrentMaxExtream()) {
				ExtremeEntry prevMax = iter.getPrevious(SignalStates.max);
				ExtremeEntry prevMinEntry = entry.getPrevious();
				ExtremeEntry nextMinEntry = entry.getNext();
				Long length = iter.getPeakLength();
				Float lengthTime = values.toTime(length.intValue());
				Double area = iter.getArea();

				Integer clusterID = clusterCollection
						.matchClusterClass(createMatchVector(iter
								.getPeakLength(), iter.getArea()));
				if (prevMax == null) {
					continue;
				}
				if (clusterID >= 1 && prevClusterId == 0) {
					if ((lengthTime + prevLengthTime) < getMaxLength()) {
						log.debug("join smaler chunk {0} to bigger one {1}",
								prevMax, entry);
						iter.remove(prevMax);
						iter.remove(prevMax.getNext());
					}
				}
				if (clusterID == 0 && prevClusterId == 0) {
					if ((lengthTime + prevLengthTime) < getMaxLength()) {
						log.debug("iter area: {0}; length: {1}; prev {2}",
								area, length, prevClusterId);
						iter.remove(entry);
						if (prevClusterId > 0) {
							iter.remove(nextMinEntry);
						} else {
							iter.remove(prevMinEntry);
						}
					}
				}
				prevClusterId = clusterID;
				prevLengthTime = lengthTime;
			}
		}

		log.debug("finished clean up");

		log.debug("starting duplicated min elimination");
		// if (log.isDebugMode()) {
		// log.debug("before duplicate min elimination {0}",
		// allExtriemesSequence.toMap());
		// }
		for (ExtremeListIterator iter = extriemesSequence
				.extreamsListIterator(); iter.hasNext();) {
			ExtremeEntry entry = iter.next();
			if (iter.isCurrentMinExtream()) {
				ExtremeEntry prev = entry.getPrevious();
				if (prev == null)
					continue;
				if (SignalStates.min.equals(prev.getSignalState())) {
					log.debug("remove prev as is min as well {0} {1}", prev,
							entry);
					if (prev.getValue() < entry.getValue()) {
						iter.remove(prev);
					} else {
						iter.remove(entry);
					}
				}
			}
		}
		log.debug("finished duplicated min elimination");
		// if(allExtriemesSequence.size()>3*2){
		// calculateCenters(allExtriemesSequence);
		// }
		Map<Integer, ExtremeEntry> rtnExtremes = extriemesSequence.toMap();
		log.debug("[filtterExremeOffline]extremes  {0} ", extremes);
		log.debug("[filtterExremeOffline]rtnExtremes  {0} ", rtnExtremes);

		return rtnExtremes;
	}

	/**
	 * 
	 * @param extriemesSequence
	 * @return
	 */
	public MarkerSet calculateExtremesSegments(
			ExtremeSequences extriemesSequence) {
		MarkerSet markerSet = new MarkerSet();
		int i = 0;

		ClusterCollection clusterCollection = calculateCenters(extriemesSequence);
		Float sampleRate = extriemesSequence.allValues.getSampleRate();

		for (ExtremeListIterator iter = extriemesSequence
				.extreamsListIterator(); iter.hasNext();) {
			ExtremeEntry entry = iter.next();

			if (iter.isPreviousMinExtream() && iter.isCurrentMaxExtream()
					&& iter.isNextMinExtream()) {

				Integer clusterID = clusterCollection
						.matchClusterClass(createMatchVector(iter
								.getPeakLength(), iter.getArea()));
				// delete lower energy segments
				if (clusterID == 0) {
					continue;
				}
				Marker marker = createMarker(entry, sampleRate);
				marker.setLabel(MessageFormat.format("{0}:{1}", i, clusterID));

				markerSet.getMarkers().add(marker);
				i++;
			}
		}
		markerSet.setMarkerSetType(MarkerSetHolder.MarkerSetHolderEnum.phone
				.name());
		return markerSet;
	}

	/**
	 * 
	 * @param entry
	 * @param sampleRate
	 * @return
	 */
	protected Marker createMarker(ExtremeEntry entry, Float sampleRate) {
		Marker marker = new Marker();
		Integer startInSample = entry.getPrevious().getIndex();
		Integer endInSample = entry.getNext().getIndex();
		Long start = markerService.getTime(startInSample, sampleRate);
		Long end = markerService.getTime(endInSample, sampleRate);
		marker.setStart(start);
		marker.setEnd(end);
		marker.getExtractionData().setStartSampleNum(startInSample.longValue());
		marker.getExtractionData().setEndSampleNum(endInSample.longValue());
		return marker;
	}

	/**
	 * 
	 * @param extremes
	 * @param values
	 * @return
	 */
	// public FrameValues calculateExtremesStates(MarkerSet markerSet) {
	//
	// Set<Integer> maximas = new HashSet<Integer>();
	//
	// FrameValues extremesStates = new FrameValues();
	//
	// int lastEnd = 0;
	// // int entryIndex = 0;
	// for (Marker marker : markerSet.getMarkers()) {
	// // entryIndex++;
	// // if (entryIndex % 2 == 0)
	// // continue;
	// int start = marker.getExtractionData().getStartSampleNum()
	// .intValue();
	// int end = start
	// + marker.getExtractionData().getLengthSampleNum()
	// .intValue();
	// for (int i = start; i < end; i++) {
	// maximas.add(i);
	// }
	// lastEnd = end;
	// }
	//
	// for (int index = 0; index < lastEnd; index++) {
	// if (maximas.contains(index)) {
	// extremesStates.add(1F);
	// // getThresholdValues().add(value);
	// } else {
	// extremesStates.add(0F);
	// // getThresholdValues().add(min);
	// }
	// }
	// return extremesStates;
	// }

	// public FrameValues calculateChangePoints(MarkerSet markerSet) {
	// Map<Integer,Float> maximas = new HashMap<Integer,Float>();
	//
	// FrameValues extremesStates = new FrameValues();
	//
	// int lastEnd = 0;
	// for (Marker marker : markerSet.getMarkers()) {
	//
	// int start = marker.getExtractionData().getStartSampleNum()
	// .intValue();
	// int end = start
	// + marker.getExtractionData().getLengthSampleNum()
	// .intValue();
	// maximas.put(start,1000F);
	// maximas.put(end,1000F);
	//			
	// lastEnd = end;
	// }
	//
	// for (int index = 0; index < lastEnd; index++) {
	// if (maximas.containsKey(index)) {
	// extremesStates.add(maximas.get(index));
	// } else {
	// extremesStates.add(0F);
	// // getThresholdValues().add(min);
	// }
	// }
	// return extremesStates;
	// }

	protected int getClusterSize() {
		return 3;
	}

	protected float getMaxLength() {
		return .2F;
	}

}

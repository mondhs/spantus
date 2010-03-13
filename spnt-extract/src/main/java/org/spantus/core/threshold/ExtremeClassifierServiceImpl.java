package org.spantus.core.threshold;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.LinkedList;
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

public class ExtremeClassifierServiceImpl {

	Logger log = Logger.getLogger(ExtremeClassifierServiceImpl.class);

	IMarkerService markerService;

	public ExtremeClassifierServiceImpl() {
		markerService = MarkerServiceFactory.createMarkerService();
	}

//	/**
//	 * 
//	 * @param values
//	 * @return
//	 */
//	public Map<Integer, ExtremeEntry> calculateExtremes(FrameValues values) {
//		Map<Integer, ExtremeEntry> extremes = null;
//		ExtremeCtx extremeCtx = new ExtremeCtx();
//		extremeCtx.setValues(values);
//		extremes = extractExtremes(extremeCtx);
//		extremes = filtterExremeOffline(extremeCtx);
//		return extremes;
//	}

	
	/**
	 * 
	 * @param extremes
	 * @return
	 */
	public ExtremeOfflineCtx calculateSegments(FrameValues values) {
		ExtremeOfflineCtx ctx = new ExtremeOfflineCtx();
		ctx.setValues(values);
		extractExtremes(ctx);
		extractSements(ctx);
		initialCleanup(ctx);
		extractMarkerSet(ctx);
		return ctx;
		
	}
	/**
	 * 
	 * @param extremeCtx
	 * @return
	 */
	public List<ExtremeSegment> extractSements(ExtremeOfflineCtx extremeCtx) {

		List<ExtremeSegment> segments = new LinkedList<ExtremeSegment>();
		
		ExtremeSequences sequence = extremeCtx.getSequence();
//		ExtremeSegment prev = null;
		for (ExtremeListIterator iter = sequence.extreamsListIterator(); iter
				.hasNext();) {
			ExtremeEntry entry = iter.next();
			if (iter.isCurrentMaxExtream()) {
				ExtremeSegment extremeSegment = new ExtremeSegment();
				extremeSegment.setMiddleEntry(entry);
				extremeSegment.setStartEntry(entry.getPrevious());
				extremeSegment.setEndEntry(entry.getNext());
				segments.add(extremeSegment);
//				if(prev != null){
//					prev.setNext(extremeSegment);
//					extremeSegment.setPrevious(prev);
//				}
//				prev = extremeSegment;
			}
		}
		extremeCtx.setSegments(segments);
		if(log.isDebugMode()){
			log.debug("[extractSements]{1} segments: {0}", segments, segments.size());
		}
		return segments;
	}
	
	/**
	 * extract min/max from signal
	 * 
	 * @param values
	 * @return
	 */
	public Map<Integer, ExtremeEntry> extractExtremes(ExtremeOfflineCtx extremeCtx) {
		
		FrameValues values = extremeCtx.getValues();
		
		Map<Integer, ExtremeEntry> extremes1 = new TreeMap<Integer, ExtremeEntry>();
		ExtremeSequences sequence = new ExtremeSequences(extremes1.values(),
				values);
		extremeCtx.setSequence(sequence);
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
	public List<ExtremeSegment> initialCleanup(ExtremeOfflineCtx ctx) {
		if (log.isDebugMode()) {
			for (ExtremeSegment extremeSegment : ctx.getSegments()) {
				log.debug("[initialCleanup]before intial cleanup {0} ", extremeSegment);				
			}
		}
		LinkedList<ExtremeSegment> segments = new LinkedList<ExtremeSegment>();
//		Float prevLengthTime = 0F;
		ExtremeSegment previous = null;
		for (ListIterator<ExtremeSegment> iter = ctx.getSegmentsIterator(); iter
				.hasNext();) {
			ExtremeSegment entry = iter.next();
			log.debug("[initialCleanup]++++++++++++++++++++++++++++++");
			
			if (previous == null) {
				segments.add(entry);
				log.debug("[initialCleanup]reusing first:{0}; exists: {1} ",  entry, segments.size());
			}else {
				
//				Long length = entry.getPeakLength();
//				Float lengthTime = ctx.getValues().toTime(length.intValue());
				// Double area = iter.getArea();
				if(entry.isIncrease() && previous.isIncrease()){
					log.debug("[initialCleanup]remove:{0}; exists: {1} ",  previous, segments.size());
					ExtremeSegment eliminated = segments.removeLast();
					ExtremeSegment joined = join(eliminated, entry);
					log.debug("[initialCleanup] /// inc:true; inc:true ");
					segments.add(joined);
					log.debug("[initialCleanup]joined:{0}; size: {1} ",  joined, segments.size());
				}else if(entry.isDecrease() && previous.isDecrease()){
					log.debug("[initialCleanup]remove:{0}; size: {1} ",  previous, segments.size());
					ExtremeSegment eliminated = segments.removeLast();
					ExtremeSegment joined = join(eliminated, entry);
					log.debug("[initialCleanup] \\\\\\ dec:true; dec:true ");
					segments.add(joined);
					log.debug("[initialCleanup]joined:{0}; size: {1} ",  joined, segments.size());
				}else {
					segments.add(entry);
					log.debug("[initialCleanup]reusing:{0}; size: {1} ",  entry, segments.size());
				}
				log.debug("[initialCleanup]size {0} ", segments.size());				
//
//				// join increased and decreasing parts
//				if (prevMax != null) {
//					// increasing
//					boolean prevChunkIncrease = prevMax.getPrevious()
//							.getValue() < prevMax.getNext().getValue();
//					boolean currentChunkIncrease = entry.getPrevious()
//							.getValue() < entry.getNext().getValue();
//					if (prevChunkIncrease && currentChunkIncrease) {
//						if (lengthTime + prevLengthTime < getMaxLength()) {
//							log.debug("prev join as increase for {0}", entry);
//							iter.remove(prevMax);
//							iter.remove(prevMax.getNext());
//						}
//					}
//					boolean prevChunkDec = prevMax.getPrevious().getValue() > prevMax
//							.getNext().getValue();
//					boolean currentChunkDec = entry.getPrevious().getValue() > entry
//							.getNext().getValue();
//					// decreasing
//					if (prevChunkDec && currentChunkDec) {
//						if (lengthTime + prevLengthTime < getMaxLength()) {
//							log.debug("prev join as decrease for {0}", entry);
//							iter.remove(entry);
//							iter.remove(entry.getPrevious());
//						}
//					}
//				}
//				prevLengthTime = lengthTime;
			}
			previous = entry;
		}
		
		ctx.setSegments(segments);
		if (log.isDebugMode()) {
			for (ExtremeSegment extremeSegment : segments) {
				log.debug("[initialCleanup]after intial cleanup {0} ", extremeSegment);				
			}
		}
		return ctx.getSegments();
	}
	/**
	 * 
	 * @param s1
	 * @param s2
	 * @return
	 */
	public ExtremeSegment join(ExtremeSegment s1, ExtremeSegment s2){
		ExtremeSegment joined = new ExtremeSegment();
		ExtremeSegment prior = s1;
		ExtremeSegment next = s2;
		if(s1.getEndEntry().getIndex().compareTo(s2.getEndEntry().getIndex())>0){
			prior = s2;
			next = s1;
		}
		joined.setStartEntry(prior.getStartEntry());
		joined.setEndEntry(next.getEndEntry());

		if(s1.getMiddleEntry().getValue().compareTo(s2.getMiddleEntry().getValue())>0){
			joined.setMiddleEntry(s1.getMiddleEntry());
		}else{
			joined.setMiddleEntry(s2.getMiddleEntry());	
		}

//		joined.setNext(next.getNext());
//		joined.setPrevious(prior.getPrevious());
//		next.setPrevious(joined);
//		prior.setNext(joined);
		
		return joined;
	}
	
	public boolean remove(List<ExtremeSegment> segments, ExtremeSegment removeTo){
//		removeTo.getPrevious().setNext(removeTo.getNext());
//		removeTo.getNext().setPrevious(removeTo.getPrevious());
		return segments.remove(removeTo);
	}
	
	/**
	 * 
	 * @param vectors
	 * @param centers
	 */
	public void writeDebug(List<List<Float>> vectors, ClusterCollection centers) {
//		try {
//			FileOutputStream fos = new FileOutputStream(new File(
//					"./target/result.csv"));
//			DataOutputStream oos = new DataOutputStream(fos);
//			for (List<Float> list : vectors) {
//				String seperator = "";
//				StringBuilder sb = new StringBuilder();
//				for (Float float1 : list) {
//					sb.append(float1).append(seperator);
//					seperator = ";";
//				}
//				oos.writeBytes(sb.toString());
//			}
			for (Entry<Integer, List<Float>> entry : centers.entrySet()) {
				List<Float> list = entry.getValue();
				String seperator = "";
				StringBuilder sb = new StringBuilder();
				for (Float float1 : list) {
					sb.append(seperator).append(float1);
					seperator = ";";
				}
//				oos.writeBytes(sb.toString());
				log.debug("cluster {0} center: area, length:[{1}]", entry
						.getKey(), sb.toString());
			}
//			oos.close();
//		} catch (FileNotFoundException e) {
//			log.error(e);
//		} catch (IOException e) {
//			log.error(e);
//		}

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
		return createVector(length, area);
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
//	public Map<Integer, ExtremeEntry> filtterExremeOffline(ExtremeCtx extremeCtx) {
//		Map<Integer, ExtremeEntry> extremes = extremeCtx.getExtremes();
//		FrameValues values = extremeCtx.getValues();
//		if (extremes.size() == 0) {
//			return extremes;
//		}
//
//		ExtremeSequences extriemesSequence = new ExtremeSequences(extremes
//				.values(), values);
//		ClusterCollection clusterCollection = calculateCenters(extriemesSequence);
//
//		initialCleanup(extriemesSequence);
//
//		// log debug data
//		if (log.isDebugMode()) {
//			log.debug("data before filterring");
//
//			for (ExtremeListIterator iter = extriemesSequence
//					.extreamsListIterator(); iter.hasNext();) {
//				ExtremeEntry entry = iter.next();
//				if (iter.isCurrentMaxExtream()) {
//					// if( entry.getNext() != null &&
//					// entry.getNext().getSignalState().equals(SignalStates.min)){
//					log.debug("{2}; area: {0}; lenght {1}; cluster: {3}", iter
//							.getArea(), iter.getPeakLength(), entry.toString(),
//							clusterCollection
//									.matchClusterClass(createMatchVector(iter
//											.getPeakLength(), iter.getArea())));
//					// }
//				} else {
//					log.debug(entry.toString());
//				}
//			}
//		}
//		// if(true){
//		// return extriemesSequence.toMap();
//		// }
//		Integer prevClusterId = 0;
//		Float prevLengthTime = 0F;
//		log.debug("start clean up");
//		for (ExtremeListIterator iter = extriemesSequence
//				.extreamsListIterator(); iter.hasNext();) {
//			ExtremeEntry entry = iter.next();
//			if (iter.isCurrentMaxExtream()) {
//				ExtremeEntry prevMax = iter.getPrevious(SignalStates.max);
//				ExtremeEntry prevMinEntry = entry.getPrevious();
//				ExtremeEntry nextMinEntry = entry.getNext();
//				Long length = iter.getPeakLength();
//				Float lengthTime = values.toTime(length.intValue());
//				Double area = iter.getArea();
//
//				Integer clusterID = clusterCollection
//						.matchClusterClass(createMatchVector(iter
//								.getPeakLength(), iter.getArea()));
//				if (prevMax == null) {
//					continue;
//				}
//				// if (clusterID >= 1 && prevClusterId == 0) {
//				// if ((lengthTime + prevLengthTime) < getMaxLength()) {
//				// log.debug("join smaler chunk {0} to bigger one {1}",
//				// prevMax, entry);
//				// iter.remove(prevMax);
//				// iter.remove(prevMax.getNext());
//				// }
//				// }
//				// clusterID == 0 && prevClusterId == 0
////				if (true) {
////					if ((lengthTime + prevLengthTime) < getMaxLength()) {
////						log.debug("iter area: {0}; length: {1}; prev {2}",
////								area, length, prevClusterId);
////						iter.remove(entry);
////						if (prevClusterId > 0) {
////							iter.remove(nextMinEntry);
////						} else {
////							iter.remove(prevMinEntry);
////						}
////					}
////				}
//				prevClusterId = clusterID;
//				prevLengthTime = lengthTime;
//			}
//		}
//
//		log.debug("finished clean up");
//
//		log.debug("starting duplicated min elimination");
//		// if (log.isDebugMode()) {
//		// log.debug("before duplicate min elimination {0}",
//		// allExtriemesSequence.toMap());
//		// }
//		for (ExtremeListIterator iter = extriemesSequence
//				.extreamsListIterator(); iter.hasNext();) {
//			ExtremeEntry entry = iter.next();
//			if (iter.isCurrentMinExtream()) {
//				ExtremeEntry prev = entry.getPrevious();
//				if (prev == null)
//					continue;
//				if (SignalStates.min.equals(prev.getSignalState())) {
//					log.debug("remove prev as is min as well {0} {1}", prev,
//							entry);
//					if (prev.getValue() < entry.getValue()) {
//						iter.remove(prev);
//					} else {
//						iter.remove(entry);
//					}
//				}
//			}
//		}
//		log.debug("finished duplicated min elimination");
//		// if(allExtriemesSequence.size()>3*2){
//		// calculateCenters(allExtriemesSequence);
//		// }
//		Map<Integer, ExtremeEntry> rtnExtremes = extriemesSequence.toMap();
//		log.debug("[filtterExremeOffline]extremes  {0} ", extremes);
//		log.debug("[filtterExremeOffline]rtnExtremes  {0} ", rtnExtremes);
//
//		return rtnExtremes;
//	}

	/**
	 * 
	 * @param extriemesSequence
	 * @return
	 */
	public MarkerSet extractMarkerSet(
			ExtremeOfflineCtx ctx) {
		MarkerSet markerSet = new MarkerSet();
		ctx.setMarkerSet(markerSet);
		int i = 0;
		if(ctx.getSequence().size() < 6){
			return markerSet;
		}
		ClusterCollection clusterCollection = calculateCenters(ctx.getSequence());
		Float sampleRate = ctx.getSampleRate();

		for (ExtremeListIterator iter = ctx.getSequence()
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

package org.spantus.externals.recognition.services;

import java.awt.Point;
import java.text.MessageFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.stat.descriptive.StatisticalSummary;
import org.spantus.core.FrameValues;
import org.spantus.core.FrameVectorValues;
import org.spantus.core.IValues;
import org.spantus.core.beans.IValueHolder;
import org.spantus.core.beans.RecognitionResult;
import org.spantus.core.beans.RecognitionResultDetails;
import org.spantus.core.beans.SignalSegment;
import org.spantus.exception.ProcessingException;
import org.spantus.logger.Logger;
import org.spantus.math.NumberUtils;
import org.spantus.math.dtw.DtwResult;
import org.spantus.math.dtw.DtwService;

public class CorpusServiceHelperImpl {
	
	private static final Logger LOG = Logger.getLogger(CorpusServiceHelperImpl.class);
	
	private DtwService dtwService;
	private Set<String> includeFeatures;
	
//	/**
//	 * Update information with min max for each feature
//	 * 
//	 * @param feature
//	 * @param value
//	 * @param minimum
//	 * @param maximum
//	 */
//	public void updateMinMax(String feature, Double value,
//			Map<String, Double> minimum, Map<String, Double> maximum) {
//		// log.debug("[updateMinMax] feature [{0}]: {1} ", feature, value);
//		if (minimum.get(feature) == null) {
//			minimum.put(feature, Double.MAX_VALUE);
//		}
//		if (maximum.get(feature) == null) {
//			maximum.put(feature, -Double.MAX_VALUE);
//		}
//		if (minimum.get(feature).compareTo(value) > 0) {
//			// log.debug("[updateMinMax] [{2}] minimum {0}>{1}",
//			// minimum.get(feature),value, feature);
//			minimum.put(feature, value);
//		}
//		if (maximum.get(feature).compareTo(value) < 0) {
//			// log.debug("[updateMinMax] [{2}] maximum {0}<{1}",maximum.get(feature)
//			// ,value, feature);
//			maximum.put(feature, value);
//		}
//		// log.debug("[updateMinMax][{0}] [{1}]", minimum, maximum);
//	}
//	
	/**
	 * Update information with min max for each feature
	 * 
	 * @param feature
	 * @param value
	 * @param minimum
	 * @param maximum
	 * @param targetLength 
	 * @param sampleLength 
	 */
	public void updateMinMax(String feature, Double value,
			Map<String, Double> minimum, Map<String, Double> maximum, Long sampleLength, Long targetLength) {
		// log.debug("[updateMinMax] feature [{0}]: {1} ", feature, value);
		if (minimum.get(feature) == null) {
			minimum.put(feature, Double.MAX_VALUE);
		}
		if (maximum.get(feature) == null) {
			maximum.put(feature, -Double.MAX_VALUE);
		}
		if (minimum.get(feature).compareTo(value) > 0 && !value.isInfinite()) {
			// log.debug("[updateMinMax] [{2}] minimum {0}>{1}",
			// minimum.get(feature),value, feature);
			minimum.put(feature, value);
		}
		if (maximum.get(feature).compareTo(value) < 0 && !value.isInfinite()) {
			// log.debug("[updateMinMax] [{2}] maximum {0}<{1}",maximum.get(feature)
			// ,value, feature);
			maximum.put(feature, value);
		}
		// log.debug("[updateMinMax][{0}] [{1}]", minimum, maximum);
	}
	
	
	/**
	 * post process multi match result. normalize and ordering
	 * 
	 * @param results
	 * @return
	 */
//	public <T extends RecognitionResult> List<T> postProcessResult(
//			List<T> results, Map<String, Double> minimum,
//			Map<String, Double> maximum) {
//		// log.debug("[postProcessResult]+++");
//
//		for (RecognitionResult result : results) {
//			Map<String, Double> normalizedScores = new HashMap<String, Double>();
//			Double normalizedSum = 0D;
//
//			for (Entry<String, Double> score : result.getScores().entrySet()) {
//				// Double min = minimum.get(score.getKey());
//				// Double max = maximum.get(score.getKey());
//				// Double delta = max-min;
//				// Double normalizedScore = (score.getValue() - min)/delta;
//				Double normalizedScore = score.getValue();
//
//				if (getIncludeFeatures() == null
//						|| getIncludeFeatures().isEmpty()) {
//					normalizedSum += normalizedScore;
//				} else if (getIncludeFeatures().contains(score.getKey())) {
//					normalizedSum += normalizedScore;
//				}
//
//				normalizedScores.put(score.getKey(), normalizedScore);
//			}
//			result.setDistance(normalizedSum);
//			result.setScores(normalizedScores);
//		}
//		// log.debug("[postProcessResult] results before sort: {0}", results);
//		Collections.sort(results, new Comparator<RecognitionResult>() {
//			public int compare(RecognitionResult o1, RecognitionResult o2) {
//				return NumberUtils.compare(o1.getDistance(), o2.getDistance());
//			}
//		});
//		int maxElementSize = NumberUtils.min(20, results.size());
//
//		LOG.debug("[postProcessResult] results after sort: {0}", results);
//		// log.debug("[postProcessResult]---");
//		return results.subList(0, maxElementSize);
//	}
	
	/**
	 * post process multi match result. normalize and ordering
	 * 
	 * @param results
	 * @return
	 */
	public <T extends RecognitionResult> List<T> postProcessResult(
			List<T> results, Map<String, Double> minimum,
			Map<String, Double> maximum) {
		// log.debug("[postProcessResult]+++");
		
		

		for (RecognitionResult result : results) {
			Map<String, Double> normalizedScores = new HashMap<String, Double>();
			Map<String, Double> distances = new HashMap<String, Double>();
			Double normalizedSum = 0D;
			int countAffectiveScores = 0;
			
			for (Entry<String, Double> score : result.getScores().entrySet()) {
				 Double min = minimum.get(score.getKey());
				 Double max = maximum.get(score.getKey());
				 Double delta = max-min;
				 Double normalizedScore = (score.getValue() - min)/delta;
//				Double normalizedScore = score.getValue();

				if (getIncludeFeatures() == null
						|| getIncludeFeatures().isEmpty()) {
					normalizedSum += normalizedScore;
					countAffectiveScores++;
				} else if (getIncludeFeatures().contains(score.getKey())) {
					normalizedSum += normalizedScore;
					countAffectiveScores++;
				}
				normalizedScores.put(score.getKey(), normalizedScore);
				distances.put(score.getKey(), score.getValue());
			}
			countAffectiveScores = Math.max(1, countAffectiveScores);
			result.setDistance(normalizedSum/countAffectiveScores);
			
			if(result.getDetails() !=null){
				result.getDetails().setDistances(distances);
			}
			result.setScores(normalizedScores);
		}
		// log.debug("[postProcessResult] results before sort: {0}", results);
		Collections.sort(results, new Comparator<RecognitionResult>() {
			public int compare(RecognitionResult o1, RecognitionResult o2) {
				return NumberUtils.compare(o1.getDistance(), o2.getDistance());
			}
		});
		int maxElementSize = NumberUtils.min(20, results.size());

		LOG.debug("[postProcessResult] results after sort: {0}", results);
		// log.debug("[postProcessResult]---");
		return results.subList(0, maxElementSize);
	}
	
	/**
	 * 
	 * @param targetEntry
	 * @param sampleFeatureData
	 * @return
	 */
//	public DtwResult findDtwResult(Entry<String, IValues> targetEntry,
//			IValueHolder<?> sampleFeatureData) {
//		DtwResult dtwResult = null;
//		int targetDimention = targetEntry.getValue().getDimention();
//		// you know your stuff
//		if (targetDimention == 1) {
//			dtwResult = (DtwResult) getDtwService()
//					.calculateInfo((FrameValues) targetEntry.getValue(),
//							(FrameValues) sampleFeatureData.getValues());
//		} else {
//			if (targetDimention != sampleFeatureData.getValues().getDimention()) {
//				String msg = "Sample size not same " + targetEntry.getKey()
//						+ targetDimention + "!="
//						+ sampleFeatureData.getValues().getDimention();
//				LOG.error("[findMultipleMatch] " + msg);
//				throw new ProcessingException(msg);
//			}
//			dtwResult = getDtwService()
//					.calculateInfoVector(
//							(FrameVectorValues) targetEntry.getValue(),
//							(FrameVectorValues) sampleFeatureData.getValues());
//		}
//		return dtwResult;
//	}
	
	/**
	 * 
	 * @param key
	 * @param targetEntry
	 * @param sampleFeatureData
	 * @return
	 */
	public DtwResult findDtwResult(String key, IValues targetEntry, IValues sampleFeatureData) {
		int targetDimention = targetEntry.getDimention();
		DtwResult dtwResult = null;
		
		if (targetDimention  != sampleFeatureData
				.getDimention()) {
			String msg = MessageFormat.format("[findMultipleMatch] Sample size not same {0}: {1} != {2}",
					 key,
					 targetDimention ,
					 sampleFeatureData.getDimention());
			throw new ProcessingException(msg);
		}
		
		if (targetDimention == 1) {
			dtwResult = (DtwResult) getDtwService().calculateInfo(
					(FrameValues) targetEntry,
					(FrameValues) sampleFeatureData);
		} else {
			dtwResult = getDtwService().calculateInfoVector(
					(FrameVectorValues) targetEntry,
					(FrameVectorValues) sampleFeatureData);
		}
		if(dtwResult.getResult().isInfinite()){
			return null;
		}
		return dtwResult;
	}
	
	/**
	 * 
	 * @param target
	 * @param sample
	 * @return
	 */
	protected RecognitionResult compare(String featureName,
			IValues targetValues, SignalSegment sample) {
		IValueHolder<?> fd = sample.findValueHolder(featureName);
		if (fd == null) {
			return null;
		}
		if(targetValues.size()<2){
			return null;
		}
		RecognitionResult result = new RecognitionResult();
		result.setInfo(sample);
		if (targetValues.getDimention() == 1) {
			result.setDistance(getDtwService().calculateDistance(
					(FrameValues) targetValues, (FrameValues) fd.getValues()));
		} else {
			result.setDistance(getDtwService().calculateDistanceVector(
					(FrameVectorValues) targetValues,
					(FrameVectorValues) fd.getValues()));
		}
		if(result.getDistance().isInfinite()){
			return null;
		}
		return result;
	}
	
//	/**
//	 * 
//	 * @param target
//	 * @param sample
//	 * @return
//	 */
//	protected RecognitionResult compare(String featureName,
//			IValues targetValues, SignalSegment sample) {
//		IValueHolder<?> fd = sample.findValueHolder(featureName);
//		if (fd == null) {
//			return null;
//		}
//		RecognitionResult result = new RecognitionResult();
//		result.setInfo(sample);
//		if (targetValues.getDimention() == 1) {
//			if(((FrameValues) targetValues).size()<2){
//				return null;
//			}
//			result.setDistance(getDtwService().calculateDistance(
//					(FrameValues) targetValues, (FrameValues) fd.getValues()));
//		} else {
//			if(((FrameVectorValues) targetValues).size()<2){
//				return null;
//			}
//			result.setDistance(getDtwService().calculateDistanceVector(
//					(FrameVectorValues) targetValues,
//					(FrameVectorValues) fd.getValues()));
//		}
//
//		return result;
//	}
	
	/**
	 * 
	 * @return
	 */
//	public RecognitionResultDetails createRecognitionResultDetail() {
//		RecognitionResultDetails result = new RecognitionResultDetails();
//		result.setPath(new HashMap<String, List<Point>>());
//		result.setPath(new HashMap<String, List<Point>>());
//		result.setDistances(new HashMap<String, Double>());
//		result.setTargetLegths(new HashMap<String, Long>());
//		result.setSampleLegths(new HashMap<String, Long>());
//		return result;
//	}
	
	/**
	 * 
	 * @param recognitionResultDetails 
	 * @return
	 */
	public RecognitionResult createRecognitionResultDetail() {
		RecognitionResult resultInfo = new RecognitionResult();
		resultInfo.setScores(new HashMap<String, Double>());
		RecognitionResultDetails details = new RecognitionResultDetails();
		details.setPath(new HashMap<String, List<Point>>());
		details.setPath(new HashMap<String, List<Point>>());
		details.setTargetLegths(new HashMap<String, Long>());
		details.setSampleLegths(new HashMap<String, Long>());
		details.setCostMatrixMap(new HashMap<String, RealMatrix>());
		details.setStatisticalSummaryMap(new HashMap<String, StatisticalSummary>());
		resultInfo.setDetails(details);
		return resultInfo;
	}
	

	
	
	public DtwService getDtwService() {
		return dtwService;
	}
	
	

	public void setDtwService(DtwService dtwService) {
		this.dtwService = dtwService;
	}


	public Set<String> getIncludeFeatures() {
		return includeFeatures;
	}


	public void setIncludeFeatures(Set<String> includeFeatures) {
		this.includeFeatures = includeFeatures;
	}




}

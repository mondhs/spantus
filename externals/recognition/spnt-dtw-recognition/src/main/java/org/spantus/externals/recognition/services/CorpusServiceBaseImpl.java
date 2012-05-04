package org.spantus.externals.recognition.services;

import java.awt.Point;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.sound.sampled.AudioInputStream;

import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.stat.descriptive.StatisticalSummary;
import org.spantus.core.FrameValues;
import org.spantus.core.FrameVectorValues;
import org.spantus.core.IValues;
import org.spantus.core.beans.FrameValuesHolder;
import org.spantus.core.beans.FrameVectorValuesHolder;
import org.spantus.core.beans.IValueHolder;
import org.spantus.core.beans.RecognitionResult;
import org.spantus.core.beans.RecognitionResultDetails;
import org.spantus.core.beans.SignalSegment;
import org.spantus.core.io.ProcessedFrameLinstener;
import org.spantus.core.service.CorpusRepository;
import org.spantus.core.service.CorpusService;
import org.spantus.exception.ProcessingException;
import org.spantus.externals.recognition.corpus.CorpusRepositoryFileImpl;
import org.spantus.logger.Logger;
import org.spantus.math.NumberUtils;
import org.spantus.math.dtw.DtwResult;
import org.spantus.math.dtw.DtwService;
import org.spantus.math.dtw.DtwServiceJavaMLImpl.JavaMLLocalConstraint;
import org.spantus.math.dtw.DtwServiceJavaMLImpl.JavaMLSearchWindow;
import org.spantus.math.services.MathServicesFactory;

/**
 * 
 * @author Mindaugas Greibus
 * 
 */
public class CorpusServiceBaseImpl implements CorpusService {

	private final static Logger LOG = Logger.getLogger(CorpusServiceBaseImpl.class);

	private DtwService dtwService;

	private CorpusRepository corpus;

	private Set<String> includeFeatures;

	private Float searchRadius;
	
	private Set<ProcessedFrameLinstener> listeners;
	

	private JavaMLSearchWindow javaMLSearchWindow;
	private JavaMLLocalConstraint javaMLLocalConstraint = JavaMLLocalConstraint.Default;

	public RecognitionResult match(Map<String, IValues> target) {
		RecognitionResult match = findBestMatch(target);
		return match;
	}

	public RecognitionResult matchByCorpusEntry(SignalSegment corpusEntry) {
		Map<String, IValues> target = new HashMap<String, IValues>();
		for (Entry<String, FrameVectorValuesHolder> featureData : corpusEntry.getFeatureFrameVectorValuesMap().entrySet()) {
			target.put(featureData.getKey(), featureData.getValue().getValues());
		}
		for (Entry<String, FrameValuesHolder> featureData : corpusEntry.getFeatureFrameValuesMap().entrySet()) {
			target.put(featureData.getKey(), featureData.getValue().getValues());
		}
		RecognitionResult match = findBestMatch(target);
		return match;
	}

	/**
	 * Find mutliple match
	 * 
	 * @param target
	 * @return
	 */
	public List<RecognitionResult> findMultipleMatchFull(
			Map<String, IValues> target) {
		Long begin = System.currentTimeMillis();
		LOG.debug("[findMultipleMatch]+++ ");
		List<RecognitionResult> results = new ArrayList<RecognitionResult>();
		if (target == null || target.isEmpty()) {
			return results;
		}
		
		Long processedCount = 0L;
		Long totalToProcess = getCorpus().count()*target.size();
		started(totalToProcess);
		
		Map<String, Double> minimum = new HashMap<String, Double>();
		Map<String, Double> maximum = new HashMap<String, Double>();

		// iterate all entries in corpus
		for (SignalSegment corpusSample : getCorpus().findAllEntries()) {
			LOG.debug("[findMultipleMatch] sample: {0} ", 
					corpusSample.getName());
			RecognitionResult result = createRecognitionResultDetail();
			Long targetLength = target.values().iterator().next().getTime();
			Long sampleLength = targetLength;
			if(corpusSample.getMarker()!=null){
				sampleLength = corpusSample.getMarker().getLength();
			}
			for (Map.Entry<String, IValues> targetEntry : target.entrySet()) {
				String featureName = targetEntry.getKey();
				
				IValueHolder<?> sampleFeatureData = corpusSample.getFeatureFrameVectorValuesMap().get(
						targetEntry.getKey());
				
				if (sampleFeatureData == null) {
					LOG.error("[findMultipleMatch] sampleFeatureData is not found. skip for " + featureName);
					continue;
				}
				
				DtwResult dtwResult = calculateInfo(featureName, targetEntry.getValue(),sampleFeatureData.getValues());
				result = updateResults(result, dtwResult, featureName, corpusSample, targetEntry);
				if(result!=null){
					updateMinMax(featureName, dtwResult.getResult(), minimum,
							maximum,sampleLength,targetLength);
					
				}else{
					break;
				}
				processed(processedCount++, totalToProcess);	
			}
			if(result != null){
				result.getDetails().setAudioFilePath(getCorpus().findAudioFileById(
					result.getInfo().getId()));
				results.add(result);
			}
		}
		results = postProcessResult(results, minimum, maximum);
		LOG.debug("[findMultipleMatch]--- in {0} ms ",
				System.currentTimeMillis() - begin);
		ended();
		return results;
	}
	/**
	 * 
	 * @param result
	 * @param dtwResult
	 * @param featureName
	 * @param sample
	 * @param targetEntry
	 * @return
	 */
	private RecognitionResult updateResults(
			RecognitionResult resultInfo, DtwResult dtwResult,
			String featureName, SignalSegment sample,
			Entry<String, IValues> targetEntry) {
		if (dtwResult == null || dtwResult.getResult().isInfinite()) {
			return null;
		}
		
		RecognitionResultDetails result = resultInfo.getDetails();
		IValueHolder<?> sampleFeatureData = sample
				.getFeatureFrameVectorValuesMap().get(targetEntry.getKey());
		result.getSampleLegths().put(
				featureName, sampleFeatureData.getValues().getTime() );
		result.getTargetLegths().put(featureName,targetEntry.getValue().getTime() );
		resultInfo.setInfo(sample);
		result.getPath().put(featureName, dtwResult.getPath());
		resultInfo.getScores().put(featureName, dtwResult.getResult());
		result.getCostMatrixMap().put(featureName, dtwResult.getCostMatrix());
		result.getStatisticalSummaryMap().put(featureName,
				dtwResult.getStatisticalSummary());
		return resultInfo;
	}



	/**
	 * 
	 * @param recognitionResultDetails 
	 * @return
	 */
	private RecognitionResult createRecognitionResultDetail() {
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
	private void updateMinMax(String feature, Double value,
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
	private <T extends RecognitionResult> List<T> postProcessResult(
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
	 * Same as {@link #learn(java.lang.String, java.util.Map)} only with audio
	 * stream
	 * 
	 * @param label
	 * @param featureDataMap
	 * @param audioStream
	 * @return
	 */
	public SignalSegment learn(SignalSegment corpusEntry,
			AudioInputStream audioStream) {
		// CorpusEntry corpusEntry = create(label, featureDataMap);
		SignalSegment learnedCorpusEntry = getCorpus().update(corpusEntry,
				audioStream);
		return learnedCorpusEntry;
	}

	/**
	 * 
	 * @param label
	 * @param featureDataMap
	 * @return
	 */
	public SignalSegment create(String label, Map<String, IValues> featureDataMap) {
		return getCorpus().create(label, featureDataMap);
	}

	/**
	 * learn with multiple features
	 * 
	 * @param label
	 * @param featureDataMap
	 * @return
	 */
	public SignalSegment learn(String label, Map<String, IValues> featureDataMap) {
		SignalSegment corpusEntry = create(label, featureDataMap);
		return getCorpus().save(corpusEntry);
	}
	/**
	 * 
	 * @return
	 * @param corpusEntry
	 */
	public Map<String, RecognitionResult> bestMatchesForFeatures(
			SignalSegment corpusEntry) {
		Map<String, IValues> target = new HashMap<String, IValues>();
		for (Entry<String, FrameVectorValuesHolder> featureData : corpusEntry.getFeatureFrameVectorValuesMap().entrySet()) {
			target.put(featureData.getKey(), featureData.getValue().getValues());
		}
		return bestMatchesForFeatures(target);
	}
	/**
	 * 
	 */
	public Map<String, RecognitionResult> bestMatchesForFeatures(
			Map<String, IValues> target) {
		Map<String, RecognitionResult> match = new HashMap<String, RecognitionResult>();
		for (SignalSegment corpusSample : getCorpus().findAllEntries()) {
			long start = System.currentTimeMillis();
			for (Map.Entry<String, IValues> targetEntry : target.entrySet()) {
				String featureName = targetEntry.getKey();
				RecognitionResult result1 = compare(featureName,
						targetEntry.getValue(), corpusSample);
				// match if this best for feature
				if (match.get(featureName) == null) {
					// feature never seen
					match.put(featureName, result1);
				} else {
					Double prevDistance = match.get(featureName).getDistance();
					if (result1!= null && NumberUtils
							.compare(result1.getDistance(), prevDistance) < 0) {
						match.put(featureName, result1);
					}
				}
				LOG.debug(
						"[bestMatchesForFeatures] iteration for [{1}] in {2} ms. score: {3} ",
						 corpusSample.getName(),
						(System.currentTimeMillis() - start), match);
			}
		}
		return match;
	}

	/**
	 * find best match in the corpus
	 * 
	 * @param target
	 * @return
	 */
	protected RecognitionResult findBestMatch(Map<String, IValues> target) {
		List<RecognitionResult> results = new ArrayList<RecognitionResult>();
		Map<String, Double> minimum = new HashMap<String, Double>();
		Map<String, Double> maximum = new HashMap<String, Double>();
		int i = 1;
		Long processedCount = 0L;
		Long totalToProcess = getCorpus().count()*target.size();
		started(totalToProcess);
		
		for (SignalSegment corpusSample : getCorpus().findAllEntries()) {
			long start = System.currentTimeMillis();
			Long targetLength = target.values().iterator().next().getTime();
			Long sampleLength = targetLength;
			if(corpusSample.getMarker()!=null){
				sampleLength = corpusSample.getMarker().getLength();
			}
			
			RecognitionResult result = new RecognitionResult();
			result.setScores(new HashMap<String, Double>());
			result.setInfo(corpusSample);
			for (Map.Entry<String, IValues> targetEntry : target.entrySet()) {
				String featureName = targetEntry.getKey();
				RecognitionResult result1 = compare(featureName,
						targetEntry.getValue(), corpusSample);
				if (result1 == null) {
					LOG.debug("[findBestMatch]result not found");
					continue;
				}
				result.getScores().put(featureName, result1.getDistance());
				updateMinMax(featureName, result1.getDistance(), minimum,
						maximum, sampleLength, targetLength );
				processed(processedCount++, totalToProcess);
			}
			
			if(!result.getScores().isEmpty()){
				results.add(result);
				LOG.debug(
						"[findBestMatch] {0}. iteration for [{1}] in {2} ms.  ",
						i++, corpusSample.getName(),
						(System.currentTimeMillis() - start));
			}
			if (results.size() > 100) {
				results = postProcessResult(results, minimum, maximum);
			}
			started(getCorpus().count()*target.size());
		}
		results = postProcessResult(results, minimum, maximum);
		LOG.info(MessageFormat.format("[findBestMatch] sample: {0}", results));
		if (results.isEmpty()) {
			return null;
		}
		ended();
		return results.get(0);
	}

	
	/**
	 * 
	 * @param key
	 * @param targetEntry
	 * @param sampleFeatureData
	 * @return
	 */
	protected DtwResult calculateInfo(String key, IValues targetEntry, IValues sampleFeatureData) {
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

	public void processed(Long current, Long total) {
		for (ProcessedFrameLinstener linstener : getListeners()) {
			linstener.processed(current, total);
		}
	}

	public void started(Long total) {
		for (ProcessedFrameLinstener linstener : getListeners()) {
			linstener.started(total);
		}
	}
	public void ended() {
		for (ProcessedFrameLinstener linstener : getListeners()) {
			linstener.ended();
		}
	}
	
	
	public void setCorpus(CorpusRepository corpus) {
		this.corpus = corpus;
	}

	public CorpusRepository getCorpus() {
		if (corpus == null) {
			corpus = new CorpusRepositoryFileImpl();
		}
		return corpus;
	}

	public DtwService getDtwService() {
		if (dtwService == null) {
			if(searchRadius== null ||  javaMLSearchWindow ==null ){
				dtwService = MathServicesFactory.createDtwService();
			}else{
				dtwService = MathServicesFactory.createDtwService(getSearchRadius(), getJavaMLSearchWindow(),getJavaMLLocalConstraint());
				
			}
		}
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

	public Float getSearchRadius() {
		return searchRadius;
	}

	public void setSearchRadius(Float searchRadius) {
		this.searchRadius = searchRadius;
		dtwService =null;
	}

	public JavaMLSearchWindow getJavaMLSearchWindow() {
		return javaMLSearchWindow;
	}

	public void setJavaMLSearchWindow(JavaMLSearchWindow javaMLSearchWindow) {
		this.javaMLSearchWindow = javaMLSearchWindow;
		dtwService =null;
	}

	public JavaMLLocalConstraint getJavaMLLocalConstraint() {
		return javaMLLocalConstraint;
	}

	public void setJavaMLLocalConstraint(JavaMLLocalConstraint javaMLLocalConstraint) {
		this.javaMLLocalConstraint = javaMLLocalConstraint;
	}
	
	public Set<ProcessedFrameLinstener> getListeners() {
		if(listeners == null){
			listeners = new LinkedHashSet<ProcessedFrameLinstener>();
		}
		return listeners;
	}

}

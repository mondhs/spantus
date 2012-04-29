package org.spantus.externals.recognition.services;

import java.awt.Point;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.sound.sampled.AudioInputStream;

import org.spantus.core.FrameValues;
import org.spantus.core.FrameVectorValues;
import org.spantus.core.IValues;
import org.spantus.core.beans.FrameValuesHolder;
import org.spantus.core.beans.FrameVectorValuesHolder;
import org.spantus.core.beans.IValueHolder;
import org.spantus.core.beans.RecognitionResult;
import org.spantus.core.beans.RecognitionResultDetails;
import org.spantus.core.beans.SignalSegment;
import org.spantus.core.service.CorpusRepository;
import org.spantus.core.service.CorpusService;
import org.spantus.externals.recognition.corpus.CorpusRepositoryFileImpl;
import org.spantus.logger.Logger;
import org.spantus.math.NumberUtils;
import org.spantus.math.dtw.DtwResult;
import org.spantus.math.dtw.DtwService;
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

	private Integer searchRadius;

	private JavaMLSearchWindow javaMLSearchWindow;

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
	public List<RecognitionResultDetails> findMultipleMatch(
			Map<String, IValues> target) {
		Long begin = System.currentTimeMillis();
		LOG.debug("[findMultipleMatch]+++ ");
		List<RecognitionResultDetails> results = new ArrayList<RecognitionResultDetails>();
		if (target == null || target.isEmpty()) {
			return results;
		}
		Map<String, Double> minimum = new HashMap<String, Double>();
		Map<String, Double> maximum = new HashMap<String, Double>();

		// iterate all entires in corpus
		for (SignalSegment sample : getCorpus().findAllEntries()) {
			LOG.debug("[findMultipleMatch] sample: {0} ", 
					sample.getName());
			RecognitionResultDetails result = createRecognitionResultDetail();

			for (Map.Entry<String, IValues> targetEntry : target.entrySet()) {
				if (sample.getFeatureFrameVectorValuesMap().get(targetEntry.getKey()) == null
						|| sample.getFeatureFrameVectorValuesMap().get(targetEntry.getKey())
								.getValues() == null) {

				}
				String featureName = targetEntry.getKey();
				IValueHolder<?> sampleFeatureData = sample.getFeatureFrameVectorValuesMap().get(
						targetEntry.getKey());
				if (sampleFeatureData == null) {
					continue;
				}

				// log.debug("[findMultipleMatch] target [{0}]: {1} ",
				// featureName, targetEntry.getValue());
				// log.debug("[findMultipleMatch] sample [{0}]: {1} ",
				// featureName,sampleFeatureData.getValues());
				result.getSampleLegths().put(
						featureName,
						 (double) Math.round(sampleFeatureData.getValues()
								.getTime() * 1000));
				result.getTargetLegths()
						.put(featureName,
								 (double) Math.round(targetEntry.getValue()
										.getTime() * 1000));
				result.setInfo(sample);

				DtwResult dtwResult = null;
				int targetDimention = targetEntry.getValue().getDimention();
				// you know your stuff
				if (targetDimention == 1) {
					dtwResult = (DtwResult) getDtwService().calculateInfo(
							(FrameValues) targetEntry.getValue(),
							(FrameValues) sampleFeatureData.getValues());
				} else {
					if (targetDimention  != sampleFeatureData
							.getValues().getDimention()) {
						LOG.error("[findMultipleMatch] Sample size not same "
								+ targetEntry.getKey()
								+ targetDimention + "!="
								+ sampleFeatureData.getValues().getDimention());
						continue;
					}
					dtwResult = getDtwService().calculateInfoVector(
							(FrameVectorValues) targetEntry.getValue(),
							(FrameVectorValues) sampleFeatureData.getValues());
				}
				result.getPath().put(featureName, dtwResult.getPath());
				result.getScores().put(featureName, dtwResult.getResult());
				updateMinMax(featureName, dtwResult.getResult(), minimum,
						maximum);
			}
			result.setDistance(null);
			result.setAudioFilePath(getCorpus().findAudioFileById(
					result.getInfo().getId()));
			results.add(result);
		}
		results = postProcessResult(results, minimum, maximum);
		LOG.debug("[findMultipleMatch]--- in {0} ms ",
				System.currentTimeMillis() - begin);
		return results;
	}

	/**
	 * 
	 * @return
	 */
	private RecognitionResultDetails createRecognitionResultDetail() {
		RecognitionResultDetails result = new RecognitionResultDetails();
		result.setPath(new HashMap<String, List<Point>>());
		result.setPath(new HashMap<String, List<Point>>());
		result.setScores(new HashMap<String, Double>());
		result.setTargetLegths(new HashMap<String, Double>());
		result.setSampleLegths(new HashMap<String, Double>());
		return result;
	}

	/**
	 * Update information with min max for each feature
	 * 
	 * @param feature
	 * @param value
	 * @param minimum
	 * @param maximum
	 */
	private void updateMinMax(String feature, Double value,
			Map<String, Double> minimum, Map<String, Double> maximum) {
		// log.debug("[updateMinMax] feature [{0}]: {1} ", feature, value);
		if (minimum.get(feature) == null) {
			minimum.put(feature, Double.MAX_VALUE);
		}
		if (maximum.get(feature) == null) {
			maximum.put(feature, -Double.MAX_VALUE);
		}
		if (minimum.get(feature).compareTo(value) > 0) {
			// log.debug("[updateMinMax] [{2}] minimum {0}>{1}",
			// minimum.get(feature),value, feature);
			minimum.put(feature, value);
		}
		if (maximum.get(feature).compareTo(value) < 0) {
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
			Double normalizedSum = 0D;

			for (Entry<String, Double> score : result.getScores().entrySet()) {
				 Double min = minimum.get(score.getKey());
				 Double max = maximum.get(score.getKey());
				 Double delta = max-min;
				 Double normalizedScore = (score.getValue() - min)/delta;
//				Double normalizedScore = score.getValue();

				if (getIncludeFeatures() == null
						|| getIncludeFeatures().isEmpty()) {
					normalizedSum += normalizedScore;
				} else if (getIncludeFeatures().contains(score.getKey())) {
					normalizedSum += normalizedScore;
				}

				normalizedScores.put(score.getKey(), normalizedScore);
			}
			result.setDistance(normalizedSum);
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
		for (SignalSegment corpusSample : getCorpus().findAllEntries()) {
			long start = System.currentTimeMillis();
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
						maximum);
			}
			results.add(result);
			LOG.debug(
					"[findBestMatch] {0}. iteration for [{1}] in {2} ms. score: {3} ",
					i++, corpusSample.getName(),
					(System.currentTimeMillis() - start), result.getScores()
							.get("MFCC_EXTRACTOR"));
			if (results.size() > 100) {
				results = postProcessResult(results, minimum, maximum);
			}
		}
		results = postProcessResult(results, minimum, maximum);
		LOG.info(MessageFormat.format("[findBestMatch] sample: {0}", results));
		if (results.isEmpty()) {
			return null;
		}
		return results.get(0);
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
		RecognitionResult result = new RecognitionResult();
		result.setInfo(sample);
		if (targetValues.getDimention() == 1) {
			if(((FrameValues) targetValues).size()<2){
				return null;
			}
			result.setDistance(getDtwService().calculateDistance(
					(FrameValues) targetValues, (FrameValues) fd.getValues()));
		} else {
			if(((FrameVectorValues) targetValues).size()<2){
				return null;
			}
			result.setDistance(getDtwService().calculateDistanceVector(
					(FrameVectorValues) targetValues,
					(FrameVectorValues) fd.getValues()));
		}

		return result;
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
				dtwService = MathServicesFactory.createDtwService(searchRadius, javaMLSearchWindow);
				
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

	public Integer getSearchRadius() {
		return searchRadius;
	}

	public void setSearchRadius(Integer searchRadius) {
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

}

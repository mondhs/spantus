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
import java.util.TreeMap;
import org.spantus.core.FrameVectorValues;

import org.spantus.externals.recognition.bean.CorpusEntry;
import org.spantus.externals.recognition.bean.FeatureData;
import org.spantus.externals.recognition.bean.RecognitionResult;
import org.spantus.externals.recognition.bean.RecognitionResultDetails;
import org.spantus.externals.recognition.corpus.CorpusRepository;
import org.spantus.externals.recognition.corpus.CorpusRepositoryFileImpl;
import org.spantus.logger.Logger;
import org.spantus.math.dtw.DtwResult;
import org.spantus.math.dtw.DtwService;
import org.spantus.math.services.MathServicesFactory;
/**
 * 
 * @author Mindaugas Greibus
 *
 */
public class CorpusServiceBaseImpl implements CorpusService {

	private Logger log = Logger.getLogger(getClass());

	
	private DtwService dtwService;

	private CorpusRepository corpus;
	

	public RecognitionResult match(Map<String, FrameVectorValues> target) {
		RecognitionResult match = findBestMatch(target);
		return match;
	}
        /**
         * Find mutliple match
         * 
         * @param target
         * @return
         */
	public List<RecognitionResultDetails> findMultipleMatch(Map<String, FrameVectorValues> target) {
                List<RecognitionResultDetails> results = new ArrayList<RecognitionResultDetails>();
		if(target == null || target.isEmpty()){
                    return results;
                }
                Map<String, Float> minimum = new HashMap<String, Float> ();
                Map<String, Float> maximum = new HashMap<String, Float> ();

                //iterate all entires in corpus
                for (CorpusEntry sample : getCorpus().findAllEntries()) {
                        log.debug("[findMultipleMatch] sample [{0}]: {1} ", sample.getId(), sample.getName());
                        RecognitionResultDetails result = new RecognitionResultDetails();
                        result.setPath(new HashMap<String, List<Point>>());
                        result.setScores(new HashMap<String, Float>());
                        for (Map.Entry<String, FrameVectorValues> entry : target.entrySet()) {
                            if(sample.getFeatureMap().get(entry.getKey()).getValues() == null){
                                continue;
                            }
                            String featureName =  entry.getKey();
                            log.debug("[findMultipleMatch] entry [{0}]: {1} ", featureName, entry.getValue());
                            log.debug("[findMultipleMatch] sample [{0}]: {1} ", featureName, sample.getFeatureMap().get(entry.getKey()).getValues());
                            result.setInfo(sample);
                            DtwResult dtwResult = getDtwService().calculateInfoVector(entry.getValue(),
                                    sample.getFeatureMap().get(entry.getKey()).getValues());
                            result.getPath().put(featureName,dtwResult.getPath());
                            result.getScores().put(featureName, dtwResult.getResult());
                            updateMinMax(featureName, dtwResult.getResult(), minimum, maximum);
                        }
                        result.setDistance(null);
                        results.add(result);
		}
                results = postProcessResult(results, minimum, maximum);
		return results;
        }
        /**
         * Update information with min max for each feature
         * @param feature
         * @param value
         * @param minimum
         * @param maximum
         */
        private void updateMinMax(String feature, Float value, Map<String, Float> minimum, Map<String, Float> maximum) {
            log.debug("[updateMinMax] feature [{0}]: {1} ", feature, value);
            if(minimum.get(feature)==null){
                minimum.put(feature, Float.MAX_VALUE);
            }
            if(maximum.get(feature)==null){
                maximum.put(feature, -Float.MAX_VALUE);
            }
            if(minimum.get(feature).compareTo(value)>0){
                log.debug("[updateMinMax] [{2}] minimum {0}>{1}",
                        minimum.get(feature),value, feature);
                minimum.put(feature, value);
            }
            if(maximum.get(feature).compareTo(value)<0){
                log.debug("[updateMinMax] [{2}] maximum {0}<{1}",maximum.get(feature)
                        ,value, feature);
                maximum.put(feature, value);
            }
            log.debug("[updateMinMax][{0}] [{1}]", minimum, maximum);
        }
        /**
         * post process multi match result. normalize and ordering
         * @param results
         * @return
         */
        private List<RecognitionResultDetails> postProcessResult(List<RecognitionResultDetails> results,
                Map<String, Float> minimum, Map<String, Float> maximum) {
            log.debug("[postProcessResult]+++");
            for (RecognitionResultDetails result : results) {
                Map<String,Float> normalizedScores = new HashMap<String, Float>();
                Float normalizedSum = 0F;
                for (Entry<String,Float> score : result.getScores().entrySet()) {
                    float min = minimum.get(score.getKey());
                    float max = maximum.get(score.getKey());
                    float delta = max-min;
                    float normalizedScore = (score.getValue() - min)/delta;
                    normalizedSum += normalizedScore;
                    normalizedScores.put(score.getKey(), normalizedScore);
                }
                result.setDistance(normalizedSum);
                result.setScores(normalizedScores);
            }
            log.debug("[postProcessResult] results before sort: {0}", results);
            Collections.sort(results, new Comparator<RecognitionResultDetails>(){
                public int compare(RecognitionResultDetails o1, RecognitionResultDetails o2) {
                    return o1.getDistance().compareTo(o2.getDistance());
                }
            });
            log.debug("[postProcessResult] results after sort: {0}", results);
            log.debug("[postProcessResult]---");
            return results;
        }

        /**
         * learn with multiple features
         * @param label
         * @param featureDataMap
         * @return
         */
        public boolean learn(String label, Map<String, FrameVectorValues> featureDataMap) {
            CorpusEntry entry = new CorpusEntry();
            entry.setName(label);
            for (Map.Entry<String, FrameVectorValues> entry1 : featureDataMap.entrySet()) {
                FeatureData fd = new FeatureData();
                fd.setName(entry1.getKey());
                fd.setValues(entry1.getValue());
                entry.getFeatureMap().put(entry1.getKey(), fd);
            }

            getCorpus().save(entry);
            return true;
        }
	/**
	 * find best match in the corpus
	 * @param target
	 * @return
	 */
	protected RecognitionResult findBestMatch(Map<String, FrameVectorValues> target){
		TreeMap<Float, RecognitionResult> results = new TreeMap<Float, RecognitionResult>();
		Map<String, Float> min = new HashMap<String, Float>();
		RecognitionResult match = null;
                int matchMinCount = Integer.MAX_VALUE;
		for (CorpusEntry corpusSample : getCorpus().findAllEntries()) {
                    int minCount = 0;
                    for (Map.Entry<String, FrameVectorValues> targetEntry : target.entrySet()) {
                       String featureName =  targetEntry.getKey();
                       RecognitionResult result = compare(featureName, targetEntry.getValue(), corpusSample);
                        if (result == null) {
                            log.debug("[findBestMatch]result not found");
                            continue;
                        }
                        if(min.get(featureName) == null){
                            min.put(featureName, Float.MAX_VALUE);
                        }
			if(min.get(featureName) > result.getDistance()){
				min.put(featureName, result.getDistance());
                                minCount++;
                                log.debug("[findBestMatch]new min {0};feature:{3}[{1}]; mincount: {2};",
                                        corpusSample.getName(),
                                        result.getDistance(),
                                        minCount,
                                        featureName);

			}
			if(log.isDebugMode()) 
				results.put(result.getDistance(),result);
                    }
                    if(matchMinCount>minCount){
                        match = new RecognitionResult();
                        match.setDistance(0F + minCount);
                        match.setInfo(corpusSample);
                        log.debug("[findBestMatch]new match {0}", match);
                        matchMinCount = minCount;
                    }
		}
		
		if(log.isDebugMode()){
			log.debug("[findBestMatch] sample: {0};[{1}]", match, results);
		}
		log.info(MessageFormat.format("[findBestMatch] sample: {0};[{1}]", match, results.values()));
		return match;
	}
	/**
         * 
         * @param target
         * @param sample
         * @return
         */
	protected RecognitionResult compare(String featureName, FrameVectorValues targetValues,
			CorpusEntry sample) {
             if(sample.getFeatureMap().get(featureName) == null){
                 return null;
             }
		RecognitionResult result = new RecognitionResult();
		result.setInfo(sample);
		result.setDistance(getDtwService().calculateDistanceVector(targetValues,
				sample.getFeatureMap().get(featureName).getValues()
		));
		return result;
	}

	public void setCorpus(CorpusRepository corpus) {
		this.corpus = corpus;
	}
	
	public CorpusRepository getCorpus() {
		if(corpus == null){
			corpus = new CorpusRepositoryFileImpl();
		}
		return corpus;
	}

	public DtwService getDtwService() {
		if(dtwService == null){
			dtwService = MathServicesFactory.createDtwService();
		}
		return dtwService;
	}




}

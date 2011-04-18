package org.spantus.externals.recognition.services;

import java.awt.Point;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import javax.sound.sampled.AudioInputStream;

import org.spantus.core.FrameValues;
import org.spantus.core.FrameVectorValues;
import org.spantus.core.IValues;
import org.spantus.externals.recognition.bean.CorpusEntry;
import org.spantus.externals.recognition.bean.FeatureData;
import org.spantus.externals.recognition.bean.RecognitionResult;
import org.spantus.externals.recognition.bean.RecognitionResultDetails;
import org.spantus.externals.recognition.corpus.CorpusRepository;
import org.spantus.externals.recognition.corpus.CorpusRepositoryFileImpl;
import org.spantus.logger.Logger;
import org.spantus.math.NumberUtils;
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

        private Set<String> includeFeatures;

        public RecognitionResult match(Map<String, IValues> target) {
		RecognitionResult match = findBestMatch(target);
		return match;
	}
        
        public RecognitionResult matchByCorpusEntry(CorpusEntry corpusEntry) {
            Map<String, IValues> target = new HashMap<String, IValues>();
            for (FeatureData featureData : corpusEntry.getFeatureMap().values()) {
                target.put(featureData.getName(), featureData.getValues());
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
	public List<RecognitionResultDetails> findMultipleMatch(Map<String, IValues> target) {
			Long begin = System.currentTimeMillis();
			log.debug("[findMultipleMatch]+++ ");
                List<RecognitionResultDetails> results = new ArrayList<RecognitionResultDetails>();
		if(target == null || target.isEmpty()){
                    return results;
                }
                Map<String, Float> minimum = new HashMap<String, Float> ();
                Map<String, Float> maximum = new HashMap<String, Float> ();

                //iterate all entires in corpus
                for (CorpusEntry sample : getCorpus().findAllEntries()) {
                        log.debug("[findMultipleMatch] sample [{0}]: {1} ", sample.getId(), sample.getName());
                        RecognitionResultDetails result = createRecognitionResultDetail();

                        for (Map.Entry<String, IValues> targetEntry : target.entrySet()) {
                            if(sample.getFeatureMap().get(targetEntry.getKey()) == null ||
                                    sample.getFeatureMap().get(targetEntry.getKey()).getValues() == null){

                            }
                            String featureName =  targetEntry.getKey();
                            FeatureData sampleFeatureData = sample.getFeatureMap().get(targetEntry.getKey());
                            if(sampleFeatureData == null){
                            	continue;
                            }
                            	
//                            log.debug("[findMultipleMatch] target [{0}]: {1} ", featureName, targetEntry.getValue());
//                            log.debug("[findMultipleMatch] sample [{0}]: {1} ", featureName,sampleFeatureData.getValues());
                            result.getSampleLegths().put(featureName, 
                                   (float)Math.round( sampleFeatureData.getValues().getTime()*1000));
                            result.getTargetLegths().put(featureName, 
                                    (float)Math.round(targetEntry.getValue().getTime()*1000));
                            result.setInfo(sample);

                            DtwResult dtwResult = null;
                            //yuo know your stuff
                            if(targetEntry.getValue().getDmention()==1){
                                dtwResult = (DtwResult) getDtwService().calculateInfo(
                                    (FrameValues)targetEntry.getValue(),
                                    (FrameValues)sampleFeatureData.getValues());
                            }else{
                                if(targetEntry.getValue().getDmention() != 
                                        sampleFeatureData.getValues().getDmention()){
                                    log.error("[findMultipleMatch] Sample size not same "
                                            +targetEntry.getKey() +
                                            targetEntry.getValue().getDmention() 
                                            +"!=" + sampleFeatureData.getValues().getDmention()
                                            );
                                    continue;
                                }
                                dtwResult = getDtwService().calculateInfoVector(
                                        (FrameVectorValues)targetEntry.getValue(),
                                    (FrameVectorValues)sampleFeatureData.getValues());
                            }
                            result.getPath().put(featureName,dtwResult.getPath());
                            result.getScores().put(featureName, dtwResult.getResult());
                            updateMinMax(featureName, dtwResult.getResult(), minimum, maximum);
                        }
                        result.setDistance(null);
                        result.setAudioFilePath(getCorpus().findAudioFileById(result.getInfo().getId()));
                        results.add(result);
		}
                results = postProcessResult(results, minimum, maximum);
                log.debug("[findMultipleMatch]--- in {0} ms ", System.currentTimeMillis() - begin);
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
            result.setScores(new HashMap<String, Float>());
            result.setTargetLegths(new HashMap<String, Float>());
            result.setSampleLegths(new HashMap<String, Float>());
            return result;
        }
        /**
         * Update information with min max for each feature
         * @param feature
         * @param value
         * @param minimum
         * @param maximum
         */
        private void updateMinMax(String feature, Float value, Map<String, Float> minimum, Map<String, Float> maximum) {
//            log.debug("[updateMinMax] feature [{0}]: {1} ", feature, value);
            if(minimum.get(feature)==null){
                minimum.put(feature, Float.MAX_VALUE);
            }
            if(maximum.get(feature)==null){
                maximum.put(feature, -Float.MAX_VALUE);
            }
            if(minimum.get(feature).compareTo(value)>0){
//                log.debug("[updateMinMax] [{2}] minimum {0}>{1}",
//                        minimum.get(feature),value, feature);
                minimum.put(feature, value);
            }
            if(maximum.get(feature).compareTo(value)<0){
//                log.debug("[updateMinMax] [{2}] maximum {0}<{1}",maximum.get(feature)
//                        ,value, feature);
                maximum.put(feature, value);
            }
//            log.debug("[updateMinMax][{0}] [{1}]", minimum, maximum);
        }
        /**
         * post process multi match result. normalize and ordering
         * @param results
         * @return
         */
        private <T extends RecognitionResult> List<T> postProcessResult(List<T> results,
                Map<String, Float> minimum, Map<String, Float> maximum) {
//            log.debug("[postProcessResult]+++");
            
            for (RecognitionResult result : results) {
                Map<String,Float> normalizedScores = new HashMap<String, Float>();
                Float normalizedSum = 0F;
                
                for (Entry<String,Float> score : result.getScores().entrySet()) {
//                    float min = minimum.get(score.getKey());
//                    float max = maximum.get(score.getKey());
//                    float delta = max-min;
//                    float normalizedScore = (score.getValue() - min)/delta;
                    float normalizedScore = score.getValue();

                    if(getIncludeFeatures() == null || getIncludeFeatures().isEmpty()){
                        normalizedSum += normalizedScore;
                    }else if(getIncludeFeatures().contains(score.getKey())){
                        normalizedSum += normalizedScore;
                    }
                    
                    normalizedScores.put(score.getKey(), normalizedScore);
                }
                result.setDistance(normalizedSum);
                result.setScores(normalizedScores);
            }
//            log.debug("[postProcessResult] results before sort: {0}", results);
            Collections.sort(results, new Comparator<RecognitionResult>(){
                public int compare(RecognitionResult o1, RecognitionResult o2) {
                    return NumberUtils.compare(o1.getDistance(), o2.getDistance());
                }
            });
            int maxElementSize = NumberUtils.min(20, results.size()); 
            
            log.debug("[postProcessResult] results after sort: {0}", results);
//            log.debug("[postProcessResult]---");
            return results.subList(0, maxElementSize);
        }
        
        /**
         * Same as {@link #learn(java.lang.String, java.util.Map)} only with audio
         * stream
         * @param label
         * @param featureDataMap
         * @param audioStream
         * @return
         */
        public CorpusEntry learn(CorpusEntry corpusEntry, AudioInputStream audioStream) {
//            CorpusEntry corpusEntry = create(label, featureDataMap);
            CorpusEntry learnedCorpusEntry = getCorpus().update(corpusEntry, audioStream);
            return learnedCorpusEntry;
        }
        /**
         * 
         * @param label
         * @param featureDataMap
         * @return
         */
        public CorpusEntry create(String label, Map<String, IValues> featureDataMap) {
            return getCorpus().create(label, featureDataMap);
        }
        
        /**
         * learn with multiple features
         * @param label
         * @param featureDataMap
         * @return
         */
        public CorpusEntry learn(String label, Map<String, IValues> featureDataMap) {
            CorpusEntry corpusEntry =  create(label, featureDataMap);
            return getCorpus().save(corpusEntry);
        }
	/**
	 * find best match in the corpus
	 * @param target
	 * @return
	 */
	protected RecognitionResult findBestMatch(Map<String, IValues> target){
                List<RecognitionResult> results = new ArrayList<RecognitionResult>();
                Map<String, Float> minimum = new HashMap<String, Float> ();
                Map<String, Float> maximum = new HashMap<String, Float> ();
                int i = 1;
                for (CorpusEntry corpusSample : getCorpus().findAllEntries()) {
                	long start = System.currentTimeMillis();
                    RecognitionResult result = new RecognitionResult();
                    result.setScores(new HashMap<String, Float>());
                    result.setInfo(corpusSample);
                    for (Map.Entry<String, IValues> targetEntry : target.entrySet()) {
                       String featureName =  targetEntry.getKey();
                       RecognitionResult result1 = compare(featureName, targetEntry.getValue(), corpusSample);
                        if (result1 == null) {
                            log.debug("[findBestMatch]result not found");
                            continue;
                        } 
                        result.getScores().put(featureName, result1.getDistance());
                        updateMinMax(featureName, result1.getDistance(), minimum, maximum);
                    }
                    results.add(result);
                    log.debug("[findBestMatch] {0}. iteration for [{1}] in {2} ms. score: {3} ", 
                    		i++,
                    		corpusSample.getName(),
                    		(System.currentTimeMillis()-start),
                    		result.getScores().get("MFCC_EXTRACTOR"));
                    if(results.size()>100){
                    	results = postProcessResult(results, minimum, maximum);
                    }
				}
				results = postProcessResult(results, minimum, maximum);
				log.info(MessageFormat.format("[findBestMatch] sample: {0}",  results));
				if(results.isEmpty()){
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
	protected RecognitionResult compare(String featureName, IValues targetValues,
			CorpusEntry sample) {
             FeatureData fd = sample.getFeatureMap().get(featureName);
             if(fd == null){
                 return null;
             }
		RecognitionResult result = new RecognitionResult();
		result.setInfo(sample);
                if(targetValues.getDmention()==1){
                     result.setDistance(getDtwService().calculateDistance(
                            (FrameValues)targetValues,
                            (FrameValues)fd.getValues()
                    ));
                }else{
                    result.setDistance(getDtwService().calculateDistanceVector(
                            (FrameVectorValues)targetValues,
                            (FrameVectorValues)fd.getValues()
                    ));
                }

		
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

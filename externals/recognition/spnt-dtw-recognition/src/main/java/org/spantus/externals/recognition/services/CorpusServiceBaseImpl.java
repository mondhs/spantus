package org.spantus.externals.recognition.services;

import java.awt.Point;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
import org.spantus.utils.CollectionUtils;
/**
 * 
 * @author Mindaugas Greibus
 *
 */
public class CorpusServiceBaseImpl implements CorpusService {

	private Logger log = Logger.getLogger(getClass()); 
	
	private DtwService dtwService;

	private CorpusRepository corpus;
	

	public RecognitionResult match(FeatureData featureData) {
		RecognitionResult match = findBestMatch(featureData);
		return match;
	}
        /**
         * 
         * @param target
         * @return
         */
	public List<RecognitionResultDetails> findMultipleMatch(Map<String, FrameVectorValues> target) {
                TreeMap<Float, RecognitionResultDetails> results = new TreeMap<Float, RecognitionResultDetails>();
		if(target == null || target.isEmpty()){
                    return new ArrayList<RecognitionResultDetails>(results.values());
                }
                for (CorpusEntry sample : getCorpus().findAllEntries()) {
                        RecognitionResultDetails result = new RecognitionResultDetails();
                        result.setPath(new HashMap<String, List<Point>>());
                        Float distance = 0F;
                        for (Map.Entry<String, FrameVectorValues> entry : target.entrySet()) {
                            if(sample.getFeatureMap().get(entry.getKey()).getValues() == null){
                                continue;
                            }

                            result.setInfo(sample);
                            DtwResult dtwResult = getDtwService().calculateInfoVector(entry.getValue(),
                                    sample.getFeatureMap().get(entry.getKey()).getValues());
                            result.getPath().put(entry.getKey(),dtwResult.getPath());
                            distance +=dtwResult.getResult();
                        }
                        result.setDistance(distance);
                        results.put(distance,result);
		}
                if(results.isEmpty()){
                    return new ArrayList<RecognitionResultDetails>();
                }
		return new ArrayList<RecognitionResultDetails>(results.values());
        }
        /**
         * learn on single feature value
         *
         * @param label
         * @param featureData
         * @return
         */
	public boolean learn(String label, FeatureData featureData) {
		CorpusEntry entry = new CorpusEntry();
		entry.setName(label);
		entry.getFeatureMap().put(featureData.getName(), featureData);
		getCorpus().save(entry);
		return true;
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
	protected RecognitionResult findBestMatch(FeatureData target){
		TreeMap<Float, RecognitionResult> results = new TreeMap<Float, RecognitionResult>();
		Float min = Float.MAX_VALUE;
		RecognitionResult match = null;
		for (CorpusEntry sample : getCorpus().findAllEntries()) {
			RecognitionResult res = compare(target, sample);
			if(min > res.getDistance()){
				min = res.getDistance();
				match = res;
			}
			if(log.isDebugMode()) 
				results.put(res.getDistance(),res);
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
	protected RecognitionResult compare(FeatureData target,
			CorpusEntry sample) {
		RecognitionResult result = new RecognitionResult();
		result.setInfo(sample);
		result.setDistance(getDtwService().calculateDistanceVector(target.getValues(), 
				sample.getFeatureMap().get(target.getName()).getValues()
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

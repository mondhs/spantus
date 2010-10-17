package org.spantus.externals.recognition.services;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.TreeMap;

import org.spantus.externals.recognition.bean.CorpusEntry;
import org.spantus.externals.recognition.bean.FeatureData;
import org.spantus.externals.recognition.bean.RecognitionResult;
import org.spantus.externals.recognition.corpus.CorpusRepository;
import org.spantus.externals.recognition.corpus.CorpusRepositoryFileImpl;
import org.spantus.logger.Logger;
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
	public List<RecognitionResult> multipleMatch(FeatureData target) {
                TreeMap<Float, RecognitionResult> results = new TreeMap<Float, RecognitionResult>();
		for (CorpusEntry sample : getCorpus().findAllEntries()) {
			RecognitionResult res = compare(target, sample);
			results.put(res.getDistance(),res);
		}

		if(log.isDebugMode()){
			log.debug("[multipleMatch] sample: {0}", results);
		}
                if(results.isEmpty()){
                    return new ArrayList<RecognitionResult>();
                }
		return new ArrayList<RecognitionResult>(results.values());
        }

	public boolean learn(String label, FeatureData featureData) {
		CorpusEntry entry = new CorpusEntry();
		entry.setName(label);
		entry.getFeatureMap().put(featureData.getName(), featureData);
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

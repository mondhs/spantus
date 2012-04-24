package org.spantus.server.services.impl;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.sound.sampled.AudioInputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spantus.core.IValues;
import org.spantus.core.beans.FrameValuesHolder;
import org.spantus.core.beans.FrameVectorValuesHolder;
import org.spantus.core.beans.IValueHolder;
import org.spantus.core.beans.RecognitionResult;
import org.spantus.core.beans.RecognitionResultDetails;
import org.spantus.core.beans.SignalSegment;
import org.spantus.core.service.CorpusService;
import org.spantus.exception.ProcessingException;
import org.spantus.math.dtw.DtwResult;
import org.spantus.server.dto.SignalSegmentEntry;
import org.spantus.server.services.recognition.repository.SignalSegmentEntryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component(value = "corpusService")
public class CorpusServiceServerImpl implements CorpusService {
	private static final Logger LOG = LoggerFactory
			.getLogger(CorpusServiceServerImpl.class);

	@Autowired
	SignalSegmentEntryRepository signalSegmentEntryRepository;

	private CorpusServiceHelperImpl corpusServiceHelper = new CorpusServiceHelperImpl();

	@Override
	public RecognitionResult matchByCorpusEntry(SignalSegment corpusEntry) {
		Map<String, IValues> target = new HashMap<String, IValues>();

		for (Entry<String, FrameVectorValuesHolder> featureData : corpusEntry
				.getFeatureFrameVectorValuesMap().entrySet()) {
			target.put(featureData.getKey(), featureData.getValue().getValues());
		}
		for (Entry<String, FrameValuesHolder> featureData : corpusEntry
				.getFeatureFrameValuesMap().entrySet()) {
			target.put(featureData.getKey(), featureData.getValue().getValues());
		}
		RecognitionResult match = match(target);
		return match;
	}

	@Override
	public RecognitionResult match(Map<String, IValues> target) {
		List<RecognitionResult> results = new ArrayList<RecognitionResult>();
		Map<String, Double> minimum = new HashMap<String, Double>();
		Map<String, Double> maximum = new HashMap<String, Double>();
		// int i = 1;
		
		for (SignalSegmentEntry corpusSample : signalSegmentEntryRepository.findAll()) {
			// long start = System.currentTimeMillis();
			RecognitionResult result = new RecognitionResult();
			result.setScores(new HashMap<String, Double>());
			result.setInfo(corpusSample.getSignalSegment());
			matchEachFeature(target, corpusSample, result, results, minimum,
					maximum);
		}
		results = corpusServiceHelper.postProcessResult(results, minimum,
				maximum);
		LOG.info(MessageFormat.format("[findBestMatch] sample: {0}", results));
		if (results.isEmpty()) {
			return null;
		}
		return results.get(0);
	}

	/**
	 * 
	 * @param target
	 * @param corpusSample
	 * @param result
	 * @param results
	 * @param minimum
	 * @param maximum
	 */
	private void matchEachFeature(Map<String, IValues> target,
			SignalSegmentEntry corpusSample, RecognitionResult result,
			List<RecognitionResult> results, Map<String, Double> minimum,
			Map<String, Double> maximum) {
		for (Map.Entry<String, IValues> targetEntry : target.entrySet()) {
			String featureName = targetEntry.getKey();
			RecognitionResult result1 = corpusServiceHelper.compare(
					featureName, targetEntry.getValue(),
					corpusSample.getSignalSegment());
			if (result1 == null) {
				LOG.debug("[findBestMatch]result not found");
				continue;
			}
			result.getScores().put(featureName, result1.getDistance());
			corpusServiceHelper.updateMinMax(featureName,
					result1.getDistance(), minimum, maximum);
		}
	}

	@Override
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

		// iterate all entries in corpus
		for (SignalSegmentEntry signalSegmentEntry : signalSegmentEntryRepository.findByRecognizable(true)) {
			SignalSegment sample = signalSegmentEntry.getSignalSegment();
			LOG.debug("[findMultipleMatch] sample: {0} ", sample.getName());
			RecognitionResultDetails result = corpusServiceHelper
					.createRecognitionResultDetail();

			for (Map.Entry<String, IValues> targetEntry : target.entrySet()) {
				String featureName = targetEntry.getKey();
				IValueHolder<?> sampleFeatureData = sample
						.getFeatureFrameVectorValuesMap().get(
								targetEntry.getKey());
				if (sampleFeatureData == null) {
					continue;
				}
				
				DtwResult dtwResult = corpusServiceHelper.findDtwResult(targetEntry,
						sampleFeatureData);
				
				updateResult(result, featureName,dtwResult,sample,sampleFeatureData,targetEntry );
				corpusServiceHelper.updateMinMax(featureName,
						dtwResult.getResult(), minimum, maximum);
			}
			result.setDistance(null);
//			result.setAudioFilePath(getCorpus().findAudioFileById(
//					result.getInfo().getId()));
			results.add(result);
		}
		results = corpusServiceHelper.postProcessResult(results, minimum,
				maximum);
		LOG.debug("[findMultipleMatch]--- in {0} ms ",
				System.currentTimeMillis() - begin);
		return results;
	}



	private void updateResult(RecognitionResultDetails result, String featureName, DtwResult dtwResult, SignalSegment sample, IValueHolder<?> sampleFeatureData, Entry<String, IValues> targetEntry) {
		result.getSampleLegths().put(
				featureName,
				(double) Math.round(sampleFeatureData.getValues()
						.getTime() * 1000));
		result.getTargetLegths()
				.put(featureName,
						(double) Math.round(targetEntry.getValue()
								.getTime() * 1000));
		result.setInfo(sample);

		result.getPath().put(featureName, dtwResult.getPath());
		result.getScores().put(featureName, dtwResult.getResult());
		
	}

	@Override
	public SignalSegment learn(String label, Map<String, IValues> featureDataMap) {
		SignalSegment signalSegment = create(label, featureDataMap);
		SignalSegmentEntry signalSegmentEntry = signalSegmentEntryRepository.save(new SignalSegmentEntry(signalSegment));
		return signalSegmentEntry.getSignalSegment();
	}

	@Override
	public Map<String, RecognitionResult> bestMatchesForFeatures(
			Map<String, IValues> target) {
		throw new ProcessingException("Not impl.");
	}

	@Override
	public Map<String, RecognitionResult> bestMatchesForFeatures(
			SignalSegment signalSegment) {
		throw new ProcessingException("Not impl.");
	}

	@Override
	public SignalSegment learn(SignalSegment corpusEntry,
			AudioInputStream audioStream) {
		throw new ProcessingException("Not impl.");
	}

	public SignalSegment create(String label,
			Map<String, IValues> featureDataMap) {
		SignalSegment signalSegment = new SignalSegment();
		signalSegment.setName(label);
		signalSegment.putAll(featureDataMap);
		return signalSegment;
	}

}

package org.spantus.core.service;

import java.util.List;
import java.util.Map;

import javax.sound.sampled.AudioInputStream;

import org.spantus.core.IValues;
import org.spantus.core.beans.RecognitionResult;
import org.spantus.core.beans.SignalSegment;

public interface CorpusService {
	public RecognitionResult matchByCorpusEntry(SignalSegment corpusEntry);

	public RecognitionResult match(Map<String, IValues> featureDataMap);
	/**
	 * find mutliple matches
	 * @param featureDataMap
	 * @return
	 */
	public List<RecognitionResult> findMultipleMatchFull(
			Map<String, IValues> featureDataMap);
	
	/**
	 * Learn
	 * 
	 * @param label
	 * @param featureDataMap
	 * @return
	 */
	public SignalSegment learn(String label, Map<String, IValues> featureDataMap);

	/**
	 * Best matches for each feature
	 * 
	 * @param target
	 * @return
	 */
	public Map<String, RecognitionResult> bestMatchesForFeatures(
			Map<String, IValues> target);

	/**
	 * Best matches for each feature
	 * 
	 * @param corpusEntry
	 * @return
	 */
	public Map<String, RecognitionResult> bestMatchesForFeatures(
			SignalSegment signalSegment);

/**
         * Same as learn {@link #learn(java.lang.String, java.util.Map)] only with
         * audio stream
         * @param label
         * @param featureDataMap
         * @param audioStream
         * @return
         */
	public SignalSegment learn(SignalSegment corpusEntry,
			AudioInputStream audioStream);

	/**
	 * create Corpus entry
	 * 
	 * @param label
	 * @param featureDataMap
	 * @return
	 */
//	public SignalSegment create(String label, Map<String, IValues> featureDataMap);

}

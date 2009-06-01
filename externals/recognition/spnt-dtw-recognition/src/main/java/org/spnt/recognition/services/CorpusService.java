package org.spnt.recognition.services;

import org.spnt.recognition.bean.FeatureData;
import org.spnt.recognition.bean.RecognitionResult;

public interface CorpusService {
	public RecognitionResult match(FeatureData featureData);
	public boolean learn(String label, FeatureData featureData);
}

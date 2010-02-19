package org.spantus.externals.recognition.services;

import org.spantus.externals.recognition.bean.FeatureData;
import org.spantus.externals.recognition.bean.RecognitionResult;

public interface CorpusService {
	public RecognitionResult match(FeatureData featureData);
	public boolean learn(String label, FeatureData featureData);
}

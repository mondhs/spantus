package org.spantus.externals.recognition.services;

import java.util.List;
import java.util.Map;
import org.spantus.core.FrameVectorValues;
import org.spantus.externals.recognition.bean.FeatureData;
import org.spantus.externals.recognition.bean.RecognitionResult;
import org.spantus.externals.recognition.bean.RecognitionResultDetails;

public interface CorpusService {
	public RecognitionResult match(Map<String, FrameVectorValues> featureDataMap);
        public List<RecognitionResultDetails> findMultipleMatch(Map<String, FrameVectorValues> featureDataMap);
        public boolean learn(String label, Map<String, FrameVectorValues> featureDataMap);
}

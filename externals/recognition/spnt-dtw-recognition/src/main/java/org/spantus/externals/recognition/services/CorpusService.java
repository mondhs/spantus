package org.spantus.externals.recognition.services;

import java.util.List;
import java.util.Map;
import org.spantus.core.IValues;
import org.spantus.externals.recognition.bean.RecognitionResult;
import org.spantus.externals.recognition.bean.RecognitionResultDetails;

public interface CorpusService {
	public RecognitionResult match(Map<String, IValues> featureDataMap);
        public List<RecognitionResultDetails> findMultipleMatch(Map<String, IValues> featureDataMap);
        public boolean learn(String label, Map<String, IValues> featureDataMap);
}

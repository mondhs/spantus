package org.spantus.externals.recognition.services;

import java.util.List;
import java.util.Map;
import javax.sound.sampled.AudioInputStream;
import org.spantus.core.IValues;
import org.spantus.externals.recognition.bean.CorpusEntry;
import org.spantus.externals.recognition.bean.RecognitionResult;
import org.spantus.externals.recognition.bean.RecognitionResultDetails;

public interface CorpusService {
	public RecognitionResult match(Map<String, IValues> featureDataMap);
        public List<RecognitionResultDetails> findMultipleMatch(Map<String, IValues> featureDataMap);
        public CorpusEntry learn(String label, Map<String, IValues> featureDataMap);
        /**
         * Same as learn {@link #learn(java.lang.String, java.util.Map)] only with
         * audio stream
         * @param label
         * @param featureDataMap
         * @param audioStream
         * @return
         */
        public CorpusEntry learn(String label, Map<String, IValues> featureDataMap, AudioInputStream audioStream);

}

package org.spantus.externals.recognition.corpus;

import java.util.Collection;
import java.util.Map;
import javax.sound.sampled.AudioInputStream;
import org.spantus.core.IValues;

import org.spantus.externals.recognition.bean.CorpusEntry;

public interface CorpusRepository {
	public Collection<CorpusEntry> findAllEntries();
	public CorpusEntry save(CorpusEntry entry);
        public CorpusEntry update(CorpusEntry entry);
        public CorpusEntry update(CorpusEntry corpusEntry, AudioInputStream audioStream);
        public CorpusEntry delete(CorpusEntry entry);
        public String findAudioFileById(Long id);
        public void flush();
         /**
         * 
         * @param label
         * @param featureDataMap
         * @return
         */
        public CorpusEntry create(String label, Map<String, IValues> featureDataMap);
}

package org.spantus.externals.recognition.corpus;

import java.util.Collection;
import javax.sound.sampled.AudioInputStream;

import org.spantus.externals.recognition.bean.CorpusEntry;

public interface CorpusRepository {
	public Collection<CorpusEntry> findAllEntries();
	public CorpusEntry save(CorpusEntry entry);
        public CorpusEntry update(CorpusEntry entry);
        public CorpusEntry update(CorpusEntry corpusEntry, AudioInputStream audioStream);
        public CorpusEntry delete(CorpusEntry entry);
        public String findAudioFileById(Long id);
        public void flush();
}

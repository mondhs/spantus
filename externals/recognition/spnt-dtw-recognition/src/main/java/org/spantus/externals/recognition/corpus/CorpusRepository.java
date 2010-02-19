package org.spantus.externals.recognition.corpus;

import java.util.List;

import org.spantus.externals.recognition.bean.CorpusEntry;

public interface CorpusRepository {
	public List<CorpusEntry> findAllEntries();
	public void save(CorpusEntry entry);
}

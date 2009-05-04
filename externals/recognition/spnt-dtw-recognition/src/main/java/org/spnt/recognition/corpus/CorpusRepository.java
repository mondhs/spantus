package org.spnt.recognition.corpus;

import java.util.List;

import org.spnt.recognition.bean.CorpusEntry;

public interface CorpusRepository {
	public List<CorpusEntry> findAllEntries();
	public void save(CorpusEntry entry);
}

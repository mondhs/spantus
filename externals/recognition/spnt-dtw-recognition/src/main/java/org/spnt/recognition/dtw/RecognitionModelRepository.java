package org.spnt.recognition.dtw;

import java.util.List;

public interface RecognitionModelRepository {
	public List<RecognitionModelEntry> findAllEntries();
	public void save(RecognitionModelEntry entry);
}

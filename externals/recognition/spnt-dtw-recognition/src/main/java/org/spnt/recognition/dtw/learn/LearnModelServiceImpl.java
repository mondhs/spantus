package org.spnt.recognition.dtw.learn;

import org.spantus.core.FrameVectorValues;
import org.spnt.recognition.dtw.RecognitionModelEntry;
import org.spnt.recognition.dtw.RecognitionModelRepositoryImpl;

public class LearnModelServiceImpl implements LearnModelService {
	RecognitionModelRepositoryImpl repository;

	public LearnModelServiceImpl() {
		repository = new RecognitionModelRepositoryImpl();
	}
	
	public RecognitionModelEntry learn(FrameVectorValues vals, String name) {
		RecognitionModelEntry entry = new RecognitionModelEntry();
		entry.setVals(vals);
		entry.setName(name);
		repository.save(entry);
		return entry;
	}
}

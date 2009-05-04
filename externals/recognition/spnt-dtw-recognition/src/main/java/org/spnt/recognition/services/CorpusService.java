package org.spnt.recognition.services;

import org.spantus.core.FrameVectorValues;
import org.spnt.recognition.bean.RecognitionResult;

public interface CorpusService {
	public RecognitionResult match(FrameVectorValues target);
	public boolean learn(String label, FrameVectorValues target);
}

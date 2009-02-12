package org.spnt.recognition.dtw.learn;

import org.spantus.core.FrameVectorValues;
import org.spnt.recognition.dtw.RecognitionModelEntry;

public interface LearnModelService {
	public RecognitionModelEntry learn(FrameVectorValues vals, String name);
}

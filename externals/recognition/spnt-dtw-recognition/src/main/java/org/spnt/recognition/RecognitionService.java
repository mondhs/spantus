package org.spnt.recognition;

import org.spantus.core.FrameValues;
import org.spantus.core.FrameVectorValues;

public interface RecognitionService {
	public String match(FrameVectorValues test);
}

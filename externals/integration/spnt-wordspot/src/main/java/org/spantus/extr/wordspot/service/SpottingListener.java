package org.spantus.extr.wordspot.service;

import java.util.List;
import org.spantus.core.beans.RecognitionResult;
import org.spantus.core.beans.SignalSegment;

public interface SpottingListener {
	public String foundSegment(String sourceId, SignalSegment newSegment, List<RecognitionResult> recognitionResults);
}

package org.spantus.extr.wordspot.service;

import java.util.List;

import org.spantus.core.beans.RecognitionResult;

public interface SegmentRecognitionThresholdService {

	Double findSegmentThreshold(String syllableName);

	boolean checkIfBellowThreshold(String syllableName,
			RecognitionResult syllableRecognitionResult);

	boolean checkIfFirstDeltaGreater(List<RecognitionResult> resultList, Double thresholdDelta);

}

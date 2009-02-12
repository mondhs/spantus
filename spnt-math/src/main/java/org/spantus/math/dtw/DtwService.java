package org.spantus.math.dtw;

import java.util.List;

public interface DtwService {
	public Float calculateDistanceVector(List<List<Float>> targetMatrix, List<List<Float>> sampleMatrix);
	public Float calculateDistance(List<Float> targetVector, List<Float> sampleVector);
	public Float calculateDistance(DtwInfo info);

}

package org.spantus.math.dtw;

import java.util.List;

public interface DtwService {
	public Double calculateDistanceVector(List<List<Double>> targetMatrix, List<List<Double>> sampleMatrix);
	public Double calculateDistance(List<Double> targetVector, List<Double> sampleVector);
        public DtwResult calculateInfoVector(List<List<Double>> targetMatrix, List<List<Double>> sampleMatrix);
        public DtwResult calculateInfo(List<Double> targetVector, List<Double> sampleVector);

}

package org.spantus.math.knn;

import java.util.ArrayList;
import java.util.List;

public class KNNServiceImpl {
	public List<List<Float>> cluster(List<List<Float>> vectors, int clusterSize) {
		List<List<Float>> centers = new ArrayList<List<Float>>();
		for (int i = 0; i < clusterSize; i++) {
			centers.add(
					vectors.get((int)(Math.random()*vectors.size()))
			);
		}
		
		return null;
	}
}

package org.spantus.math.knn;

import java.util.List;

public interface KNNService {

	public List<List<Float>> cluster(List<List<Float>> vectors,
			int clusterSize);

}
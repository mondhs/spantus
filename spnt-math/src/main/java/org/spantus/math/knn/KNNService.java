package org.spantus.math.knn;

import java.util.List;

import org.spantus.math.cluster.ClusterCollection;

public interface KNNService {

	public ClusterCollection cluster(List<List<Float>> vectors,
			int clusterSize);

}
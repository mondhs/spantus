package org.spantus.math.cluster;

import java.util.List;


public interface ClusterService {

	public ClusterCollection cluster(List<List<Double>> vectors,
			int clusterSize);

}
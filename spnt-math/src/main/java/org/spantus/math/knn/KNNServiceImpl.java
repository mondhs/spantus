package org.spantus.math.knn;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.spantus.math.cluster.ClusterCollection;

public class KNNServiceImpl implements KNNService {

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.spantus.math.knn.KNNService#cluster(java.util.List, int)
	 */
	public ClusterCollection cluster(List<List<Float>> vectors, int clusterSize) {
		if (vectors.size() <= clusterSize * 2) {
			throw new IllegalArgumentException("not enough data vectors: " + vectors.size() + " has to have twice more elements than cluster size " + clusterSize);
		}
		Map<Integer, List<List<Float>>> clusters = new HashMap<Integer, List<List<Float>>>();
		
		ClusterCollection centers = calculateCenter(vectors, clusterSize);
		

		for (int i = 0; i < clusterSize; i++) {
			clusters.put(i, new ArrayList<List<Float>>());
		}
		int vectorsSize = vectors.size();

		debug("centers {0}; ", vectors);
		for (int i = 0; i < vectorsSize; i++) {
			debug("centers {0}; ", centers);
			debug("assign points to cluster");
			for (List<Float> point : vectors) {
				Integer clusterID = centers.matchClusterClass(point);
				clusters.get(clusterID).add(point);
			}
			debug("update centers");
			for (Entry<Integer, List<List<Float>>> clusterEntry : clusters
					.entrySet()) {
				List<List<Float>> cluster = clusterEntry.getValue();
				centers.put(clusterEntry.getKey(),avg(cluster));
			}
		}
		return centers.sort();
	}

	
	
	/**
	 * print debug messsage
	 * 
	 * @param pattern
	 * @param args
	 */
	public void debug(String pattern, Object... args) {
//		 System.out.println(MessageFormat.format(pattern, args));
	}

	/**
	 * 
	 * @param vectors
	 * @param clusterSize
	 * @return
	 */
	public ClusterCollection calculateCenter(List<List<Float>> vectors,
			int clusterSize) {
		Set<Integer> indexes = new HashSet<Integer>(clusterSize);
		int vectorsSize = vectors.size();
		ClusterCollection centers = new ClusterCollection();

		while (indexes.size() < clusterSize) {
			for (int i = 0; i < vectorsSize; i++) {
				int index = (int) (Math.random() * vectorsSize);
				indexes.add(index);
				if (indexes.size() >= clusterSize) {
					break;
				}
			}
		}
		int key = 0;
		// find centers
		for (Integer index : indexes) {
			centers.put(key, vectors.get(index));
			key++;
		}
		return centers;
	}

	

	

	/**
	 * 
	 * @param vals
	 * @return
	 */
	public List<Float> avg(List<List<Float>> vals) {
		List<Float> avg = new ArrayList<Float>();
		for (List<Float> list : vals) {
			if (avg.size() == 0) {
				for (Float float1 : list) {
					avg.add(float1);
				}
				continue;
			}
			int i = 0;
			for (Float float1 : list) {
				avg.set(i, avg.get(i) + float1);
				i++;
			}
		}
		int size = vals.size();
		for (int i = 0; i < avg.size(); i++) {
			avg.set(i, avg.get(i) / size);
		}
		return avg;
	}

	
}

package org.spantus.math.cluster;

import org.spantus.math.cluster.ClusterService;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.spantus.math.MatrixUtils;
import org.spantus.math.cluster.ClusterCollection;

public class KNNServiceImpl implements ClusterService {

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.spantus.math.knn.ClusterService#cluster(java.util.List, int)
	 */
	public ClusterCollection cluster(List<List<Double>> vectors, int clusterSize) {
		if (vectors.size() < clusterSize * 2) {
			throw new IllegalArgumentException("not enough data vectors: " + vectors.size() + " has to have twice more elements than cluster size " + clusterSize);
		}
		Map<Integer, List<List<Double>>> clusters = new HashMap<Integer, List<List<Double>>>();
		
		ClusterCollection centers = calculateInitailCenter(vectors, clusterSize);
		

		for (int i = 0; i < clusterSize; i++) {
			clusters.put(i, new ArrayList<List<Double>>());
		}
		int vectorsSize = vectors.size();

		debug("centers {0}; ", vectors);
		for (int i = 0; i < vectorsSize; i++) {
			debug("centers {0}; ", centers);
			debug("assign points to cluster");
			for (List<Double> point : vectors) {
				Integer clusterID = centers.matchClusterClass(point);
				clusters.get(clusterID).add(point);
			}
			debug("update centers");
			for (Entry<Integer, List<List<Double>>> clusterEntry : clusters
					.entrySet()) {
				List<List<Double>> cluster = clusterEntry.getValue();
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
	protected void debug(String pattern, Object... args) {
//		 System.out.println(MessageFormat.format(pattern, args));
	}
	/**
	 * 
	 * @param vectors
	 * @return
	 */
	protected ClusterCollection calculateNormalization(List<List<Double>> vectors){
		ClusterCollection centers = new ClusterCollection();
		int vectorLength = vectors.get(0).size();

		List<Double> min = MatrixUtils.generareVector(Double.MAX_VALUE, vectorLength);
		List<Double> max = MatrixUtils.generareVector(-Double.MAX_VALUE, vectorLength);
		for (List<Double> vector : vectors) {
			min = minVector(min, vector);
			max = maxVector(max, vector);
		}
		centers.setMaxVector(max);
		centers.setMinVector(min);
		List<Double> delta = new ArrayList<Double>();
		Iterator<Double> minIter = min.iterator();
		Iterator<Double> maxIter = max.iterator();
		while (minIter.hasNext()) {
			Double minVal = (Double) minIter.next();
			Double maxVal = (Double) maxIter.next();
			delta.add(maxVal-minVal);
		}
		
		centers.setDelta(delta);
		return centers;
	}
	
	/**
	 * 
	 * @param vectors
	 * @param clusterSize
	 * @return
	 */
	protected ClusterCollection calculateInitailCenter(List<List<Double>> vectors,
			int clusterSize) {
//		Set<Integer> indexes = new HashSet<Integer>(clusterSize);
		int vectorLength = vectors.get(0).size();
		
		ClusterCollection centers = calculateNormalization(vectors);
		
		for (int i = 0; i < clusterSize; i++) {
			Iterator<Double> minIterator = centers.getMinVector().iterator();
			Iterator<Double> deltaIterator = centers.getDelta().iterator();
			List<Double> center = new ArrayList<Double>();
			while (minIterator.hasNext()) {
				Double delta = deltaIterator.next();
				Double min = minIterator.next();
				Double segmentMidle = delta/(2*vectorLength);
				center.add((segmentMidle*i)+min);
			}
			centers.put(i, center);
		}
		
//		while (indexes.size() < clusterSize) {
//			for (int i = 0; i < vectorsSize; i++) {
//				int index = (int) (Math.random() * vectorsSize);
//				indexes.add(index);
//				if (indexes.size() >= clusterSize) {
//					break;
//				}
//			}
//		}
		
//		int key = 0;
		// find centers
//		for (Integer index : indexes) {
//			centers.put(key, vectors.get(index));
//			key++;
//		}
		return centers;
	}

	

	

	/**
	 * 
	 * @param vals
	 * @return
	 */
	protected List<Double> avg(List<List<Double>> vals) {
		List<Double> avg = new ArrayList<Double>();
		for (List<Double> list : vals) {
			if (avg.size() == 0) {
				for (Double Double1 : list) {
					avg.add(Double1);
				}
				continue;
			}
			int i = 0;
			for (Double float1 : list) {
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
	
	protected List<Double> minVector(List<Double> minVector, List<Double> point) {
		List<Double> vector = new ArrayList<Double>();
		Iterator<Double> minIter = minVector.iterator();
		Iterator<Double> pointIter = point.iterator();
		while (pointIter.hasNext()) {
			Double a = (Double) minIter.next();
			Double b = (Double) pointIter.next();
			vector.add(Math.min(a, b));
		}
		return vector;
	}
	protected List<Double> maxVector(List<Double> minVector, List<Double> point) {
		List<Double> vector = new ArrayList<Double>();
		Iterator<Double> minIter = minVector.iterator();
		Iterator<Double> pointIter = point.iterator();
		while (pointIter.hasNext()) {
			Double a = (Double) minIter.next();
			Double b = (Double) pointIter.next();
			vector.add(Math.max(a, b));
		}
		return vector;
	}

//	protected List<List<Float>> normalize(List<List<Float>> vectors, List<Float> min, List<Float> max){
//		List<List<Float>> normalized = new ArrayList<List<Float>>();
//		List<Float> delta = new ArrayList<Float>();
//		
//		Iterator<Float> minIter = min.iterator();
//		Iterator<Float> maxIter = max.iterator();
//		while (minIter.hasNext()) {
//			Float minVal = (Float) minIter.next();
//			Float maxVal = (Float) maxIter.next();
//			delta.add(maxVal-minVal);
//		}
//		
//		for (List<Float> point : vectors) {
//			minIter = min.iterator();
//			Iterator<Float> deltaIter = delta.iterator();
//			Iterator<Float> pointIter = point.iterator();
//			List<Float> normalizedPoint = new ArrayList<Float>();
//			while (minIter.hasNext()) {
//				Float floatValue = pointIter.next();
//				Float minVal = (Float) minIter.next();
//				Float deltaVal = (Float) deltaIter.next();
//				floatValue = (floatValue-minVal)/deltaVal;
//				normalizedPoint.add(floatValue);
//			}
//			normalized.add(normalizedPoint);
//			
//		}
//		return normalized;
//	}	
}

package org.spantus.math.knn;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

public class KNNServiceImpl {

	public List<List<Float>> cluster(List<List<Float>> vectors, int clusterSize) {
		if(vectors.size()<= clusterSize){
			throw new IllegalArgumentException("not enough data");
		}
		Map<Integer, List<List<Float>>> clusters = new HashMap<Integer, List<List<Float>>>();
		List<List<Float>> centers = calculateCenter(vectors, clusterSize);
		
		for (int i = 0; i < clusterSize; i++) {
			clusters.put(i, new ArrayList<List<Float>>());
		}
		
		
		debug("centers {0}; ", vectors);
		for (int i = 0; i < 10; i++) {
			debug("centers {0}; ", centers);

			// 
			for (List<Float> point : vectors) {
				List<Double> distances = new ArrayList<Double>();
				for (List<Float> center : centers) {
					distances.add(calculateDistance(center, point));
				}
				int minArg = minArg(distances);
				clusters.get(minArg).add(point);
			}
			for (Entry<Integer, List<List<Float>>> clusterEntry : clusters
					.entrySet()) {
				int clusterID = clusterEntry.getKey();
				List<List<Float>> cluster = clusterEntry.getValue();
				centers.set(clusterID, avg(cluster));
			}

		}
		return centers;
	}
	/**
	 * print debug messsage
	 * @param pattern
	 * @param args
	 */
	public void debug(String pattern, Object... args) {
		System.out.println(MessageFormat.format(pattern, args));
	}
	/**
	 * 
	 * @param vectors
	 * @param clusterSize
	 * @return
	 */
	public List<List<Float>> calculateCenter(List<List<Float>> vectors, int clusterSize) {
		Set<Integer> indexes = new HashSet<Integer>(clusterSize);
		List<List<Float>> centers = new ArrayList<List<Float>>();
		int vectorsSize = vectors.size();
		
		for (int i = 0; i < vectorsSize; i++) {
			int index = (int) (Math.random() * vectorsSize);	
			indexes.add(index);
			if(indexes.size()>=clusterSize){
				break;
			}
		}
		// find centers
		for (Integer index : indexes) {
			centers.add(vectors.get(index));	
		}
		return centers;
	}
	
	/**
	 * 
	 * @param center
	 * @param point
	 * @return
	 */
	public Double calculateDistance(List<Float> center, List<Float> point) {
		if (center.size() != point.size()) {
			throw new IllegalArgumentException("sizes not matches");
		}
		Iterator<Float> centerIter = center.iterator();
		Iterator<Float> pointIter = point.iterator();
		Double distance = 0D;
		while (centerIter.hasNext()) {
			Float localDistance = centerIter.next() - pointIter.next();
			distance += localDistance * localDistance;
		}
		distance = Math.sqrt(distance);
		return distance;
	}

	/**
	 * 
	 * @param distances
	 * @return
	 */
	public int minArg(List<Double> distances) {
		int i = 0;
		Double min = Double.MAX_VALUE;
		int minArg = 0;
		for (Double double1 : distances) {
			if (min > double1) {
				min = double1;
				minArg = i;
			}
			i++;
		}
		return minArg;
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

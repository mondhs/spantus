package org.spantus.math.cluster;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class ClusterCollection extends HashMap<Integer, List<Float>> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private List<Float> minVector; 
	private List<Float> maxVector; 
	private List<Float> delta; 

	/**
	 * 
	 * @param center
	 * @param point
	 * @return
	 */
	public Double calculateDistance(List<Float> center, List<Float> point) {
		if(center.size()==0){
			return Double.MAX_VALUE;
		}
		if (center.size() != point.size()) {
			throw new IllegalArgumentException("to calculate distances vector sizes has to match matches" + center +"<>" + point);
		}
		
		Iterator<Float> minIterator = getMinVector().iterator();
		Iterator<Float> deltaIterator = getDelta().iterator();
		Iterator<Float> centerIter = center.iterator();
		Iterator<Float> pointIter = point.iterator();
		Double distance = 0D;
		while (centerIter.hasNext()) {
			Float val = pointIter.next();
			Float centerVal = centerIter.next();
			Float min = minIterator.next();
			Float delta = deltaIterator.next();
			Float p1 = (val - min )/delta;
			Float p2 = (centerVal - min )/delta;
			Float localDistance = p1-p2;
			distance += localDistance * localDistance;
		}
		distance = Math.sqrt(distance);
		return distance;
	}
	/**
	 * 
	 * @param vector
	 * @return
	 */
	public Integer matchClusterClass(List<Float> vector){
		
		return matchClusterEntry(vector).getKey();
	}
	/**
	 * 
	 * @param vector
	 * @return
	 */
	public Entry<Integer, List<Float>> matchClusterEntry(List<Float> vector){
		Map<Integer, Double> distances = new HashMap<Integer, Double>();
		Map<Integer, Entry<Integer, List<Float>>> entries = new HashMap<Integer, Entry<Integer,List<Float>>>();
		for (Entry<Integer, List<Float>> center : this.entrySet()) {
			entries.put(center.getKey(), center);
			Double distance = calculateDistance(center.getValue(), vector);
//			System.out.println(MessageFormat.format("distance:{0}; center:{1}; vector: {2}", ""+distance, center, vector));
			distances.put(center.getKey(), distance);
		}
		return entries.get(minArg(distances));
	}
	/**
	 * 
	 * @param vectorArr
	 * @return
	 */
	public Integer matchClusterClass(Float... vectorArr){
		List<Float> vector = new ArrayList<Float>(Arrays.asList(vectorArr)); 
		return matchClusterClass(vector);
	}
	/**
	 * 
	 * @return
	 */
	public ClusterCollection sort(){
		ClusterCollection centers = this;
		List<List<Float>> values = new ArrayList<List<Float>>(centers.values());
		Collections.sort(values, new ListComparator());
		ClusterCollection sorted = new ClusterCollection();
		sorted.setMinVector(this.getMinVector());
		sorted.setMaxVector(this.getMaxVector());
		sorted.setDelta(this.getDelta());
		int i = 0;
		for (List<Float> value : values) {
			sorted.put(i, value);
			i++;
		}
		return sorted;
	}
	
	/**
	 * 
	 * @param distances
	 * @return
	 */
	public Integer minArg(Map<Integer, Double> distances) {
		Double min = Double.MAX_VALUE;
		Integer minArg = 0;
		for (Entry<Integer, Double> entry : distances.entrySet()) {
			Double double1 = entry.getValue();
			if (min > double1) {
				min = double1;
				minArg = entry.getKey();
			}
		}
		return minArg;
	}
	public List<Float> getMinVector() {
		return minVector;
	}
	public void setMinVector(List<Float> minVector) {
		this.minVector = minVector;
	}
	public List<Float> getMaxVector() {
		return maxVector;
	}
	public void setMaxVector(List<Float> maxVector) {
		this.maxVector = maxVector;
	}
	public List<Float> getDelta() {
		return delta;
	}
	public void setDelta(List<Float> delta) {
		this.delta = delta;
	}

}

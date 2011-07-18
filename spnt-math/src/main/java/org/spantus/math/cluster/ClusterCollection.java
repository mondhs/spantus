package org.spantus.math.cluster;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class ClusterCollection extends HashMap<Integer, List<Double>> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private List<Double> minVector; 
	private List<Double> maxVector; 
	private List<Double> delta; 

	/**
	 * 
	 * @param center
	 * @param point
	 * @return
	 */
	public Double calculateDistance(List<Double> center, List<Double> point) {
		if(center.size()==0){
			return Double.MAX_VALUE;
		}
		if (center.size() != point.size()) {
			throw new IllegalArgumentException("to calculate distances vector sizes has to match matches" + center +"<>" + point);
		}
		
		Iterator<Double> minIterator = getMinVector().iterator();
		Iterator<Double> deltaIterator = getDelta().iterator();
		Iterator<Double> centerIter = center.iterator();
		Iterator<Double> pointIter = point.iterator();
		Double distance = 0D;
		while (centerIter.hasNext()) {
			Double val = pointIter.next();
			Double centerVal = centerIter.next();
			Double min = minIterator.next();
			Double delta = deltaIterator.next();
			Double p1 = (val - min )/delta;
			Double p2 = (centerVal - min )/delta;
			Double localDistance = p1-p2;
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
	public Integer matchClusterClass(List<Double> vector){
		
		return matchClusterEntry(vector).getKey();
	}
	/**
	 * 
	 * @param vector
	 * @return
	 */
	public Entry<Integer, List<Double>> matchClusterEntry(List<Double> vector){
		Map<Integer, Double> distances = new HashMap<Integer, Double>();
		Map<Integer, Entry<Integer, List<Double>>> entries = new HashMap<Integer, Entry<Integer,List<Double>>>();
		for (Entry<Integer, List<Double>> center : this.entrySet()) {
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
	public Integer matchClusterClass(Double... vectorArr){
		List<Double> vector = new ArrayList<Double>(Arrays.asList(vectorArr)); 
		return matchClusterClass(vector);
	}
	/**
	 * 
	 * @return
	 */
	public ClusterCollection sort(){
		ClusterCollection centers = this;
		List<List<Double>> values = new ArrayList<List<Double>>(centers.values());
		Collections.sort(values, new ListComparator());
		ClusterCollection sorted = new ClusterCollection();
		sorted.setMinVector(this.getMinVector());
		sorted.setMaxVector(this.getMaxVector());
		sorted.setDelta(this.getDelta());
		int i = 0;
		for (List<Double> value : values) {
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
	public List<Double> getMinVector() {
		return minVector;
	}
	public void setMinVector(List<Double> minVector) {
		this.minVector = minVector;
	}
	public List<Double> getMaxVector() {
		return maxVector;
	}
	public void setMaxVector(List<Double> maxVector) {
		this.maxVector = maxVector;
	}
	public List<Double> getDelta() {
		return delta;
	}
	public void setDelta(List<Double> delta) {
		this.delta = delta;
	}

}

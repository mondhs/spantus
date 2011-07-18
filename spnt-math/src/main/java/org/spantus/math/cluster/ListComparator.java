package org.spantus.math.cluster;

import java.util.Comparator;
import java.util.List;

/**
 * 
 * @author Mindaugas Greibus
 * 
 */
public class ListComparator implements Comparator<List<Double>> {
	public ListComparator() {
	}

	public int compare(List<Double> centerVector, List<Double> pointVector) {
		Double center = criteria(centerVector);
		Double point = criteria(pointVector);
		return center.compareTo(point);
	}
	protected Double criteria(List<Double> vector){
		if(vector == null || vector.size() == 0){
			return Double.MAX_VALUE;
		}
		Double criteria = 0D;
		for (Double float1 : vector) {
			criteria += float1*float1;
		}
		return criteria;
	}
}

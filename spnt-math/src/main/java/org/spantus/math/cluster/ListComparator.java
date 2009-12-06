package org.spantus.math.cluster;

import java.util.Comparator;
import java.util.List;

/**
 * 
 * @author Mindaugas Greibus
 * 
 */
public class ListComparator implements Comparator<List<Float>> {
	public ListComparator() {
	}

	public int compare(List<Float> centerVector, List<Float> pointVector) {
		Double center = criteria(centerVector);
		Double point = criteria(pointVector);
		return center.compareTo(point);
	}
	protected Double criteria(List<Float> vector){
		if(vector == null || vector.size() == 0){
			return Double.MAX_VALUE;
		}
		Double criteria = 0D;
		for (Float float1 : vector) {
			criteria += float1*float1;
		}
		return criteria;
	}
}

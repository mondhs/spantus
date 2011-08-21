package org.spantus.math.services.impl;

import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.spantus.math.services.ConvexHullService;

import scikit.util.Pair;

/**
 * patched version:
 * http://en.wikibooks.org/wiki/Algorithm_Implementation/Geometry
 * /Convex_hull/Monotone_chain
 * 
 * 
 */
public class ConvexHullServiceImpl implements ConvexHullService {

	/**
	 * executes the brute-force algorithm for finding the convex hull of its
	 * input points. The output polygon is an array of vertices in order. The
	 * array size of the polygon may be larger than the number of vertices; if
	 * this the case, the index after the last vertex stores a null Object.
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public Map<Integer, Double> calculateConvexHull(List<Double> signal) {
		Map<Integer, Double> result = new TreeMap<Integer, Double>();
		Pair<Integer, Double>[] H = new Pair[signal.size()];

		int n = signal.size(), k = 0;
		// ListIterator<Double> signalIterator = signal.listIterator();
		// Build lower hull

		for (ListIterator<Double> iterator = signal.listIterator(); iterator
				.hasNext();) {
			Pair<Integer, Double> pi = Pair.newPair(iterator.nextIndex(),
					iterator.next());
			while (k >= 2) {
				Pair<Integer, Double> hk2 = H[k - 2];
				Pair<Integer, Double> hk1 = H[k - 1];
				if (cross(hk2, hk1, pi) <= 0) {
					break;
				}
				k--;
			}
			H[k++] = pi;
		}

		ListIterator<Double> signalIterator = signal.listIterator(n - 1);
		// Build upper hull
		for (int i = n - 2, t = k + 1; i >= 0; i--) {
			Pair<Integer, Double> pi = Pair.newPair(i,
					signalIterator.previous());
			while (k >= t) {
				Pair<Integer, Double> hk2 = H[k - 2];
				Pair<Integer, Double> hk1 = H[k - 1];
				if (cross(hk2, hk1, pi) <= 0) {
					break;
				}
				k--;
			}
			H[k++] = pi;
		}
		int lastIndex = 0;
		for (Pair<Integer, Double> pair : H) {
			if (pair == null) {
				break;
			}
			if (pair.fst() < lastIndex) {
				break;
			}
			result.put(pair.fst(), pair.snd());
			lastIndex = pair.fst();
		}
		return result;
	}

	public static Double cross(Pair<Integer, Double> vertexZ1,
			Pair<Integer, Double> vertex, Pair<Integer, Double> iVertex) {
		return (vertex.fst() - vertexZ1.fst())
				* (iVertex.snd() - vertexZ1.snd())
				- (vertex.snd() - vertexZ1.snd())
				* (iVertex.fst() - vertexZ1.fst());
	}
	/**
	 * 
	 */
	public List<Double> calculateConvexHullTreshold(List<Double> signalDouble) {
		Map<Integer, Double> keyPoits = calculateConvexHull(signalDouble);
		List<Double> result = new LinkedList<Double>();

//		HashMap<Integer, Pair<Double, Double>> map = new HashMap<Integer, Pair<Double, Double>>();
//		ValueComparator bvc = new ValueComparator(map);
//		TreeMap<String, Double> sorted_map = new TreeMap(bvc);

		Pair<Integer,Double> maxDeltaPair = null;
		maxDeltaPair =	findMaxDelta(signalDouble, keyPoits,-Double.MAX_VALUE);
		keyPoits.put(maxDeltaPair.fst(), maxDeltaPair.snd());
		
		 Pair<Integer, Double> firstPair = maxDeltaPair;
		
		 List<Double> subSignal = null;
		 Map<Integer, Double> subKeyPoits = null;
		 
		 //left
		 for (int i = 0; i < 10; i++) {
			 if(maxDeltaPair.fst()<10){
				 break;
			 }
				 subSignal = signalDouble.subList(0, maxDeltaPair.fst());
				subKeyPoits = calculateConvexHull(subSignal);
				maxDeltaPair =	findMaxDelta(subSignal, subKeyPoits, 0.0);
				if(maxDeltaPair == null){
					break;
				}
				keyPoits.put(maxDeltaPair.fst(), maxDeltaPair.snd());
		}
		 
		 for (int i = 0; i < 10; i++) {
			 if(signalDouble.size()-firstPair.fst()<10){
				 break;
			 }
				subSignal = signalDouble.subList(firstPair.fst(),signalDouble.size()-1);
				subKeyPoits = calculateConvexHull(subSignal);
				maxDeltaPair =	findMaxDelta(subSignal, subKeyPoits, 0.0);
				if(maxDeltaPair == null){
					break;
				}
				keyPoits.put(firstPair.fst() + maxDeltaPair.fst(), maxDeltaPair.snd());
				firstPair = Pair.newPair(firstPair.fst() + maxDeltaPair.fst(), maxDeltaPair.snd());
		}
		 


		Entry<Integer, Double> prev = null;
		Double lastVal = 0D;
		for (Entry<Integer, Double> pair : keyPoits.entrySet()) {
			if (prev == null) {
				prev = pair;
				continue;
			}
			Double deltaY = pair.getValue() - prev.getValue();
			int deltaX = pair.getKey() - prev.getKey();
			Double slope = deltaY / deltaX;
			Double yIntersection = pair.getValue() - slope * pair.getKey();

			for (int i = prev.getKey(); i < pair.getKey(); i++) {
				lastVal = slope * i + yIntersection;
				result.add(lastVal);
			}
			prev = pair;
		}

		result.add(lastVal);
		return result;
	}

	private Pair<Integer, Double> findMaxDelta(List<Double> signalDouble, Map<Integer, Double> keyPoits, double minDelta) {
		Entry<Integer, Double> prev = null;
		Double lastVal = 0D;
		Iterator<Double> singalIter = signalDouble.iterator();
		Double maxDelta = null;
		Double valOnMax = null;
		int indexOnMax = 0;
		for (Entry<Integer, Double> pair : keyPoits.entrySet()) {
			if (prev == null) {
				prev = pair;
				continue;
			}
			Double deltaY = pair.getValue() - prev.getValue();
			int deltaX = pair.getKey() - prev.getKey();
			Double slope = deltaY / deltaX;
			Double yIntersection = pair.getValue() - slope * pair.getKey();

			for (int i = prev.getKey(); i < pair.getKey(); i++) {
				lastVal = slope * i + yIntersection;
				Double currentVal = singalIter.next();
//				map.put(i, Pair.newPair(first, second))
				if (maxDelta == null) {
					maxDelta = lastVal - currentVal;
					valOnMax = currentVal;
					indexOnMax = i;
				} else {
					if (lastVal - currentVal > maxDelta) {
						maxDelta = lastVal - currentVal;
						valOnMax = currentVal;
						indexOnMax = i;
					}
				}
			}
			prev = pair;
		}
		System.out.printf("[calculateConvexHullTreshold] %s->%s [%s]\n", indexOnMax, valOnMax, maxDelta);
		if(maxDelta<minDelta){
			return null;
		}
		return Pair.newPair(indexOnMax, valOnMax);
	}

	class ValueComparator implements Comparator<Integer> {

		Map<Integer, Pair<Double, Double>> base;

		public ValueComparator(Map<Integer, Pair<Double, Double>> base) {
			this.base = base;
		}

		public int compare(Integer a, Integer b) {
			return base.get(a).snd().compareTo(base.get(b).snd());
		}
	}

}

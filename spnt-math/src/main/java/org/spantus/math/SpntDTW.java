package org.spantus.math;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.spantus.math.dtw.DtwInfo;
import org.spantus.math.dtw.DtwResult;
import org.spantus.math.dtw.DtwTypeI;
import org.spantus.math.dtw.DtwTypeII;
import org.spantus.math.dtw.DtwTypeIII;

public class SpntDTW {

	public static int MAX_DISTANCE_COEF = 4;

	public static Double distance(Double t, Double r) {
		return Math.sqrt(Math.pow((t - r), 2));
	}
	public static Double distanceEuclidian(List<Double> ts, List<Double> rs) {
		double sum = 0.0;
		Iterator<Double> ti = ts.iterator();
		Iterator<Double> ri = rs.iterator();
		while (ri.hasNext()) {
			Double r = ri.next();
			Double t = ti.next();
			sum += Math.pow(t-r,2);
		}
		
		return  Math.sqrt(sum);
	}

	
	public static List<List<Double>> distanceVectorMatrix(List<List<Double>> target,
			List<List<Double>> sample, Integer maxDisanceCoef) {
		List<List<Double>> matrix = new ArrayList<List<Double>>();
		int j = 0, i = 0;
		List<List<Double>> _target = sample.size() > target.size() ? sample : target;
		List<List<Double>> _sample = sample.size() > target.size() ? target : sample;
		for (List<Double> sampleVector : _sample) {
			j++;
			List<Double> row = new ArrayList<Double>();
			for (List<Double> targetVector : _target) {
				i++;
				if (isInLegalRange(i, j, target.size(), sample.size(), maxDisanceCoef)) {
					row.add(distanceEuclidian(targetVector, sampleVector));
				} else {
					row.add(Double.NaN);
				}

			}
			matrix.add(row);
			i = 0;
		}
		return matrix;
	}
	
	public static List<List<Double>> distanceMatrix(List<Double> target,
			List<Double> sample, Integer maxDisanceCoef) {
		List<List<Double>> matrix = new ArrayList<List<Double>>();
		int j = 0, i = 0;
		List<Double> _target = sample.size() > target.size() ? sample : target;
		List<Double> _sample = sample.size() > target.size() ? target : sample;
		for (Double float1 : _sample) {
			j++;
			List<Double> row = new ArrayList<Double>();
			for (Double float2 : _target) {
				i++;
				if (isInLegalRange(i, j, target.size(), sample.size(), maxDisanceCoef)) {
					row.add(distance(float1, float2));
				} else {
					row.add(Double.NaN);
				}

			}
			matrix.add(row);
			i = 0;
		}
		return matrix;
	}

	public static DtwResult dtwRecusion(DtwInfo info) {
		switch(info.getType()){
		case typeI:
			return new DtwTypeI().dtwRecusion(info.getDistanceMatrix().get(0)
					.size() - 1, info.getDistanceMatrix().size() - 1, info);
		case typeII:
			return new DtwTypeII().dtwRecusion(info.getDistanceMatrix().get(0)
				.size() - 1, info.getDistanceMatrix().size() - 1, info);
		case typeIII:
			return new DtwTypeIII().dtwRecusion(info.getDistanceMatrix().get(0)
				.size() - 1, info.getDistanceMatrix().size() - 1, info);
		default:
			throw new RuntimeException("not impls");
		}
	}
	
	public static DtwInfo createDtwVectorInfo(List<List<Double>> target, List<List<Double>> sample){
		List<List<Double>> distanceMatrix = distanceVectorMatrix(target, sample, null);
		DtwInfo info = new DtwInfo();
		info.setDistanceMatrix(distanceMatrix);
		return info;
	}


	public static DtwInfo createDtwInfo(List<Double> target, List<Double> sample){
		return createDtwInfo(target, sample, null);
	}
	
	public static DtwInfo createDtwInfo(List<Double> target, List<Double> sample, Integer maxDisanceCoef){
		List<List<Double>> distanceMatrix = distanceMatrix(target, sample, maxDisanceCoef);
		DtwInfo info = new DtwInfo();
		info.setDistanceMatrix(distanceMatrix);
//		debug("\n" + logMatrix(distanceMatrix));
		return info;
	}

	/**
	 * 
	 * @param target
	 * @param sample
	 * @return
	 */
	public static Double estimate(DtwInfo info) {
		DtwResult result = null;
		result = dtwRecusion(info);
		return result.getResult();
	}
	
	public static Double estimate(List<Double> target, List<Double> sample) {
		return estimate(createDtwInfo(target, sample));
	}


	

	protected static boolean isInLegalRange(int x, int y, int sizeX, int sizeY, Integer maxDisanceCoef) {
		// int r = sizeI-(int)((float)sizeI*.6f);
		if(maxDisanceCoef == null){
			maxDisanceCoef = MAX_DISTANCE_COEF;
		}
		int r = (sizeX + sizeY) / maxDisanceCoef;
		if ((y == sizeY && x == sizeX) || (x == 0 && y == 0)) {
			return true;
		}
		// if(Math.abs(x*.5-y)>r){
		// return false;
		// }
		return Math.abs(x - y) < r;
		// return j-r<i && (i+1)<j+r;
		// return j-r<(i) && (i+2)<j+r;
	}

}

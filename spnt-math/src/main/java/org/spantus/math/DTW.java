package org.spantus.math;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.spantus.math.dtw.DtwInfo;
import org.spantus.math.dtw.DtwResult;
import org.spantus.math.dtw.DtwTypeI;
import org.spantus.math.dtw.DtwTypeII;
import org.spantus.math.dtw.DtwTypeIII;

public class DTW {

	public static int MAX_DISTANCE_COEF = 4;

	public static Float distance(Float t, Float r) {
		return (float) Math.sqrt(Math.pow((t - r), 2));
	}
	public static Float distanceEuclidian(List<Float> ts, List<Float> rs) {
		double sum = 0.0;
		Iterator<Float> ti = ts.iterator();
		Iterator<Float> ri = rs.iterator();
		while (ri.hasNext()) {
			Float r = ri.next();
			Float t = ti.next();
			sum += Math.pow(t-r,2);
		}
		
		return (float) Math.sqrt(sum);
	}

	
	public static List<List<Float>> distanceVectorMatrix(List<List<Float>> target,
			List<List<Float>> sample, Integer maxDisanceCoef) {
		List<List<Float>> matrix = new ArrayList<List<Float>>();
		int j = 0, i = 0;
		List<List<Float>> _target = sample.size() > target.size() ? sample : target;
		List<List<Float>> _sample = sample.size() > target.size() ? target : sample;
		for (List<Float> sampleVector : _sample) {
			j++;
			List<Float> row = new ArrayList<Float>();
			for (List<Float> targetVector : _target) {
				i++;
				if (isInLegalRange(i, j, target.size(), sample.size(), maxDisanceCoef)) {
					row.add(distanceEuclidian(targetVector, sampleVector));
				} else {
					row.add(Float.NaN);
				}

			}
			matrix.add(row);
			i = 0;
		}
		return matrix;
	}
	
	public static List<List<Float>> distanceMatrix(List<Float> target,
			List<Float> sample, Integer maxDisanceCoef) {
		List<List<Float>> matrix = new ArrayList<List<Float>>();
		int j = 0, i = 0;
		List<Float> _target = sample.size() > target.size() ? sample : target;
		List<Float> _sample = sample.size() > target.size() ? target : sample;
		for (Float float1 : _sample) {
			j++;
			List<Float> row = new ArrayList<Float>();
			for (Float float2 : _target) {
				i++;
				if (isInLegalRange(i, j, target.size(), sample.size(), maxDisanceCoef)) {
					row.add(distance(float1, float2));
				} else {
					row.add(Float.NaN);
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
	
	public static DtwInfo createDtwVectorInfo(List<List<Float>> target, List<List<Float>> sample){
		List<List<Float>> distanceMatrix = distanceVectorMatrix(target, sample, null);
		DtwInfo info = new DtwInfo();
		info.setDistanceMatrix(distanceMatrix);
		return info;
	}


	public static DtwInfo createDtwInfo(List<Float> target, List<Float> sample){
		return createDtwInfo(target, sample, null);
	}
	
	public static DtwInfo createDtwInfo(List<Float> target, List<Float> sample, Integer maxDisanceCoef){
		List<List<Float>> distanceMatrix = distanceMatrix(target, sample, maxDisanceCoef);
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
	public static Float estimate(DtwInfo info) {
		DtwResult result = null;
		result = dtwRecusion(info);
		return result.getResult();
	}
	
	public static Float estimate(List<Float> target, List<Float> sample) {
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

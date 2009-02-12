package org.spnt.recognition.dtw;

import java.util.ArrayList;
import java.util.List;

import org.spantus.core.FrameVectorValues;
import org.spantus.math.DTW;
import org.spantus.math.dtw.DtwInfo;

public class DTWUtils extends DTW {
	public static List<List<Float>> distanceVectorMatrix(FrameVectorValues target,
			FrameVectorValues sample, Integer maxDisanceCoef) {
		List<List<Float>> matrix = new ArrayList<List<Float>>();
		int j = 0, i = 0;
		FrameVectorValues _target = sample.size() > target.size() ? sample : target;
		FrameVectorValues _sample = sample.size() > target.size() ? target : sample;
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
	
	public static DtwInfo createDtwInfo(FrameVectorValues target,
			FrameVectorValues sample){
		DtwInfo dtw = new DtwInfo();
		dtw.setDistanceMatrix(distanceVectorMatrix(target, sample, null));
		return dtw;
		
	}
}

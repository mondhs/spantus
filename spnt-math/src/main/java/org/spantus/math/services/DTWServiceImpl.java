package org.spantus.math.services;

import java.util.List;

import org.spantus.math.DTW;

public class DTWServiceImpl implements DTWService {

	public Float calculateDTWPath(List<Float> target, List<Float> sample) {
		return DTW.estimate(target, sample);
	}

}

package org.spantus.math.services;

import java.util.List;

import org.spantus.math.DTW;

public class DTWServiceImpl implements DTWService {

	public Double calculateDTWPath(List<Double> target, List<Double> sample) {
		return DTW.estimate(target, sample);
	}

}

package org.spantus.math.services;

import java.util.List;

import org.spantus.math.SpntDTW;

public class DTWServiceImpl implements DTWService {

	public Double calculateDTWPath(List<Double> target, List<Double> sample) {
		return SpntDTW.estimate(target, sample);
	}

}

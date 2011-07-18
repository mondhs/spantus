package org.spantus.math.services;

import java.util.List;

public interface DTWService {
	public Double calculateDTWPath(List<Double> target, List<Double> sample);
}

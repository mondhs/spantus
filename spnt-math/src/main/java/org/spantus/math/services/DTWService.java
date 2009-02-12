package org.spantus.math.services;

import java.util.List;

public interface DTWService {
	public Float calculateDTWPath(List<Float> target, List<Float> sample);
}

package org.spantus.math.services;

import java.util.List;

public interface LPCService {
	public List<Float> calculateLPC(List<Float> x, int order);
}

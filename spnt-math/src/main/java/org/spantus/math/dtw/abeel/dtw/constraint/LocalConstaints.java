package org.spantus.math.dtw.abeel.dtw.constraint;

import java.util.Map;

import scikit.util.Pair;

public interface LocalConstaints {
	public Map<Pair<Integer, Integer>,Double> createLocalConstraints(int i, int j);
}

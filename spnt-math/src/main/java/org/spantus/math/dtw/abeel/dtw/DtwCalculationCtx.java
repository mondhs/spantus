package org.spantus.math.dtw.abeel.dtw;

import java.util.Map;

import scikit.util.Pair;

public class DtwCalculationCtx {
	Map<Pair<Integer, Integer>, Double> localValues;
	Pair<Integer, Integer> minPair;
	
	public DtwCalculationCtx() {
	}
	
	public DtwCalculationCtx(Map<Pair<Integer, Integer>, Double> localValues,
			Pair<Integer, Integer> minPair) {
		super();
		this.localValues = localValues;
		this.minPair = minPair;
	}
	
	public Map<Pair<Integer, Integer>, Double> getLocalValues() {
		return localValues;
	}
	public void setLocalValues(Map<Pair<Integer, Integer>, Double> localValues) {
		this.localValues = localValues;
	}
	public Pair<Integer, Integer> getMinPair() {
		return minPair;
	}
	public void setMinPair(Pair<Integer, Integer> minPair) {
		this.minPair = minPair;
	}
	
}

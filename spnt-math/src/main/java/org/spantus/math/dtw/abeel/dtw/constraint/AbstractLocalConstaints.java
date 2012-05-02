package org.spantus.math.dtw.abeel.dtw.constraint;

import java.util.LinkedHashMap;
import java.util.Map;

import scikit.util.Pair;
/**
 * 
 * @author mondhs
 * @since 0.3
 *
 */
public abstract class AbstractLocalConstaints implements LocalConstaints {

	public Map<Pair<Integer, Integer>,Double> createLocalConstraints(int i, int j) {
		Map<Pair<Integer, Integer>,Double> constaints = new LinkedHashMap<Pair<Integer,Integer>, Double>();
    	
		if(i == 0 && j == 0){
			return null;
		}
    	addConstraints(constaints, i, j);
    	
    	return constaints;
	}
	
	public abstract void addConstraints(Map<Pair<Integer, Integer>, Double> constaints, int i, int j);

    protected void safeAdd(Map<Pair<Integer, Integer>, Double> constaints,
    		Integer newI, Integer newJ) {
    	newI = Math.max(newI, 0);
    	newJ = Math.max(newJ, 0);
    	
//    	if(newI>=0 && newJ>=0){
    		constaints.put(new Pair<Integer,Integer>(newI, newJ), null);
//    	}
	}
}

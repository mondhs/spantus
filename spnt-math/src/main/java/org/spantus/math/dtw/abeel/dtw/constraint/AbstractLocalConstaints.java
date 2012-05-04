package org.spantus.math.dtw.abeel.dtw.constraint;

import java.util.LinkedHashMap;
import java.util.Map;

import scikit.util.Pair;
/**
 * http://www.springer.com/cda/content/document/cda_downloaddocument/9783540740476-c1.pdf?SGWID=0-0-45-452103-p173751818
 * @author mondhs
 * @since 0.3
 *
 */
public abstract class AbstractLocalConstaints implements LocalConstaints {
	/**
	 * 
	 */
	public Map<Pair<Integer, Integer>,Double> createLocalConstraints(int i, int j) {
		Map<Pair<Integer, Integer>,Double> constaints = new LinkedHashMap<Pair<Integer,Integer>, Double>();
    	
		if(i == 0 && j == 0){
			return null;
		}
    	addConstraints(constaints, i, j);
    	
    	return constaints;
	}
	/**
	 * 
	 * @param constaints
	 * @param i
	 * @param j
	 */
	public abstract void addConstraints(Map<Pair<Integer, Integer>, Double> constaints, int i, int j);

	/**
	 * 
	 * 
	 * @param constaints
	 * @param newI
	 * @param newJ
	 * @param coef
	 */
    protected void safeAdd(Map<Pair<Integer, Integer>, Double> constaints,
    		Integer newI, Integer newJ, Double coef) {
//    	newI = Math.max(newI, 0);
//    	newJ = Math.max(newJ, 0);
    	
    	if(newI>=0 && newJ>=0){
    		constaints.put(new Pair<Integer,Integer>(newI, newJ), coef);
    	}
	}
}

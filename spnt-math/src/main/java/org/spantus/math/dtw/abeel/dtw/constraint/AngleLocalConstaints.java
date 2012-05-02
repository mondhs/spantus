package org.spantus.math.dtw.abeel.dtw.constraint;

import java.util.Map;

import scikit.util.Pair;
/**
 * 
 * @author mondhs
 * @since 0.3
 *
 */
public class AngleLocalConstaints extends AbstractLocalConstaints {

	@Override
	public void addConstraints(Map<Pair<Integer, Integer>, Double> constaints, int i, int j) {
    	safeAdd(constaints, i-1, j-1);
    	safeAdd(constaints, i-1, j-2);
    	safeAdd(constaints, i-2, j-1);
	}

	

}

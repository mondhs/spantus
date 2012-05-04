package org.spantus.math.dtw.abeel.dtw.constraint;

import java.util.Map;

import scikit.util.Pair;
/**
 * 
 * @author mondhs
 * @since 0.3
 *
 */
public class DefaultLocalConstaints extends AbstractLocalConstaints {

	@Override
	public void addConstraints(Map<Pair<Integer, Integer>, Double> constaints, int i, int j) {
    	Double minPriority = 2D;
    	Double maxPriority = 1D;
		safeAdd(constaints, i-1, j-1, maxPriority);
    	safeAdd(constaints, i-1, j, minPriority);
    	safeAdd(constaints, i, j-1, minPriority);
	}

	

}

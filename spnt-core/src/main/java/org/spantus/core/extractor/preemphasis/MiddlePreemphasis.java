package org.spantus.core.extractor.preemphasis;

public class MiddlePreemphasis implements Preemphasis {

	private Double previousValueZ1 = 0D;
	Double previousValueZ2 = 0D;
	
	/**
	 * 		y[n] = b0 x[n] + b1 x[n-1] + b2 x[n-2]
	 *	b0 = 0.3426, b1 = 0.4945 and b2 = -0.64
	 */
	public Double process(Double currentValue){
		Double val = currentValue.doubleValue();
		Double b0 = 0.3426D, b1 = 0.4945D, b2 = -0.64D;
		val = b0*val+b1*previousValueZ1+b2*previousValueZ2;
		previousValueZ2 = previousValueZ1;
		previousValueZ1 = currentValue;
		return val;
		
	}

}

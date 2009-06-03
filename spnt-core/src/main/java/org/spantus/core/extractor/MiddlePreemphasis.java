package org.spantus.core.extractor;

public class MiddlePreemphasis implements Preemphasis {

	private Float previousValueZ1 = 0F;
	Float previousValueZ2 = 0F;
	
	/**
	 * 		y[n] = b0 x[n] + b1 x[n-1] + b2 x[n-2]
	 *	b0 = 0.3426, b1 = 0.4945 and b2 = -0.64
	 */
	public Float process(Float currentValue){
		Double val = currentValue.doubleValue();
		float b0 = 0.3426f, b1 = 0.4945f, b2 = -0.64f;
		val = b0*val+b1*previousValueZ1+b2*previousValueZ2;
		previousValueZ2 = previousValueZ1;
		previousValueZ1 = currentValue;
		return val.floatValue();
		
	}

}

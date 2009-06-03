package org.spantus.core.extractor;

public class HighPreemphasis implements Preemphasis {

	private Float previousValueZ1 = 0F;
	
	public Float process(Float currentValue) {
		Double val = currentValue.doubleValue();
		val -=  (previousValueZ1*0.95);
		previousValueZ1 = currentValue;
		return val.floatValue();
	}

}

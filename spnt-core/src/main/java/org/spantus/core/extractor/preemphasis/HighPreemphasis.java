package org.spantus.core.extractor.preemphasis;

public class HighPreemphasis implements Preemphasis {

	private Double previousValueZ1 = 0D;
	
	public Double process(Double currentValue) {
		Double val = currentValue.doubleValue();
		val -=  (previousValueZ1*0.95);
		previousValueZ1 = currentValue;
		return val;
	}

}

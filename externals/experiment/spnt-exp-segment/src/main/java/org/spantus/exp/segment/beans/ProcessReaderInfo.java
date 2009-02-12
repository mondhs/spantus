package org.spantus.exp.segment.beans;


public class ProcessReaderInfo {

	Double thresholdCoef;
	
	public ProcessReaderInfo() {
	}
	
	
	
	public ProcessReaderInfo(Double thresholdCoef) {
		super();
		this.thresholdCoef = thresholdCoef;
	}



	public Double getThresholdCoef() {
		return thresholdCoef;
	}

	public void setThresholdCoef(Double thresholdCoef) {
		this.thresholdCoef = thresholdCoef;
	}
	
}

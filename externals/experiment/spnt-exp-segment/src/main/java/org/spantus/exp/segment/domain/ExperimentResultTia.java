package org.spantus.exp.segment.domain;


/**
 * 
 * @author Mindaugas Greibus
 * 
 */
public class ExperimentResultTia extends ExperimentResult {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	Float onset;
	Float steady;
	Float offset;
	Float deltaVAF;
	public Float getOnset() {
		return onset;
	}
	public void setOnset(Float onset) {
		this.onset = onset;
	}
	public Float getSteady() {
		return steady;
	}
	public void setSteady(Float steady) {
		this.steady = steady;
	}
	public Float getOffset() {
		return offset;
	}
	public void setOffset(Float offset) {
		this.offset = offset;
	}
	public Float getDeltaVAF() {
		return deltaVAF;
	}
	public void setDeltaVAF(Float deltaVAF) {
		this.deltaVAF = deltaVAF;
	}
}

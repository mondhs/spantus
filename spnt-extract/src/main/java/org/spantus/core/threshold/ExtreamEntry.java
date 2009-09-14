package org.spantus.core.threshold;

/**
 * 
 * @author Mindaugas Greibus
 * 
 *
 */
public class ExtreamEntry {
	
	public enum SignalStates{stable, maxExtream, increasing, minExtream, decreasing} 

	
	private Integer index;
	private Float value;
	private SignalStates signalStates;
	
	public ExtreamEntry(Integer index, Float value,
			SignalStates signalStates) {
		super();
		this.index = index;
		this.value = value;
		this.signalStates = signalStates;
	}
	public Integer getIndex() {
		return index;
	}
	public void setIndex(Integer index) {
		this.index = index;
	}
	
	public Float getValue() {
		return value;
	}
	public void setValue(Float value) {
		this.value = value;
	}
	public SignalStates getSignalStates() {
		return signalStates;
	}
	public void setSignalStates(SignalStates signalStates) {
		this.signalStates = signalStates;
	}
	@Override
	public String toString() {
		return getIndex() +"=>["+getValue() + "; " + getSignalStates() + "]";
	}
}

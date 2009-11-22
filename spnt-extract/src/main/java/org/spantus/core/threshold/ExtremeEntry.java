package org.spantus.core.threshold;


/**
 * 
 * @author Mindaugas Greibus
 * 
 *
 */
public class ExtremeEntry {
	
	public enum SignalStates{stable, max, increasing, min, decreasing} 

	
	private Integer index;
	private Float value;
	private SignalStates signalState;
	private ExtremeEntry next;
	private ExtremeEntry previous;
	
	public ExtremeEntry(Integer index, Float value,
			SignalStates signalState) {
		super();
		this.index = index;
		this.value = value;
		this.signalState = signalState;
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
	public SignalStates getSignalState() {
		return signalState;
	}
	public void setSignalState(SignalStates signalStates) {
		this.signalState = signalStates;
	}
	
	public void link(ExtremeEntry previous, ExtremeEntry next){
		this.next = next;
		this.previous = previous;
	}
	
	public ExtremeEntry getNext() {
		return next;
	}
	public void setNext(ExtremeEntry next) {
		this.next = next;
	}
	public ExtremeEntry getPrevious() {
		return previous;
	}
	public void setPrevious(ExtremeEntry previous) {
		this.previous = previous;
	}
	
	@Override
	public String toString() {
		return getIndex() +"=>["+getValue() + "; " + getSignalState() + "]";
	}
}

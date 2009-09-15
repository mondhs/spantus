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
	private ExtreamEntry next;
	private ExtreamEntry previous;
	
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
	
	public void link(ExtreamEntry previous, ExtreamEntry next){
		this.next = next;
		this.previous = previous;
	}
	
	public ExtreamEntry getNext() {
		return next;
	}
	public void setNext(ExtreamEntry next) {
		this.next = next;
	}
	public ExtreamEntry getPrevious() {
		return previous;
	}
	public void setPrevious(ExtreamEntry previous) {
		this.previous = previous;
	}
	
	@Override
	public String toString() {
		return getIndex() +"=>["+getValue() + "; " + getSignalStates() + "]";
	}
}

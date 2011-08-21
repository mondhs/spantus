package org.spantus.extractor.segments.offline;

import java.io.Serializable;

import org.spantus.utils.Assert;


/**
 * 
 * @author Mindaugas Greibus
 * 
 *
 */
public class ExtremeEntry implements Cloneable, Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public enum FeatureStates{stable, max, increasing, min, decreasing} 

	
	private Integer index;
	private Double value;
	private FeatureStates signalState;
	private ExtremeEntry next;
	private ExtremeEntry previous;
	
	public ExtremeEntry(Integer index, Double value,
			FeatureStates signalState) {
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
	
	public Double getValue() {
		return value;
	}
	public void setValue(Double value) {
		this.value = value;
	}
	public FeatureStates getSignalState() {
		return signalState;
	}
	public void setSignalState(FeatureStates signalStates) {
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
	
	public boolean gtV(ExtremeEntry entry){
		return this.getValue()>entry.getValue();
	}
	public boolean ltV(ExtremeEntry entry){
		return this.getValue()<entry.getValue();
	}
	
	public boolean before(ExtremeEntry entry){
		return this.getIndex()>entry.getIndex();
	}
	public boolean after(ExtremeEntry entry){
		return this.getIndex()<entry.getIndex();
	}
	
	@Override
	public String toString() {
		return getIndex() +"=>["+getValue() + "; " + getSignalState() + "]";
	}
	
	public ExtremeEntry clone(){
		try {
			return (ExtremeEntry)super.clone();
		} catch (CloneNotSupportedException e) {
			throw new IllegalArgumentException(e);
		}
	}
}

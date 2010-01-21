package org.spantus.core.threshold;

import java.text.MessageFormat;

public class ExtremeSegment {
	private ExtremeEntry startEntry;
	private ExtremeEntry middleEntry;
	private ExtremeEntry endEntry;
	
//	private ExtremeSegment next;
//	private ExtremeSegment previous;
	
	public ExtremeEntry getStartEntry() {
		return startEntry;
	}
	public void setStartEntry(ExtremeEntry startEntry) {
		this.startEntry = startEntry;
	}
	public ExtremeEntry getMiddleEntry() {
		return middleEntry;
	}
	public void setMiddleEntry(ExtremeEntry middleEntry) {
		this.middleEntry = middleEntry;
	}
	public ExtremeEntry getEndEntry() {
		return endEntry;
	}
	public void setEndEntry(ExtremeEntry endEntry) {
		this.endEntry = endEntry;
	}
//	public ExtremeSegment getNext() {
//		return next;
//	}
//	public void setNext(ExtremeSegment next) {
//		this.next = next;
//	}
//	public ExtremeSegment getPrevious() {
//		return previous;
//	}
//	public void setPrevious(ExtremeSegment previous) {
//		this.previous = previous;
//	}
	/**
	 * 
	 * @return
	 */
	public Long getPeakLength(){
		return (long)(getEndEntry().getIndex()-getStartEntry().getIndex());
	}

	@Override
	public String toString() {
		String toString = MessageFormat.format("{0}[{1};{2};{3}]", getClass().getSimpleName(), getStartEntry(),getMiddleEntry(), getEndEntry());
		return toString;
	}
	public boolean isIncrease(){
		return getEndEntry().getValue().compareTo(getStartEntry().getValue())>0;
	}
	public boolean isDecrease(){
		return getStartEntry().getValue().compareTo(getEndEntry().getValue())>0;
	}

}

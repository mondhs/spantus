package org.spantus.core.threshold;

import java.util.LinkedList;
import java.util.List;

import org.spantus.core.FrameValues;


public class ExtremeSegment {
	private ExtremeEntry startEntry;
	private ExtremeEntry peakEntry;
	private ExtremeEntry endEntry;
	
	private List<ExtremeEntry> peakEntries;

	private FrameValues values;
	
	public boolean isIncrease(){
		return getEndEntry().getValue().compareTo(getStartEntry().getValue())>0;
	}
	public boolean isDecrease(){
		return getStartEntry().getValue().compareTo(getEndEntry().getValue())>0;
	}
	
	//calculates
	public Double getCalculatedArea(){
		Double area = 0D;
		for (Float f1: getValues()) {
			area += f1;
		}
		return area;
	}

	public Long getCalculatedLength(){
		Long time = getValues().indextoMils(getValues().size());
		return time;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(getClass().getSimpleName()).append("[");
		if(getStartEntry() != null){
			sb.append(getStartEntry().getIndex()).append("=").append(getStartEntry().getValue()).append(";");
		}else{
			sb.append("-;");
		}
		if(getPeakEntry() != null){
			sb.append(getPeakEntry().getIndex()).append("=").append(getPeakEntry().getValue()).append(";");
		}else{
			sb.append("-;");
		}
		if(getEndEntry() != null){
			sb.append(getEndEntry().getIndex()).append("=").append(getEndEntry().getValue()).append(";");
		}else{
			sb.append("-;");
		}
		
		sb.append("]");
//		String toString = MessageFormat.format("{0}[{1}={2};{3}={4};{5}={6}]", getClass().getSimpleName(),
//				getStartEntry().getIndex(), getStartEntry().getValue().toString(),
//				getMiddleEntry().getIndex(), getMiddleEntry().getValue().toString(),
//				getEndEntry().getIndex(), getEndEntry().getValue().toString()
//				);
		return sb.toString();
	}
	
	//getters and setters
	
	
	
	public ExtremeEntry getStartEntry() {
		return startEntry;
	}
	public void setStartEntry(ExtremeEntry startEntry) {
		this.startEntry = startEntry;
	}
	public ExtremeEntry getPeakEntry() {
		return peakEntry;
	}
	public void setPeakEntry(ExtremeEntry middleEntry) {
		this.peakEntry = middleEntry;
	}
	public ExtremeEntry getEndEntry() {
		return endEntry;
	}
	public void setEndEntry(ExtremeEntry endEntry) {
		this.endEntry = endEntry;
	}
	
	public FrameValues getValues() {
		return values;
	}
	public void setValues(FrameValues values) {
		this.values = values;
	}
	
	public List<ExtremeEntry> getPeakEntries() {
		if(peakEntries == null){
			peakEntries = new LinkedList<ExtremeEntry>();
		}
		return peakEntries;
	}
	public void setPeakEntries(List<ExtremeEntry> peakEntries) {
		this.peakEntries = peakEntries;
	}


}

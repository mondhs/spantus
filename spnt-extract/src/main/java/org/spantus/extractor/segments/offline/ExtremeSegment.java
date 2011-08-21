package org.spantus.extractor.segments.offline;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

import org.spantus.core.FrameValues;
import org.spantus.core.marker.Marker;
import org.spantus.utils.Assert;


public class ExtremeSegment extends Marker implements Serializable, Cloneable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private ExtremeEntry startEntry;
	private ExtremeEntry peakEntry;
	private ExtremeEntry endEntry;
	private boolean approved = false;
	
	private LinkedList<ExtremeEntry> peakEntries;

	private FrameValues values;
	
	public boolean isIncrease(){
		if(getStartEntry() == null || getEndEntry() == null) return false;
		Double start = getStartEntry().getValue();
		Double end = getEndEntry().getValue();
		return end.compareTo(start)<0;
	}
	public boolean isDecrease(){
		if(getStartEntry() == null || getEndEntry() == null) return false;
		Double start = getStartEntry().getValue();
		Double end = getEndEntry().getValue();
		return start.compareTo(end)>0;
	}
	public boolean isDecrease(ExtremeSegment segment){
//		Double thisPeak = this.getPeakEntry().getValue();
//		Double otherPeak = segment.getPeakEntry().getValue();
//		boolean decrease = isDecrease() && segment.isDecrease() && thisPeak>otherPeak; 
		Double thisPeak = this.getPeakEntry().getValue();
		Double otherPeak = segment.getPeakEntry().getValue();
		Double thisStart = this.getStartEntry().getValue();
		Double otherStart = segment.getStartEntry().getValue();
		boolean decrease = thisPeak<otherPeak && thisStart < otherStart;
		return decrease;
	}

	public boolean isIncrease(ExtremeSegment previousSegment){
		Double thisPeak = this.getPeakEntry().getValue();
		Double otherPeak = previousSegment.getPeakEntry().getValue();
		Double thisStart = this.getStartEntry().getValue();
		Double otherStart = previousSegment.getStartEntry().getValue();
		boolean increase = thisPeak-otherPeak>(thisPeak+otherPeak)*.1 && thisStart > otherStart;
		 
//		boolean increase = isIncrease() && previousSegment.isIncrease() && thisPeak>otherPeak;  
		return increase;
	}
	public boolean isSimilar(ExtremeSegment segment){
		Double lastArea = segment.getCalculatedArea();
		Double currentArea = this.getCalculatedArea();
		Integer lastPeak = segment.getPeakEntries().size();
		Integer currentPeak = this.getPeakEntries().size();
		Long lastLength = segment.getCalculatedLength();
		Long currentLength = this.getCalculatedLength();
                Double diff = 0D;
                if(this.peakEntry.getValue() < segment.peakEntry.getValue()){
                    diff = this.peakEntry.getValue()/segment.peakEntry.getValue();
                }else{
                    diff = segment.peakEntry.getValue()/this.peakEntry.getValue();
                }
                
                double similarity = 0;
//                similarity += diff>.99?.5:0;
//		similarity = lastArea>currentArea*.99 && lastArea<currentArea?0.50:0;
//		similarity += currentArea>lastArea*.99 && currentArea<lastArea?0.50:0;
//		similarity += currentArea==lastArea?0.50:0;
//		similarity += lastLength==currentLength?0.25:0;
//		similarity += lastLength>currentLength*.9 && lastLength<currentLength?0.25:0;
//		similarity += currentLength>lastLength*.9 && currentLength<lastLength?0.25:0;
//		similarity += lastPeak==currentPeak?0.25:0;
		boolean similar = similarity>=.5;
		return similar;
	}

	//calculates
	public Double getCalculatedArea(){
		Double area = 0D;
		for (Double f1: getValues()) {
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
			sb.append(getEndEntry().getIndex()).append("=").append(getEndEntry().getValue());
		}else{
			sb.append("-");
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
		if(endEntry != null){
			Assert.isTrue(getStartEntry()!=null, "start not set");
			Assert.isTrue(getStartEntry().after(endEntry), "End should be after start");
		}
		this.endEntry = endEntry;
	}
	
	public FrameValues getValues() {
		return values;
	}
	public void setValues(FrameValues values) {
		this.values = values;
	}
	
	public LinkedList<ExtremeEntry> getPeakEntries() {
		if(peakEntries == null){
			peakEntries = new LinkedList<ExtremeEntry>();
		}
		return peakEntries;
	}
	public void setPeakEntries(LinkedList<ExtremeEntry> peakEntries) {
		this.peakEntries = peakEntries;
	}
	
	public ExtremeSegment clone() {
		ExtremeSegment esCloned = (ExtremeSegment) super.clone();
		esCloned.setPeakEntries(new LinkedList<ExtremeEntry>());
		for (ExtremeEntry ee : this.getPeakEntries()) {
			esCloned.getPeakEntries().add(ee.clone());
		}
		return esCloned;
	}
	public boolean getApproved() {
		return approved;
	}
	public void setApproved(boolean approved) {
		this.approved = approved;
	}

}

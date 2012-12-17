package org.spantus.extractor.segments.offline;

import java.io.Serializable;
import java.util.LinkedList;

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
		if(endEntry != null){
			Assert.isTrue(middleEntry.after(endEntry), "Peak {0} should be after end {1}",middleEntry, endEntry );
		}
		this.peakEntry = middleEntry;
	}
	public ExtremeEntry getEndEntry() {
		return endEntry;
	}
	public void setEndEntry(ExtremeEntry endEntry) {
		if(endEntry != null){
			Assert.isTrue(getStartEntry()!=null, "start not set");
			Assert.isTrue(getStartEntry().after(endEntry), "End should be after start");
			Assert.isTrue(getPeakEntry().after(endEntry), "Peak should be after end");
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

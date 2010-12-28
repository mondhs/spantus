package org.spantus.extractor.segments.online;

import java.io.Serializable;

public class SegmentInnerData implements Serializable, Cloneable, Comparable<SegmentInnerData> {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public Integer peaks;
	public Double area;
	public Long length;
	
	public SegmentInnerData() {
	}

	public SegmentInnerData(Integer peaks, Double area, Long length) {
		super();
		this.peaks = peaks;
		this.area = area;
		this.length = length;
	}
	
	public Boolean getIsNull(){
		return area == 0D || (length == 0 && peaks == 0);
	}
	
	public void updateAsMin(Integer peaks, Double area, Long length){
		this.peaks = Math.min(this.peaks, peaks);
		this.area = Math.min(this.area, area);
		this.length = Math.min(this.length, length);
	}
	public void updateAsMax(Integer peaks, Double area, Long length){
		this.peaks = Math.max(this.peaks, peaks);
		this.area = Math.max(this.area, area);
		this.length = Math.max(this.length, length);
	}
	public void updateAsAvg(Integer peaks, Double area, Long length){
		this.peaks = (this.peaks + peaks)/2;
		this.area = (this.area + area)/2;
		this.length = (this.length + length)/2;
	}
	
	public Float distance(SegmentInnerData d1){
		return distance(d1, this);
	}
	
	public static Float distance(SegmentInnerData d1, SegmentInnerData d2){
		Double areaDiff = d1.area-d2.area;
//		Long lengthDiff = d1.length-d2.length;
//		Integer peaksDiff = d1.peaks-d2.peaks;
		Double distanceSum = areaDiff*areaDiff;
//		distanceSum += lengthDiff*lengthDiff;
//		distanceSum += peaksDiff*peaksDiff;
		distanceSum = Math.sqrt(distanceSum);
		return distanceSum.floatValue();
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(getClass().getSimpleName()).append("[");
		sb.append("peaks: ").append(peaks).append("; area: ").append(area)
			.append("; length: ").append(length);
		sb.append("]");
		return sb.toString();
	}
	
	
	public SegmentInnerData clone(){
		try {
			return (SegmentInnerData)super.clone();
		} catch (CloneNotSupportedException e) {
			throw new IllegalArgumentException(e);
		}
	}

	public int compareTo(SegmentInnerData o) {
		int areaDiff = o.area>this.area?1:-1;
		int lengthDiff = o.length>this.length?1:-1;
		int peaksDiff = o.peaks>this.peaks?1:-1;
		return areaDiff+lengthDiff+peaksDiff;
	}

	//getters and setters
	
	public Integer getPeaks() {
		return peaks;
	}

	public void setPeaks(Integer peaks) {
		this.peaks = peaks;
	}

	public Double getArea() {
		return area;
	}

	public void setArea(Double area) {
		this.area = area;
	}

	public Long getLength() {
		return length;
	}

	public void setLength(Long length) {
		this.length = length;
	}
}

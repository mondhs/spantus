package org.spantus.core.marker;

import java.math.BigDecimal;

public class Marker {

	BigDecimal start;
	BigDecimal length;

	String label;
	MarkerExtractionData extractionData;

	public BigDecimal getStart() {
		return start;
	}

	public void setStart(BigDecimal start) {
//		this.start = start.setScale(0, RoundingMode.HALF_UP);
		this.start = start;
	}

	public BigDecimal getLength() {
		return length;
	}

	public void setLength(BigDecimal length) {
		this.length = length;
	}
	
	public void setEnd(BigDecimal end) {
		setLength(end.add(getStart().negate()));
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}
	@Override
	public String toString() {
		return getClass().getSimpleName() + ": " + getLabel() +"[" + getStart() + "; " + getLength() + "]";
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj == null) return false;
		boolean val = this.hashCode() == obj.hashCode();
		return val;
	}

	public MarkerExtractionData getExtractionData() {
		if(extractionData == null){
			extractionData = new MarkerExtractionData();
		}
		return extractionData;
	}

	public void setExtractionData(MarkerExtractionData extractionData) {
		this.extractionData = extractionData;
	}
	
}

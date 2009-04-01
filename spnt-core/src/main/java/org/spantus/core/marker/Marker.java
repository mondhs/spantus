package org.spantus.core.marker;


public class Marker {

	Long start;
	Long length;

	String label;
	MarkerExtractionData extractionData;

	public Long getStart() {
		return start;
	}

	public Long getLength() {
		return length;
	}

	public void setEnd(Long end) {
		setLength(end-getStart());
	}

	public void setStart(Long start) {
		this.start = start;
	}

	public void setLength(Long length) {
		this.length = length;
	}
	
	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}
	@Override
	public String toString() {
		return getClass().getSimpleName() + ": " + getLabel() +"[" + getStart() + "; " + (getStart()+getLength()) + "]";
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

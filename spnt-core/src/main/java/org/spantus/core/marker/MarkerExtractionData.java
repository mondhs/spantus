package org.spantus.core.marker;

public class MarkerExtractionData {
	
	Long startSampleNum;
	
	Long lengthSampleNum;
	
	public Long getStartSampleNum() {
		return startSampleNum;
	}

	public void setStartSampleNum(Long startInSample) {
		this.startSampleNum = startInSample;
	}

	public Long getLengthSampleNum() {
		return lengthSampleNum;
	}

	public void setLengthSampleNum(Long lengthInSample) {
		this.lengthSampleNum = lengthInSample;
	}
	
	public void setEndSampleNum(Long end) {
		setLengthSampleNum(end - getStartSampleNum());
	}
}

package org.spantus.segment.online;

import java.math.BigDecimal;

import org.spantus.segment.SegmentatorParam;

public class OnlineDecisionSegmentatorParam extends SegmentatorParam {
	private BigDecimal minLength;
	private BigDecimal minSpace;
	private BigDecimal expandMarkerInTime;
	
	public BigDecimal getMinLength() {
		if(minLength == null){
			minLength = BigDecimal.ZERO.setScale(0);
		}
		return minLength;
	}
	public void setMinLength(BigDecimal minSignalLength) {
		this.minLength = minSignalLength;
	}
	
	public void setMinLength(Long minSignalLength) {
		setMinLength(BigDecimal.valueOf(minSignalLength));
	}

	public BigDecimal getMinSpace() {
		if(minSpace == null){
			minSpace = BigDecimal.ZERO.setScale(0);
		}
		return minSpace;
	}

	public void setMinSpace(Long minLengthToPrevious) {
		setMinSpace(BigDecimal.valueOf(minLengthToPrevious));
	}
	public void setMinSpace(BigDecimal minLengthToPrevious) {
		this.minSpace = minLengthToPrevious;
	}

	public BigDecimal getExpandMarkerInTime() {
		if(expandMarkerInTime == null){
			expandMarkerInTime = BigDecimal.ZERO.setScale(0);
		}
		return expandMarkerInTime;
	}

	public void setExpandMarkerInTime(Long latency) {
		this.expandMarkerInTime = BigDecimal.valueOf(latency);
	}
}

package org.spantus.extractor.segments.online.rule;

import org.spantus.extractor.segments.offline.ExtremeSegment;

public class RuleDataCtx {
	ExtremeSegment currentSegment = null;
	ExtremeSegment lastSegment = null;
	// boolean segmentEnd = ctx.getFoundEndSegment();
	// boolean segmentStart = ctx.getFoundStartSegment();

//	boolean segmentPeak = ctx.getFoundPeakSegment();
//	boolean noiseClass = true;

//	Double currentArea = null;
	Integer currentPeakCount = null;
	Double currentPeakValue = null;
	Long currentLength = null;
	Double currentAngle = null;
	Long stableLength = null;
//	Double lastArea = null;
	Double lastPeakValue = null;
	Integer lastPeakCount = null;
	Double lastAngle = null;
	Long lastLength = null;
	boolean isIncrease = false;
	boolean isDecrease = false;
//	boolean isSimilar = false;
//	int lastSizeValues = 0;
//	int currentSizeValues = 0;
	Long distanceBetweenPaeks = Long.MAX_VALUE;
	String className = "";
	public ExtremeSegment getCurrentSegment() {
		return currentSegment;
	}
	public void setCurrentSegment(ExtremeSegment currentSegment) {
		this.currentSegment = currentSegment;
	}
	public ExtremeSegment getLastSegment() {
		return lastSegment;
	}
	public void setLastSegment(ExtremeSegment lastSegment) {
		this.lastSegment = lastSegment;
	}
	public Integer getCurrentPeakCount() {
		return currentPeakCount;
	}
	public void setCurrentPeakCount(Integer currentPeakCount) {
		this.currentPeakCount = currentPeakCount;
	}
	public Double getCurrentPeakValue() {
		return currentPeakValue;
	}
	public void setCurrentPeakValue(Double currentPeakValue) {
		this.currentPeakValue = currentPeakValue;
	}
	public Long getCurrentLength() {
		return currentLength;
	}
	public void setCurrentLength(Long currentLength) {
		this.currentLength = currentLength;
	}
	public Double getCurrentAngle() {
		return currentAngle;
	}
	public void setCurrentAngle(Double currentAngle) {
		this.currentAngle = currentAngle;
	}
	public Long getStableLength() {
		return stableLength;
	}
	public void setStableLength(Long stableLength) {
		this.stableLength = stableLength;
	}
	public Double getLastPeakValue() {
		return lastPeakValue;
	}
	public void setLastPeakValue(Double lastPeakValue) {
		this.lastPeakValue = lastPeakValue;
	}
	public Integer getLastPeakCount() {
		return lastPeakCount;
	}
	public void setLastPeakCount(Integer lastPeakCount) {
		this.lastPeakCount = lastPeakCount;
	}
	public Double getLastAngle() {
		return lastAngle;
	}
	public void setLastAngle(Double lastAngle) {
		this.lastAngle = lastAngle;
	}
	public Long getLastLength() {
		return lastLength;
	}
	public void setLastLength(Long lastLength) {
		this.lastLength = lastLength;
	}
	public boolean isIncrease() {
		return isIncrease;
	}
	public void setIncrease(boolean isIncrease) {
		this.isIncrease = isIncrease;
	}
	public boolean isDecrease() {
		return isDecrease;
	}
	public void setDecrease(boolean isDecrease) {
		this.isDecrease = isDecrease;
	}
	public Long getDistanceBetweenPaeks() {
		return distanceBetweenPaeks;
	}
	public void setDistanceBetweenPaeks(Long distanceBetweenPaeks) {
		this.distanceBetweenPaeks = distanceBetweenPaeks;
	}
	public String getClassName() {
		return className;
	}
	public void setClassName(String className) {
		this.className = className;
	}
	
	
}

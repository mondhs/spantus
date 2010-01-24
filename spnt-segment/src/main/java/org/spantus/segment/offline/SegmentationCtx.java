package org.spantus.segment.offline;

import org.spantus.core.marker.Marker;
import org.spantus.core.marker.MarkerSet;

class SegmentationCtx{
	private Float previousState;
	
	private Float currentState;
	
	private Long currentMoment;

	private Long prevMoment;

	
//	private Float sampleRate;
	
	private MarkerSet markerSet;
	
	private Marker currentMarker;

	public MarkerSet getMarkerSet() {
		return markerSet;
	}

	public void setMarkerSet(MarkerSet markerSet) {
		this.markerSet = markerSet;
	}

	public Marker getCurrentMarker() {
		return currentMarker;
	}

	public void setCurrentMarker(Marker currentMarker) {
		this.currentMarker = currentMarker;
	}

	public Float getPreviousState() {
		return previousState;
	}

	public void setPreviousState(Float previousState) {
		this.previousState = previousState;
	}

	public Float getCurrentState() {
		return currentState;
	}

	public void setCurrentState(Float currentState) {
		this.currentState = currentState;
	}

	public Long getCurrentMoment() {
		return currentMoment;
	}

	public void setCurrentMoment(Long currentMomonet) {
		this.currentMoment = currentMomonet;
	}

	public Long getPrevMoment() {
		return prevMoment;
	}

	public void setPrevMoment(Long prevMoment) {
		this.prevMoment = prevMoment;
	}

//	public Float getSampleRate() {
//		return sampleRate;
//	}
//
//	public void setSampleRate(Float sampleRate) {
//		this.sampleRate = sampleRate;
//	}


}


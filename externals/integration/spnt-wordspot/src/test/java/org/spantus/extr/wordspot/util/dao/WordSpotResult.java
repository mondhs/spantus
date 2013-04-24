/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.spantus.extr.wordspot.util.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.spantus.core.beans.RecognitionResult;
import org.spantus.core.beans.SignalSegment;
import org.spantus.core.marker.Marker;

/**
 * 
 * @author mondhs
 */
public class WordSpotResult {
	private List<Marker> originalMarker = new ArrayList<>();
	Map<RecognitionResult, SignalSegment> segments;
	private long experimentStarted;
	private long experimentEnded;
	private long audioLength;
	private String fileName;
	private int operationCount;

	public List<Marker> getOriginalMarker() {
		return originalMarker;
	}

	public void setOriginalMarker(List<Marker> originalMarker) {
		this.originalMarker = originalMarker;
	}

	public Map<RecognitionResult, SignalSegment> getSegments() {
		return segments;
	}

	public void setSegments(Map<RecognitionResult, SignalSegment> segments) {
		this.segments = segments;
	}

	public long getExperimentStarted() {
		return experimentStarted;
	}

	public void setExperimentStarted(long experimentStarted) {
		this.experimentStarted = experimentStarted;
	}

	public long getExperimentEnded() {
		return experimentEnded;
	}

	public void setExperimentEnded(long experimentEnded) {
		this.experimentEnded = experimentEnded;
	}

	public long getAudioLength() {
		return audioLength;
	}

	public void setAudioLength(long audioLength) {
		this.audioLength = audioLength;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getFileName() {
		return fileName;
	}

	public void setOperationCount(int experimentOperationCount) {
		this.operationCount = experimentOperationCount;

	}

	public int getOperationCount() {
		return operationCount;
	}

}

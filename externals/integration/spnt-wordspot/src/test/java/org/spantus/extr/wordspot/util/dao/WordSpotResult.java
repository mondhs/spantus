/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.spantus.extr.wordspot.util.dao;

import java.io.File;
import java.util.Map;
import org.spantus.core.beans.RecognitionResult;
import org.spantus.core.beans.SignalSegment;
import org.spantus.core.marker.Marker;

/**
 *
 * @author mondhs
 */
public class WordSpotResult {
    private Marker originalMarker;
    Map<RecognitionResult, SignalSegment> segments;
    private long experimentStarted;
    private long experimentEnded;
    private long audioLength;
    private String fileName;

    public Marker getOriginalMarker() {
        return originalMarker;
    }

    public void setOriginalMarker(Marker originalMarker) {
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

   
}

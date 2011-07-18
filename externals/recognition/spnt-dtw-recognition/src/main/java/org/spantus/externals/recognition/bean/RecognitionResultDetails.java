/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.spantus.externals.recognition.bean;

import java.awt.Point;
import java.util.List;
import java.util.Map;

/**
 *
 * @author mondhs
 */
public class RecognitionResultDetails extends RecognitionResult {

    private Map<String, List<Point>> path;
    private Map<String, Double> targetLegths;
    private Map<String, Double> sampleLegths;
    private String audioFilePath;


    public void setPath(Map<String, List<Point>> path) {
        this.path = path;
    }

    public Map<String, List<Point>> getPath() {
        return path;
    }

    public Map<String, Double> getTargetLegths() {
        return targetLegths;
    }

    public void setTargetLegths(Map<String, Double> targetLegths) {
        this.targetLegths = targetLegths;
    }
    public Map<String, Double> getSampleLegths() {
        return sampleLegths;
    }

    public void setSampleLegths(Map<String, Double> sampleLegths) {
        this.sampleLegths = sampleLegths;
    }

    public String getAudioFilePath() {
        return this.audioFilePath;
    }
    
    public void setAudioFilePath(String audioFilePath) {
        this.audioFilePath = audioFilePath;
    }

}

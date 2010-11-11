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
    private Map<String, Float> targetLegths;
    private Map<String, Float> sampleLegths;


    public void setPath(Map<String, List<Point>> path) {
        this.path = path;
    }

    public Map<String, List<Point>> getPath() {
        return path;
    }

    public Map<String, Float> getTargetLegths() {
        return targetLegths;
    }

    public void setTargetLegths(Map<String, Float> targetLegths) {
        this.targetLegths = targetLegths;
    }
    public Map<String, Float> getSampleLegths() {
        return sampleLegths;
    }

    public void setSampleLegths(Map<String, Float> sampleLegths) {
        this.sampleLegths = sampleLegths;
    }

}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.spantus.core.beans;

import java.awt.Point;
import java.util.List;
import java.util.Map;

import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.stat.descriptive.StatisticalSummary;

/**
 *
 * @author mondhs
 */
public class RecognitionResultDetails extends RecognitionResult {

    private Map<String, List<Point>> path;
    private Map<String, RealMatrix> costMatrixMap;
    private Map<String, StatisticalSummary> statisticalSummaryMap;
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

	public Map<String, RealMatrix> getCostMatrixMap() {
		return costMatrixMap;
	}

	public void setCostMatrixMap(Map<String, RealMatrix> costMatrixMap) {
		this.costMatrixMap = costMatrixMap;
	}

	public Map<String, StatisticalSummary> getStatisticalSummaryMap() {
		return statisticalSummaryMap;
	}

	public void setStatisticalSummaryMap(
			Map<String, StatisticalSummary> statisticalSummaryMap) {
		this.statisticalSummaryMap = statisticalSummaryMap;
	}


}

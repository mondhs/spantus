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
public class RecognitionResultDetails {

    private Map<String, List<Point>> path;
    private Map<String, RealMatrix> costMatrixMap;
    private Map<String, StatisticalSummary> statisticalSummaryMap;
    private Map<String, Long> targetLegths;
    private Map<String, Long> sampleLegths;
    private Map<String, Double> distances;
    private String audioFilePath;


    public void setPath(Map<String, List<Point>> path) {
        this.path = path;
    }

    public Map<String, List<Point>> getPath() {
        return path;
    }

    public Map<String, Long> getTargetLegths() {
        return targetLegths;
    }

    public void setTargetLegths(Map<String, Long> targetLegths) {
        this.targetLegths = targetLegths;
    }
    public Map<String, Long> getSampleLegths() {
        return sampleLegths;
    }

    public void setSampleLegths(Map<String, Long> sampleLegths) {
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
    
	public Map<String, Double> getDistances() {
		return distances;
	}

	public void setDistances(Map<String, Double> distances) {
		this.distances = distances;
	}


}

package org.spantus.core.beans;

import java.util.Map;



public class RecognitionResult {
    private SignalSegment info;
    private Double distance;
    private Map<String, Double> scores;

    public SignalSegment getInfo() {
        return info;
    }

    public void setInfo(SignalSegment info) {
        this.info = info;
    }

    public Double getDistance() {
        return distance;
    }

    public void setDistance(Double distance) {
        this.distance = distance;
    }


    public Map<String, Double> getScores() {
        return scores;
    }

    public void setScores(Map<String, Double> score) {
        this.scores = score;
    }
   

    @Override
    public String toString() {
        return getInfo().getName() +  "[" + getDistance() + "]";
    }

}

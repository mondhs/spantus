package org.spantus.externals.recognition.bean;

import java.util.Map;


public class RecognitionResult {
    private CorpusEntry info;
    private Double distance;
    private Map<String, Double> scores;

    public CorpusEntry getInfo() {
        return info;
    }

    public void setInfo(CorpusEntry info) {
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
        return getInfo().getName() + ":" + getInfo().getId() + "[" + getDistance() + "]";
    }

}

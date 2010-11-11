package org.spantus.externals.recognition.bean;

import java.util.Map;

public class RecognitionResult {

    private CorpusEntry info;
    private Float distance;
    private Map<String, Float> scores;

    public CorpusEntry getInfo() {
        return info;
    }

    public void setInfo(CorpusEntry info) {
        this.info = info;
    }

    public Float getDistance() {
        return distance;
    }

    public void setDistance(Float distance) {
        this.distance = distance;
    }


    public Map<String, Float> getScores() {
        return scores;
    }

    public void setScores(Map<String, Float> score) {
        this.scores = score;
    }
   

    @Override
    public String toString() {
        return getInfo().getName() + ":" + getInfo().getId() + "[" + getDistance() + "]";
    }

}

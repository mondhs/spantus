package org.spantus.externals.recognition.bean;

import java.awt.Point;
import java.util.List;

public class RecognitionResult {

    private CorpusEntry info;
    private Float distance;


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

    @Override
    public String toString() {
        return getInfo().getName() + ":" + getInfo().getId() + "[" + getDistance() + "]";
    }

}

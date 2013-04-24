package org.spantus.core.beans;

import java.util.Map;




public class RecognitionResult {
    private SignalSegment info;
    private Double distance;
    private RecognitionResultDetails details;
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


    @Override
    public String toString() {
    	Map<String, Double> distantces = null;
    	if(getDetails()!= null){
    		distantces = getDetails().getDistances();
    	}
        return getInfo().getName() +  "[" + distantces + "]";
    }

	public RecognitionResultDetails getDetails() {
		return details;
	}

	public void setDetails(RecognitionResultDetails details) {
		this.details = details;
	}

    public Map<String, Double> getScores() {
        return scores;
    }

    public void setScores(Map<String, Double> score) {
        this.scores = score;
    }


}

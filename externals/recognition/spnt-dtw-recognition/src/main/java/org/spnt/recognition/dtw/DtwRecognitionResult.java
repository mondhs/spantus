package org.spnt.recognition.dtw;

public class DtwRecognitionResult {
	private RecognitionModelEntry info;
	private Float distance;

	public RecognitionModelEntry getInfo() {
		return info;
	}

	public void setInfo(RecognitionModelEntry info) {
		this.info = info;
	}

	public Float getDistance() {
		return distance;
	}

	public void setDistance(Float distance) {
		this.distance = distance;
	}
}

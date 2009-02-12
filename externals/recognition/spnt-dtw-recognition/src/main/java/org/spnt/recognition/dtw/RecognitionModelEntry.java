package org.spnt.recognition.dtw;

import org.spantus.core.FrameVectorValues;

public class RecognitionModelEntry {
	String name;
	FrameVectorValues vals;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public FrameVectorValues getVals() {
		return vals;
	}

	public void setVals(FrameVectorValues vals) {
		this.vals = vals;
	}
}

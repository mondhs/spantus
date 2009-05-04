package org.spnt.recognition.bean;

import org.spantus.core.FrameVectorValues;

public class CorpusEntry {
	Long id;
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

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
}

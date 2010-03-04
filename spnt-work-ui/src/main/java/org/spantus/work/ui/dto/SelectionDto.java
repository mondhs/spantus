package org.spantus.work.ui.dto;

public class SelectionDto {
	private Float from;

	private Float length;

	public SelectionDto() {
	}
	
	
	public SelectionDto(Float from, Float length) {
		super();
		this.from = from;
		this.length = length;
	}



	public Float getFrom() {
		return from;
	}

	public void setFrom(Float from) {
		this.from = from;
	}

	public Float getLength() {
		return length;
	}

	public void setLength(Float length) {
		this.length = length;
	}

}

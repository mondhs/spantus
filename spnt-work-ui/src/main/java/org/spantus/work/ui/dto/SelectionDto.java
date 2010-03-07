package org.spantus.work.ui.dto;

import java.text.MessageFormat;

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
	
	@Override
	public String toString() {
		return MessageFormat.format("{0} [from {1};length {2}] ", this.getClass().getSimpleName(), getFrom(), getLength());
	}

}

package org.spantus.work.ui.dto;

import java.io.Serializable;
import java.text.MessageFormat;
import org.spantus.core.marker.Marker;

public class SelectionDto implements Serializable{
	/**
         * In seconds
         */
        private Float from;
        /**
         * In seconds
         */
	private Float length;

	public SelectionDto() {
	}
	
	
	public SelectionDto(Float from, Float length) {
		super();
		this.from = from;
		this.length = length;
	}

        public SelectionDto(Marker marker) {
		this(marker.getStart().floatValue()/1000,
                        marker.getLength().floatValue()/1000);
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

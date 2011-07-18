package org.spantus.work.ui.dto;

import java.io.Serializable;
import java.text.MessageFormat;
import org.spantus.core.marker.Marker;

public class SelectionDto implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
         * In seconds
         */
        private Double from;
        /**
         * In seconds
         */
	private Double length;

	public SelectionDto() {
	}
	
	
	public SelectionDto(Double from, Double length) {
		super();
		this.from = from;
		this.length = length;
	}

        public SelectionDto(Marker marker) {
		this(marker.getStart().doubleValue()/1000,
                        marker.getLength().doubleValue()/1000);
	}

	public Double getFrom() {
		return from;
	}

	public void setFrom(Double from) {
		this.from = from;
	}

	public Double getLength() {
		return length;
	}

	public void setLength(Double length) {
		this.length = length;
	}
	
	@Override
	public String toString() {
		return MessageFormat.format("{0} [from {1};length {2}] ", this.getClass().getSimpleName(), getFrom(), getLength());
	}

}

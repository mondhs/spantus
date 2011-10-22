package org.spantus.exp.recognition.synthesis;

import java.io.Serializable;

import org.spantus.core.marker.MarkerSet;
import org.spantus.core.marker.MarkerSetHolder;
import org.spantus.core.marker.MarkerSetHolder.MarkerSetHolderEnum;

public class Transcribtion implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 3087867740948425957L;
	private MarkerSetHolder holder;
	private StringBuilder transctiption;
	private long finished = 0l;

	public Transcribtion() {
		transctiption = new StringBuilder();
		holder = new MarkerSetHolder();
		holder.getMarkerSets().put(MarkerSetHolderEnum.phone.name(),  new MarkerSet());
		holder.getMarkerSets().get(MarkerSetHolderEnum.phone.name()).setMarkerSetType(MarkerSetHolderEnum.phone.name());
		holder.getMarkerSets().put(MarkerSetHolderEnum.word.name(),  new MarkerSet());
		holder.getMarkerSets().get(MarkerSetHolderEnum.word.name()).setMarkerSetType(MarkerSetHolderEnum.word.name());
	}
	
	public void setHolder(MarkerSetHolder markerSetHolder) {
		this.holder=markerSetHolder;			
	}

	public void setTransctiption(StringBuilder stringBuilder) {
		stringBuilder = new StringBuilder();
	}

	public MarkerSetHolder getHolder() {
		return holder;
	}

	public StringBuilder getTransctiption() {
		return transctiption;
	}

	public void setStringBuilder(StringBuilder stringBuilder) {
		this.transctiption = stringBuilder;
	}

	public long getFinished() {
		return finished;
	}

	public void setFinished(long finished) {
		this.finished = finished;
	}
	public void incFinished(long finished) {
		this.finished += finished;
	}
	
}


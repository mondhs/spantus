package org.spantus.exp.synthesis;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.spantus.core.marker.MarkerSet;
import org.spantus.core.marker.MarkerSetHolder;
import org.spantus.core.marker.MarkerSetHolder.MarkerSetHolderEnum;

public class Transcribtion implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3087867740948425957L;
	private MarkerSetHolder holder;
	private StringBuilder transctiption;
	private String originalText;

	private Long finished = 0l;
	private Long previousPhoneLength = null;
	private List<MarkerMbrola> markerBrolas;

	public Transcribtion() {
		transctiption = new StringBuilder();
		markerBrolas = new ArrayList<>();
		holder = new MarkerSetHolder();
		holder.getMarkerSets().put(MarkerSetHolderEnum.phone.name(),
				new MarkerSet());
		holder.getMarkerSets().get(MarkerSetHolderEnum.phone.name())
				.setMarkerSetType(MarkerSetHolderEnum.phone.name());
		holder.getMarkerSets().put(MarkerSetHolderEnum.word.name(),
				new MarkerSet());
		holder.getMarkerSets().get(MarkerSetHolderEnum.word.name())
				.setMarkerSetType(MarkerSetHolderEnum.word.name());
	}

	public void setHolder(MarkerSetHolder markerSetHolder) {
		this.holder = markerSetHolder;
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

	public void setFinished(Long finished) {
		this.finished = finished;
	}

	public void incFinished(Long finished) {
		this.finished += finished;
	}

	public List<MarkerMbrola> getMarkerBrolas() {
		return markerBrolas;
	}

	public String getOriginalText() {
		return originalText;
	}

	public void setOriginalText(String originalText) {
		this.originalText = originalText;
	}

	public long getPreviousPhoneLength() {
		return previousPhoneLength;
	}

	public void setPreviousPhoneLength(long previousPhoneLength) {
		this.previousPhoneLength = previousPhoneLength;
	}

	@Override
	public String toString() {
		return "Transcribtion [holder=" + holder + ", transctiption="
				+ transctiption + ", originalText=" + originalText
				+ ", finished=" + finished + ", previousPhoneLength="
				+ previousPhoneLength + ", markerBrolas=" + markerBrolas + "]";
	}
	
	

}

package org.spantus.work.ui.dto;

import java.net.URL;

import javax.sound.sampled.AudioFileFormat;

import org.spantus.core.marker.MarkerSetHolder;

public class WorkSample {
	URL currentFile;
	
	String title;

	AudioFileFormat format;
	
	final MarkerSetHolder markerSetHolder = new MarkerSetHolder();
	
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public URL getCurrentFile() {
		return currentFile;
	}

	public void setCurrentFile(URL currentFile) {
		this.currentFile = currentFile;
	}
	public boolean isSamplePlayable() {
		return !getCurrentFile().getFile().endsWith("xml");
	}

	public AudioFileFormat getFormat() {
		return format;
	}

	public void setFormat(AudioFileFormat format) {
		this.format = format;
	}

	public MarkerSetHolder getMarkerSetHolder() {
		return markerSetHolder;
	}

	public void setMarkerSetHolder(MarkerSetHolder markerSetHolder) {
		this.markerSetHolder.getMarkerSets().clear();
		this.markerSetHolder.getMarkerSets().putAll(markerSetHolder.getMarkerSets());
	}
}

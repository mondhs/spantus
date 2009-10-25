package org.spantus.work.ui.dto;

import java.net.URL;

import javax.sound.sampled.AudioFileFormat;

import org.spantus.core.extractor.SignalFormat;
import org.spantus.core.marker.MarkerSetHolder;

public class WorkSample {
	private URL currentFile;
	
	private String title;
	
	private SignalFormat signalFormat;
	
	
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

	public SignalFormat getSignalFormat() {
		if(signalFormat == null){
			signalFormat = new SignalFormat();
		}
		return signalFormat;
	}

	public void setSignalFormat(SignalFormat signalFormat) {
		this.signalFormat = signalFormat;
	}
	

	public void setFormat(AudioFileFormat format) {
//		this.format = format;
		getSignalFormat().setLength(format.getFrameLength()/format.getFormat().getFrameRate());
	}

	public MarkerSetHolder getMarkerSetHolder() {
		return markerSetHolder;
	}

	public void setMarkerSetHolder(MarkerSetHolder markerSetHolder) {
		this.markerSetHolder.getMarkerSets().clear();
		this.markerSetHolder.getMarkerSets().putAll(markerSetHolder.getMarkerSets());
	}

	
}

package org.spantus.work.ui.dto;

import java.net.URL;

import javax.sound.sampled.AudioFileFormat;

import org.spantus.core.extractor.SignalFormat;
import org.spantus.core.marker.MarkerSetHolder;

public class WorkSample {
	private URL currentFile;
	
	private URL noiseFile;
	
	private String title;
	
	private SignalFormat signalFormat;
	
	
	private MarkerSetHolder markerSetHolder = new MarkerSetHolder();
	
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
		if(format == null){
			return;
		}
//		this.format = format;
		getSignalFormat().setLength((double) (format.getFrameLength()/format.getFormat().getFrameRate()));
	}

	public MarkerSetHolder getMarkerSetHolder() {
		return markerSetHolder;
	}

	public void setMarkerSetHolder(MarkerSetHolder markerSetHolder) {
		if(this.markerSetHolder == null){
			this.markerSetHolder = markerSetHolder;
		}else{
			this.markerSetHolder.getMarkerSets().clear();
			this.markerSetHolder.getMarkerSets().putAll(markerSetHolder.getMarkerSets());
		}
	}

	public URL getNoiseFile() {
		return noiseFile;
	}

	public void setNoiseFile(URL noiseFile) {
		this.noiseFile = noiseFile;
	}

	
}

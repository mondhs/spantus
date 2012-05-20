package org.spantus.externals.recognition.bean;

import java.io.File;

import org.spantus.core.beans.SignalSegment;

public class CorpusFileEntry extends SignalSegment{
	/**
	 * 
	 */
	private static final long serialVersionUID = -6222933724583829149L;
	File entryFile;
	File wavFile;

	public File getEntryFile() {
		return entryFile;
	}

	public void setEntryFile(File entryFile) {
		this.entryFile = entryFile;
	}

	public File getWavFile() {
		return wavFile;
	}

	public void setWavFile(File wavFile) {
		this.wavFile = wavFile;
	}



}

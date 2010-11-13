package org.spantus.externals.recognition.bean;

import java.io.File;

public class CorpusFileEntry extends CorpusEntry{
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

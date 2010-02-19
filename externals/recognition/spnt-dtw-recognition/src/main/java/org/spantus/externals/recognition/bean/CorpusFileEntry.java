package org.spantus.externals.recognition.bean;

import java.io.File;

public class CorpusFileEntry{
	CorpusEntry corpusEntry;
	File entryFile;
	File wavFile;

	public CorpusEntry getCorpusEntry() {
		return corpusEntry;
	}

	public void setCorpusEntry(CorpusEntry corpusEntry) {
		this.corpusEntry = corpusEntry;
	}

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

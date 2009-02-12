package org.spantus.work;

import org.spantus.core.extractor.IExtractorInputReader;
import org.spantus.core.marker.MarkerSetHolder;

public class SpantusBundle {
	IExtractorInputReader reader;
	MarkerSetHolder holder;
	public IExtractorInputReader getReader() {
		return reader;
	}
	public void setReader(IExtractorInputReader reader) {
		this.reader = reader;
	}
	public MarkerSetHolder getHolder() {
		return holder;
	}
	public void setHolder(MarkerSetHolder holder) {
		this.holder = holder;
	}
}

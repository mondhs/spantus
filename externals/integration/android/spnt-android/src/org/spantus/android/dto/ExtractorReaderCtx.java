package org.spantus.android.dto;

import org.spantus.android.service.ExtractMarkerOnlineSegmentatorListener;
import org.spantus.core.extractor.IExtractorInputReader;
import org.spantus.extractor.ExtractorInputReader;

public class ExtractorReaderCtx {
	IExtractorInputReader reader;
	ExtractMarkerOnlineSegmentatorListener segmentatorListener;

	public ExtractorReaderCtx(ExtractorInputReader reader,
			ExtractMarkerOnlineSegmentatorListener segmentatorListener) {
		this.reader=reader;
		this.segmentatorListener = segmentatorListener;
	}

	public IExtractorInputReader getReader() {
		return reader;
	}

	public void setReader(IExtractorInputReader reader) {
		this.reader = reader;
	}

	public ExtractMarkerOnlineSegmentatorListener getSegmentatorListener() {
		return segmentatorListener;
	}

	public void setSegmentatorListener(
			ExtractMarkerOnlineSegmentatorListener segmentatorListener) {
		this.segmentatorListener = segmentatorListener;
	}
}

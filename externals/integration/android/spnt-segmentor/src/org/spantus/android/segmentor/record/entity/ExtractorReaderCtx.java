package org.spantus.android.segmentor.record.entity;

import org.spantus.android.segmentor.services.impl.ExtractMarkerOnlineSegmentatorListener;
import org.spantus.core.extractor.IExtractorInputReader;

public class ExtractorReaderCtx {
	IExtractorInputReader reader;
	ExtractMarkerOnlineSegmentatorListener segmentatorListener;

	public ExtractorReaderCtx(IExtractorInputReader reader,
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

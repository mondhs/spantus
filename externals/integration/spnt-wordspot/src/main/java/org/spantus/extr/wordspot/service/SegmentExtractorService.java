package org.spantus.extr.wordspot.service;

import java.net.URL;
import java.util.Collection;

import org.spantus.core.beans.SignalSegment;
import org.spantus.segment.online.ISegmentatorListener;

public interface SegmentExtractorService {

	public Collection<SignalSegment> extractSegmentsOnline(URL url);
	public Collection<SignalSegment> extractSegmentsOffline(URL url);
	public void listenSegments(URL urlFile, ISegmentatorListener listener);
}

package org.spantus.extr.wordspot.service;

import java.net.URL;
import java.util.Collection;

import org.spantus.core.beans.SignalSegment;

public interface SegmentExtractorService {

	public Collection<SignalSegment> extractSegmentsOnline(URL url);
	public Collection<SignalSegment> extractSegmentsOffline(URL url);
	
}

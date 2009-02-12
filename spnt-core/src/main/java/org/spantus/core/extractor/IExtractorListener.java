package org.spantus.core.extractor;

import org.spantus.core.FrameValues;

public interface IExtractorListener {
	public void beforeCalculated(Long sample, FrameValues window);
	public void afterCalculated(Long sample, FrameValues result);
}

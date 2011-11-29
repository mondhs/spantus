package org.spantus.extractor;

import org.spantus.core.FrameValues;
import org.spantus.core.extractor.IExtractorConfig;

public class WindowBufferProcessorCtx {
	private IExtractorConfig config;
	private FrameValues buffer;
	
	public IExtractorConfig getConfig() {
		return config;
	}

	public void setConfig(IExtractorConfig config) {
		this.config = config;
	}

	public FrameValues getBuffer() {
		return buffer;
	}

	public void setBuffer(FrameValues buffer) {
		this.buffer = buffer;
	}
}

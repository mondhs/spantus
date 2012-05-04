package org.spantus.core.extractor.windowing;

import java.io.Serializable;

import org.spantus.core.FrameValues;
import org.spantus.core.extractor.IExtractorConfig;
/**
 * 
 * @author mondhs
 * @since 0.3
 *
 */
public class WindowBufferProcessorCtx implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 521205001891047971L;
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

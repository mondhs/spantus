package org.spantus.core.extractor;

import java.io.Serializable;
import java.util.Map;
import java.util.Set;

public interface IExtractorConfig extends Cloneable, Serializable {
	public float getSampleRate();
	public void setSampleRate(float sampleRate);
	public int getWindowSize();
	public void setWindowSize(int windowSize);
	public int getBitsPerSample();
	public int getBufferSize();
	public void setBufferSize(int bufferSize);
	public int getFrameSize();
	public void setFrameSize(int frameSize);
	public int getWindowOverlap();
	public void setWindowOverlap(int windowOverlap);
	public Set<String> getExtractors();
	public Map<String,ExtractorParam> getParameters();
	public String getWindowing();
	public void setWindowing(String windowing);
	
}

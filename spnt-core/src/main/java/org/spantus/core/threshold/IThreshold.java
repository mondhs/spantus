package org.spantus.core.threshold;

import org.spantus.core.FrameValues;
import org.spantus.core.extractor.IExtractor;

public interface IThreshold extends IExtractor{
	public FrameValues getThresholdValues();
	public FrameValues getState();

}

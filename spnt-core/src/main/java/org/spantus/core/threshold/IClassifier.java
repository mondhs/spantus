package org.spantus.core.threshold;

import org.spantus.core.FrameValues;
import org.spantus.core.extractor.IExtractor;
import org.spantus.core.marker.MarkerSet;

public interface IClassifier extends IExtractor{
	public FrameValues getThresholdValues();
//	public FrameValues getState();
	public MarkerSet getMarkerSet();

}

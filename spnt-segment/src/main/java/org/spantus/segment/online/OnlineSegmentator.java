package org.spantus.segment.online;

import org.spantus.core.extractor.IGeneralExtractor;
import org.spantus.segment.ISegmentator;

public interface OnlineSegmentator extends ISegmentator{
	public void processState(Long sampleNum, IGeneralExtractor extractor,
			Float val);

}

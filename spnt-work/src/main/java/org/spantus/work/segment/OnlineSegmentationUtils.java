package org.spantus.work.segment;

import org.spantus.core.extractor.ExtractorParam;
import org.spantus.core.extractor.IExtractorInputReader;
import org.spantus.extractor.impl.ExtractorEnum;
import org.spantus.extractor.impl.ExtractorUtils;
import org.spantus.segment.online.ThresholdSegmentatorOnline;

public abstract class OnlineSegmentationUtils {
	
	public static ThresholdSegmentatorOnline register(IExtractorInputReader bufferedReader, ExtractorEnum extractorEnum){
		ThresholdSegmentatorOnline segmentator  = new ThresholdSegmentatorOnline();
		ExtractorUtils.registerThreshold(bufferedReader, 
				extractorEnum,
				null,
				segmentator);
		return segmentator;
	}
	public static ThresholdSegmentatorOnline register(IExtractorInputReader bufferedReader, ExtractorEnum extractorEnum,
			ExtractorParam param){
		ThresholdSegmentatorOnline segmentator  = new ThresholdSegmentatorOnline();
		ExtractorUtils.registerThreshold(bufferedReader, 
				extractorEnum,
				param,
				segmentator);
		return segmentator;
	}

}

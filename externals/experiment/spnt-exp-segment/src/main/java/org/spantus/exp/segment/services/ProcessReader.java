package org.spantus.exp.segment.services;

import java.util.Map;
import java.util.Set;

import org.spantus.core.beans.SampleInfo;
import org.spantus.core.extractor.IExtractorInputReader;
import org.spantus.core.extractor.IGeneralExtractor;
import org.spantus.core.threshold.IThreshold;
import org.spantus.exp.segment.beans.ProcessReaderInfo;

public interface ProcessReader {
	public SampleInfo processReader(IExtractorInputReader reader, ProcessReaderInfo processReaderInfo);
	public Set<IThreshold> getFilterThresholdByName(Set<IThreshold> set, String contains);
	public Map<String, Set<String>> generateAllCompbinations(Set<? extends IGeneralExtractor> thresholds, int combinationDepth);
	public <T extends IGeneralExtractor> Set<T> getThresholdSet(Set<T> thresholds, Set<String> thresholdNames);
	public String getName(IGeneralExtractor threshold);
}

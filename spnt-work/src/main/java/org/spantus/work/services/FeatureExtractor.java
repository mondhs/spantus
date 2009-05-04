package org.spantus.work.services;

import java.io.File;

import org.spantus.core.extractor.IExtractorInputReader;
import org.spantus.core.extractor.IGeneralExtractor;
import org.spantus.extractor.impl.ExtractorEnum;

public interface FeatureExtractor {
	public void extract(ExtractorEnum[] extractors, File file);
	public IGeneralExtractor findExtractorByName(String name, IExtractorInputReader reader);
}

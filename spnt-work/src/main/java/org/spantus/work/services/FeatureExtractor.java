package org.spantus.work.services;

import java.io.File;

import org.spantus.extractor.impl.ExtractorEnum;

public interface FeatureExtractor {
	public void extract(ExtractorEnum[] extractors, File file);
}

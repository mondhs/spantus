package org.spantus.mpeg7;

import org.spantus.core.extractor.IExtractorInputReader;
import org.spantus.mpeg7.config.Mpeg7ExtractorConfig;
import org.spantus.mpeg7.extractors.Mpeg7ExtractorInputReader;

public abstract class Mpeg7ExtractorUtils {
	public static void register(IExtractorInputReader reader, Mpeg7ExtractorEnum extractor) {
		Mpeg7ExtractorInputReader mpeg7reader = (Mpeg7ExtractorInputReader) reader;
		Mpeg7ExtractorConfig config = (Mpeg7ExtractorConfig)mpeg7reader.getConfig();
		config.getExtractors().add(extractor.name());
	}
	public static void register(IExtractorInputReader reader,
			Mpeg7ExtractorEnum[] extractors) {
		for (Mpeg7ExtractorEnum extractor : extractors) {
			register(reader, extractor);
		}
	}



}

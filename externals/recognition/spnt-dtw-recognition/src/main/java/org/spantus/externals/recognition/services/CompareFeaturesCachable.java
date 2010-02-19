package org.spantus.externals.recognition.services;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.spantus.core.extractor.IExtractorInputReader;

public class CompareFeaturesCachable extends CompareFeatures{
	Map<File, IExtractorInputReader> cache;
	
	@Override
	protected IExtractorInputReader getExtractorInputReader(File readerFile){
		IExtractorInputReader reader = getCache().get(readerFile);
		if(reader==null){
			reader = super.getExtractorInputReader(readerFile);
			getCache().put(readerFile, reader);
		}
		return reader;
	}
	
	public Map<File, IExtractorInputReader> getCache() {
		if(cache == null){
			cache = new HashMap<File, IExtractorInputReader>();
		}
		return cache;
	}

}

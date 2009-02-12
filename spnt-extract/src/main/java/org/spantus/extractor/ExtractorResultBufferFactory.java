package org.spantus.extractor;

import org.spantus.core.extractor.IExtractor;
import org.spantus.core.extractor.IExtractorVector;
import org.spantus.core.extractor.IGeneralExtractor;

public abstract class ExtractorResultBufferFactory {
	
	public static IGeneralExtractor create(IGeneralExtractor extractor){
		if(extractor instanceof IExtractor){
			return create((IExtractor)extractor);
		}else if(extractor instanceof IExtractorVector){
			return create((IExtractorVector)extractor);
		}
		return null;
		
	}
	
	public static ExtractorResultBuffer create(IExtractor extractor){
		return new ExtractorResultBuffer(extractor);
	}
	public static ExtractorResultBuffer3D create(IExtractorVector extractor){
		return new ExtractorResultBuffer3D(extractor);
	}
}

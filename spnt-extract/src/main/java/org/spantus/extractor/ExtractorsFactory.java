package org.spantus.extractor;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;

import org.spantus.core.extractor.IExtractorConfig;
import org.spantus.core.extractor.IExtractorInputReader;

public abstract class ExtractorsFactory {
	public static IExtractorInputReader createReader(AudioFormat format){
		ExtractorInputReader reader = new ExtractorInputReader();
		reader.setConfig(createConfig(format));
		return reader;
	}
	
	public static IExtractorConfig createConfig(AudioFormat format){
		return ExtractorConfigUtil.defaultConfig(format.getSampleRate(), 
				format.getSampleSizeInBits());
	}
	
	public static IExtractorInputReader createReader(AudioFileFormat format){
		return createReader(format.getFormat());
	}
	public static IExtractorInputReader createNormalizedReader(){
		return new ExtractorInputReader();
	}

}

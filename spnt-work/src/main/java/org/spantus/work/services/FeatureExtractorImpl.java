package org.spantus.work.services;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

import org.spantus.core.extractor.IExtractorInputReader;
import org.spantus.core.io.AudioFactory;
import org.spantus.core.io.AudioReader;
import org.spantus.extractor.ExtractorsFactory;
import org.spantus.extractor.impl.ExtractorEnum;
import org.spantus.extractor.impl.ExtractorUtils;

public class FeatureExtractorImpl implements FeatureExtractor{

	public void extract(ExtractorEnum[] extractors, File file) {
		URL urlFile;
		try {
			urlFile = file.toURI().toURL();
		} catch (MalformedURLException e) {
			throw new RuntimeException(e);
		}
		AudioReader reader = AudioFactory.createAudioReader();
		IExtractorInputReader bufferedReader = ExtractorsFactory.createReader(reader.getAudioFormat(urlFile));
		ExtractorUtils.register(bufferedReader, extractors);
		reader.readAudio(urlFile, bufferedReader);
		WorkServiceFactory.createReaderDao().write(bufferedReader, createExtactorFile(file));
	}
	
	protected File createExtactorFile(File wavFile){
		File newFile = new File(wavFile.getAbsoluteFile().toString()+".sspnt.xml");
		return newFile;
	}

}

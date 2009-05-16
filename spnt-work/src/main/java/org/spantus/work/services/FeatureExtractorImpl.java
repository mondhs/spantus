package org.spantus.work.services;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

import org.spantus.core.extractor.IExtractor;
import org.spantus.core.extractor.IExtractorInputReader;
import org.spantus.core.extractor.IExtractorVector;
import org.spantus.core.extractor.IGeneralExtractor;
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
		ExtractorUtils.register(bufferedReader, extractors, null);
		reader.readAudio(urlFile, bufferedReader);
		WorkServiceFactory.createReaderDao().write(bufferedReader, createExtactorFile(file));
	}
	/**
	 * 
	 * @param name
	 * @param reader
	 * @return
	 */
	public IGeneralExtractor findExtractorByName(String name, IExtractorInputReader reader){
		for (IExtractor extractor : reader.getExtractorRegister()) {
			if(extractor.getName().contains(name)){
				return extractor;
			}
		}
		for (IExtractorVector extractor : reader.getExtractorRegister3D()) {
			if(extractor.getName().contains(name)){
				return extractor;
			}
		}
		return null;
	}
	
	protected File createExtactorFile(File wavFile){
		File newFile = new File(wavFile.getAbsoluteFile().toString()+".sspnt.xml");
		return newFile;
	}

}

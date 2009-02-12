package org.spnt.recognition.dtw.exec;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

import org.spantus.core.FrameVectorValues;
import org.spantus.core.extractor.IExtractorInputReader;
import org.spantus.core.io.AudioFactory;
import org.spantus.core.io.AudioReader;
import org.spantus.extractor.ExtractorsFactory;
import org.spantus.extractor.impl.ExtractorEnum;
import org.spantus.extractor.impl.ExtractorUtils;
import org.spnt.recognition.dtw.learn.LearnModelService;
import org.spnt.recognition.dtw.learn.LearnModelServiceImpl;

public class LearnMain {
	
	public void process(){
		LearnModelService learnModelService = 
			new LearnModelServiceImpl();
		learnModelService.learn(getVals(), "du");

	}
	
	public static FrameVectorValues getVals() {
		File wavFile = new File("../../../data/test/du.wav");
		URL urlFile;
		try {
			urlFile = wavFile.toURI().toURL();
			AudioReader reader = AudioFactory.createAudioReader();
			IExtractorInputReader bufferedReader = ExtractorsFactory
					.createReader(reader.getAudioFormat(urlFile));
			ExtractorUtils
					.register(bufferedReader, ExtractorEnum.LPC_EXTRACTOR);
			reader.readAudio(urlFile, bufferedReader);
			return bufferedReader.getExtractorRegister3D().iterator().next()
					.getOutputValues();
		} catch (MalformedURLException e) {
			throw new RuntimeException(e);
		}
	}
	
	public static void main(String[] args) {
		LearnMain main = new LearnMain();
		main.process();
	}
}

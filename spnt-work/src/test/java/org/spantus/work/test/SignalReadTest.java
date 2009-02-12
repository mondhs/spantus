package org.spantus.work.test;


import java.io.File;
import java.io.IOException;
import java.net.URL;

import javax.sound.sampled.UnsupportedAudioFileException;

import junit.framework.TestCase;

import org.spantus.core.extractor.IExtractorInputReader;
import org.spantus.core.io.AudioFactory;
import org.spantus.core.io.AudioReader;
import org.spantus.extractor.ExtractorResultBuffer;
import org.spantus.extractor.ExtractorsFactory;
import org.spantus.extractor.impl.SignalExtractor;

public class SignalReadTest extends TestCase {

	public void testReadSignal() throws UnsupportedAudioFileException,
			IOException {
		File wavFile = new File("../data/text1.wav");
		URL urlFile = wavFile.toURI().toURL();
		AudioReader reader = AudioFactory.createAudioReader();

		IExtractorInputReader bufferedReader = ExtractorsFactory.createReader(reader
				.getAudioFormat(urlFile));
		
		ExtractorResultBuffer signal = new ExtractorResultBuffer(
				new SignalExtractor());



		bufferedReader.registerExtractor(signal);


	}
}

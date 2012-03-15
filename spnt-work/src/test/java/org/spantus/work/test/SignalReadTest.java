package org.spantus.work.test;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import javax.sound.sampled.UnsupportedAudioFileException;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.spantus.core.extractor.IExtractorInputReader;
import org.spantus.core.io.AudioReader;
import org.spantus.core.io.AudioReaderFactory;
import org.spantus.extractor.ExtractorResultBuffer;
import org.spantus.extractor.ExtractorsFactory;
import org.spantus.extractor.impl.SignalExtractor;

public class SignalReadTest {

	private File wavFile = new File("../data/text1.wav");
	private AudioReader audioReader;

	@Before
	public void onSetup() {
		audioReader = AudioReaderFactory.createAudioReader();
	}

	@Test
	public void testReadSignal() throws UnsupportedAudioFileException,
			IOException {
		// given
		URL urlFile = wavFile.toURI().toURL();

		// when
		IExtractorInputReader extractorReader = ExtractorsFactory
				.createReader(audioReader.getAudioFormat(urlFile));

		ExtractorResultBuffer signal = new ExtractorResultBuffer(
				new SignalExtractor());

		extractorReader.registerExtractor(signal);

		// then
		Assert.assertEquals(1, extractorReader.getExtractorRegister().size());
	}
}

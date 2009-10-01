package org.spantus.serial.test;

import javax.sound.sampled.AudioFormat;

import org.spantus.core.WraperExtractorReader;
import org.spantus.core.extractors.ExtractorsFactory;
import org.spantus.core.extractors.IExtractorInputReader;
import org.spantus.core.extractors.impl.ExtractorEnum;
import org.spantus.core.extractors.impl.ExtractorUtils;
import org.spantus.core.io.AudioCapture;
import org.spantus.logger.Logger;
import org.spantus.serial.OutputStaticThresholdSerial;

public class SignalMonitor{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	Logger log = Logger.getLogger(getClass());
	AudioCapture capture;

	public SignalMonitor() {
		IExtractorInputReader reader = ExtractorsFactory
				.createReader(getFormat());
		OutputStaticThresholdSerial threshold = new OutputStaticThresholdSerial();
		threshold.setCoef(2f);
		ExtractorUtils.registerThreshold(reader,
				ExtractorEnum.SMOOTHED_ENERGY_EXTRACTOR, 
				threshold);
		capture = new AudioCapture(new WraperExtractorReader(reader));
		capture.setFormat(getFormat());

	}
	public void start(){
		capture.start();
	}

	public AudioFormat getFormat() {
		float sampleRate = 8000;
		int sampleSizeInBits = 16;
		int channels = 1;
		boolean signed = true;
		boolean bigEndian = true;
		return new AudioFormat(sampleRate, sampleSizeInBits, channels, signed,
				bigEndian);
	}

	public static void main(String[] args) {
		System.out.println(System.getProperty("java.library.path"));
		SignalMonitor serialMonitor = new SignalMonitor();
		serialMonitor.start();
	}


}

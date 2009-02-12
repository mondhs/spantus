package org.spantus.work.test;

import java.io.File;
import java.io.IOException;

import javax.sound.sampled.UnsupportedAudioFileException;

import junit.framework.TestCase;

import org.spantus.extractor.impl.ExtractorEnum;
import org.spantus.work.services.FeatureExtractor;
import org.spantus.work.services.FeatureExtractorImpl;

public class FeatureExtractorTest extends TestCase {

	public void testReadSignal() throws UnsupportedAudioFileException,
			IOException {
		File wavFile = new File("../data/t_1_2.wav");
		FeatureExtractor extractor = new FeatureExtractorImpl();
		extractor.extract(new ExtractorEnum[] { 
				ExtractorEnum.ENERGY_EXTRACTOR,
//				ExtractorEnum.AUTOCORRELATION_EXTRACTOR,
//				ExtractorEnum.CROSSING_ZERO_EXTRACTOR,
//				ExtractorEnum.ENVELOPE_EXTRACTOR,
//				ExtractorEnum.LOG_ATTACK_TIME,
//				ExtractorEnum.LOUDNESS_EXTRACTOR,
//				ExtractorEnum.LPC_EXTRACTOR,
//				ExtractorEnum.MFCC_EXTRACTOR,
//				ExtractorEnum.SIGNAL_ENTROPY_EXTRACTOR,
//				ExtractorEnum.SMOOTHED_ENERGY_EXTRACTOR,
//				ExtractorEnum.SPECTRAL_CENTROID_EXTRACTOR,
//				ExtractorEnum.SPECTRAL_ENTROPY_EXTRACTOR,
//				ExtractorEnum.SPECTRAL_FLUX_EXTRACTOR,
		}, wavFile);
	}
}

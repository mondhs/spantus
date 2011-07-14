/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.spantus.exp.recognition;

import java.io.File;

import org.junit.Before;
import org.junit.Test;
import org.spantus.core.marker.MarkerSetHolder;
import org.spantus.exp.recognition.AbstractSegmentDirTest.TextGridNameFilter;
import org.spantus.externals.recognition.services.impl.CorpusEntryExtractorTextGridMapImpl;
import org.spantus.extractor.impl.ExtractorEnum;
import org.spantus.logger.Logger;
import org.spantus.utils.FileUtils;

/**
 * 
 * @author mondhs
 */
public class QSegmentPhonomeMappingDirTest extends AbstractSegmentDirTest {

	private static final Logger log = Logger
			.getLogger(QSegmentPhonomeMappingDirTest.class);
	private File wavDir = new File(DIR_LEARN_WAV, "WAV/AK1/");
	private File markerDir = new File(DIR_LEARN_WAV, "GRID/TRAIN/");

	@Before
	public void onSetup() {
		CorpusEntryExtractorTextGridMapImpl impl = new CorpusEntryExtractorTextGridMapImpl();
		impl.setMarkerDir(getMarkerDir());

		ExtractorEnum[] extractors = new ExtractorEnum[] {
				ExtractorEnum.MFCC_EXTRACTOR,
				ExtractorEnum.PLP_EXTRACTOR,
				ExtractorEnum.LPC_EXTRACTOR,
				// ExtractorEnum.FFT_EXTRACTOR,
				ExtractorEnum.LOUDNESS_EXTRACTOR,
				ExtractorEnum.SPECTRAL_FLUX_EXTRACTOR, 
				ExtractorEnum.SIGNAL_ENTROPY_EXTRACTOR };

		impl.setExtractors(extractors);
		
		setExtractor(impl);
		
		super.onSetup();
	}

	@Test
	public void testClassify() throws Exception {
		clearCorpus();
		int counter = 0;
		int size = getMarkerDir().listFiles(new TextGridNameFilter()).length;
		for (File filePath : getMarkerDir().listFiles(new TextGridNameFilter())) {
			counter++;
			log.error("[testClassify]Processing "+ counter + " from " + size);
//			String markersPath = FileUtils.replaceExtention(filePath,
//					".TextGrid");
			// FileUtils.replaceExtention(filePath,".mspnt.xml");
			File wavFile = new File(getWavDir(), FileUtils.replaceExtention(
					filePath, ".wav"));
//			 if(!filePath.getName().contains("far1")){
//			 continue;
//			 }
			log.debug("[testClassify]reading: {0}", filePath);
			MarkerSetHolder markerSetHolder = getExtractor().extractAndLearn(
					wavFile.getAbsoluteFile());

			log.debug("accept: {0}:{1}", filePath, markerSetHolder);
		}

	}

	public File getWavDir() {
		return wavDir;
	}

	public void setWavDir(File wavDir) {
		this.wavDir = wavDir;
	}

	public File getMarkerDir() {
		return markerDir;
	}

	public void setMarkerDir(File markerDir) {
		this.markerDir = markerDir;
	}

}

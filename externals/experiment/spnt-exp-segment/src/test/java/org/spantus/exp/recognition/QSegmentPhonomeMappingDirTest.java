/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.spantus.exp.recognition;

import java.io.File;

import org.junit.Before;
import org.junit.Test;
import org.spantus.core.marker.MarkerSetHolder;
import org.spantus.externals.recognition.services.impl.CorpusEntryExtractorTextGridMapImpl;
import org.spantus.extractor.impl.ExtractorEnum;
import org.spantus.logger.Logger;
import org.spantus.utils.FileUtils;
import org.spantus.work.services.WorkServiceFactory;

/**
 * 
 * @author mondhs
 */
public class QSegmentPhonomeMappingDirTest extends AbstractSegmentDirTest {

	private static final Logger log = Logger
			.getLogger(QSegmentPhonomeMappingDirTest.class);
	private File wavDir = new File(DIR_LEARN_WAV, "WAV/AK1/");
	private File markerDir = new File(DIR_LEARN_WAV, "GRID/AK1/");

	  @Before
	public void onSetup() {
		  CorpusEntryExtractorTextGridMapImpl impl = new CorpusEntryExtractorTextGridMapImpl();
		  impl.setMarkerDir(markerDir);
		  setExtractor(impl);
		  
		  super.onSetup();
	        ExtractorEnum[] extractors = new ExtractorEnum[]{
//	                ExtractorEnum.MFCC_EXTRACTOR,
//	                ExtractorEnum.PLP_EXTRACTOR,
//	                ExtractorEnum.LPC_EXTRACTOR,
//	                ExtractorEnum.FFT_EXTRACTOR,
	        		ExtractorEnum.LOUDNESS_EXTRACTOR,
	                ExtractorEnum.SPECTRAL_FLUX_EXTRACTOR,
	                ExtractorEnum.SIGNAL_ENTROPY_EXTRACTOR};

	        getExtractor().setExtractors(extractors);

		  
	}
	
	@Test
	public void testClassify() {
		clearCorpus();

		for (File filePath : wavDir.listFiles(new WavFileNameFilter())) {
			String markersPath = FileUtils.stripExtention(filePath);
//	        if(!markersPath.contains("far1")){
//	        	continue;
//	        }
	        markersPath += ".mspnt.xml";
			log.debug("[testClassify]reading: {0}", filePath);
	        MarkerSetHolder markerSetHolder = getExtractor().extractAndLearn(filePath.getAbsoluteFile());
	        WorkServiceFactory.createMarkerDao().write(markerSetHolder, new File(markerDir, markersPath));

			log.debug("accept: {0}:{1}", filePath, markerSetHolder);
		}

	}

}

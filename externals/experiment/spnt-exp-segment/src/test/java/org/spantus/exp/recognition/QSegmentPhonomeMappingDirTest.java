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
import org.spantus.logger.Logger;

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
		  
	}
	
	@Test
	public void testClassify() {
		clearCorpus();

		for (File filePath : wavDir.listFiles(new WavFileNameFilter())) {
			MarkerSetHolder markerSetHolder = getExtractor().extractAndLearn(filePath.getAbsoluteFile());
			log.debug("reading", filePath);
			log.debug("accept: {0}:{1}", filePath, markerSetHolder);
		}

	}

}

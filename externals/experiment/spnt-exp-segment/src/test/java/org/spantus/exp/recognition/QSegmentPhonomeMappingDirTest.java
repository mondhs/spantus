/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.spantus.exp.recognition;

import java.io.File;
import java.io.FilenameFilter;
import java.util.HashMap;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.spantus.core.extractor.ExtractorParam;
import org.spantus.core.marker.MarkerSetHolder;
import org.spantus.core.wav.AudioManager;
import org.spantus.core.wav.AudioManagerFactory;
import org.spantus.exp.recognition.multi.MultiMapper;
import org.spantus.externals.recognition.services.impl.CorpusEntryExtractorTextGridMapImpl;
import org.spantus.extractor.impl.ExtractorEnum;
import org.spantus.extractor.impl.ExtractorModifiersEnum;
import org.spantus.logger.Logger;
import org.spantus.utils.ExtractorParamUtils;
import org.spantus.utils.FileUtils;

import com.thoughtworks.xstream.mapper.Mapper;

/**
 * 
 * @author mondhs
 */
public class QSegmentPhonomeMappingDirTest  {

	private static final Logger log = Logger
			.getLogger(QSegmentPhonomeMappingDirTest.class);

	public final static String ROOT_DIR = "/home/mgreibus/src/garsynai/VDU/VDU_TRI4";

	private MultiMapper mapper;
	
	

	@Before
	public void onSetup() {
		mapper = new MultiMapper();
	}
	
	protected void init(MultiMapper mapper, String corpusName) {
		mapper.init(
				new File(ROOT_DIR, "TEST/"+ corpusName +"/"), 
				new File(ROOT_DIR, "TRAIN/"+ corpusName +"/"),
				new File(ROOT_DIR, "CORPUS/"+ corpusName +"/"), 
				new File(ROOT_DIR, "WAV/"+ corpusName +"/"), 
				new TextGridNameFilter(),
				corpusName);
	}
	

	@Test @Ignore
	public void testClassifyAK1() throws Exception {
		//given
		init(mapper,  "AK1");
		
		mapper.clearCorpus();
		mapper.extractAndLearn();
	}
	
	@Test @Ignore
	public void testClassifyBJ1() throws Exception {
		//given
		init(mapper,  "BJ1");
		mapper.clearCorpus();
		mapper.extractAndLearn();
	}

	@Test @Ignore
	public void testClassifyLK1() throws Exception {
		//given
		init(mapper,  "LK1");
		
		mapper.clearCorpus();
		mapper.extractAndLearn();
	}
	
	@Test  @Ignore
	public void testClassifyTK1() throws Exception {
		//given
		init(mapper,  "TK1");
		
		mapper.clearCorpus();
		mapper.extractAndLearn();
	}

	
}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.spantus.exp.recognition;

import java.io.File;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.spantus.exp.ExpConfig;
import org.spantus.exp.recognition.multi.MultiMapper;
import org.spantus.logger.Logger;

/**
 * 
 * @author mondhs
 */
public class QSegmentPhonomeRecognitionDirTest  {

	private static final Logger log = Logger
			.getLogger(QSegmentPhonomeRecognitionDirTest.class);
	
	private MultiMapper mapper;
	
	@Before
	public void onSetup() {
		mapper = new MultiMapper();
	}
	
	protected void init(MultiMapper mapper, String corpusName) {
		mapper.init(
//				new File(QSegmentPhonomeMappingDirTest.ROOT_DIR, "TEST/"+ corpusName +"/"), 
//				new File(QSegmentPhonomeMappingDirTest.ROOT_DIR, "TRAIN/"+ corpusName +"/"),
//				new File(QSegmentPhonomeMappingDirTest.ROOT_DIR, "CORPUS/"+ corpusName +"/"), 
//				new File(QSegmentPhonomeMappingDirTest.ROOT_DIR, "WAV/"+ corpusName +"/"), 
				ExpConfig.createConfig(),
				new TextGridNameFilter(),
				corpusName);
	}
	
	@After
	public void destroy() {
		mapper.destroy();
	}
	
	@Test 
	public void testRecognizeAK1() throws Exception {
			//given
			init(mapper,  "AK1");
			mapper.recognize();
	}
	@Test 
	public void testRecognizeBJ1() throws Exception {
			//given
			init(mapper,  "BJ1");
			mapper.recognize();
	}
	@Test 
	public void testRecognizeLK1() throws Exception {
			//given
			init(mapper,  "LK1");
			mapper.recognize();
	}
	@Test 
	public void testRecognizeTK1() throws Exception {
			//given
			init(mapper,  "TK1");
			mapper.recognize();
	}


}

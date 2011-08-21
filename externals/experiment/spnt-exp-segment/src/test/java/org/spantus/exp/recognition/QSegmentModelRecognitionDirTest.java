/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.spantus.exp.recognition;

import java.io.File;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.spantus.exp.recognition.multi.ModelMultiMapper;
import org.spantus.logger.Logger;

/**
 * 
 * @author mondhs
 */
public class QSegmentModelRecognitionDirTest  {

	private static final Logger log = Logger
			.getLogger(QSegmentModelRecognitionDirTest.class);
	
	private ModelMultiMapper mapper;
	
	@Before
	public void onSetup() {
		mapper = new ModelMultiMapper();
	}
	
	public final static String ROOT_DIR = "/home/mgreibus/tmp/garsyno.modelis";
	
	protected void init(ModelMultiMapper mapper, String corpusName) {
		mapper.init(
				new File(ROOT_DIR, "TEST/"), 
				new File(ROOT_DIR, "TRAIN/"),
				new File(ROOT_DIR, "CORPUS/"), 
				null, 
				new ExtNameFilter("txt"),
				corpusName);
	}
	
	@After
	public void destroy() {
		mapper.destroy();
	}
	
	@Test 
	public void testRecognize() throws Exception {
			//given
			init(mapper,  "AK1");
			mapper.recognize();
	}


}

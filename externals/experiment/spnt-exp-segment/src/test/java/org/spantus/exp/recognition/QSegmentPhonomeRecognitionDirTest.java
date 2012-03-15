/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.spantus.exp.recognition;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.spantus.exp.ExpConfig;
import org.spantus.exp.recognition.multi.MultiMapper;
import org.spantus.logger.Logger;

/**
 * 
 * @author mondhs
 */
public class QSegmentPhonomeRecognitionDirTest  {

	@SuppressWarnings("unused")
	private static final Logger LOG = Logger
			.getLogger(QSegmentPhonomeRecognitionDirTest.class);
	
	private MultiMapper mapper;
	
	@Before
	public void onSetup() {
		mapper = new MultiMapper();
	}
	
	protected void init(MultiMapper mapper, String corpusName) {
		mapper.init(
				ExpConfig.createConfig(),
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

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.spantus.exp.recognition;

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
		ExpConfig config = ExpConfig.createConfig();
		config.setMarkerFilePrefix(".TextGrid");
		mapper.init(
				config,
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

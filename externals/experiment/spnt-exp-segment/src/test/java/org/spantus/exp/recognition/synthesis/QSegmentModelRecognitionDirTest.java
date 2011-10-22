/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.spantus.exp.recognition.synthesis;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.spantus.exp.ExpConfig;
import org.spantus.exp.recognition.ExtNameFilter;
import org.spantus.exp.recognition.dao.ResultOdsDao;
import org.spantus.exp.recognition.multi.ModelMultiMapper;
import org.spantus.logger.Logger;

/**
 * 
 * @author mondhs
 */
public class QSegmentModelRecognitionDirTest {

	private static final Logger log = Logger
			.getLogger(QSegmentModelRecognitionDirTest.class);

	private ModelMultiMapper mapper;

	@Before
	public void onSetup() {
		mapper = new ModelMultiMapper();
	}

	public final static String ROOT_DIR = "/home/as/tmp/garsyno.modelis";

	protected void init(ModelMultiMapper mapper, String corpusName) {
		mapper.init(ExpConfig.createConfig(), new ExtNameFilter("txt"),
				corpusName);
	}

	@After
	public void destroy() {
		mapper.destroy();
	}

	@Test @Ignore
	public void testRecognize() throws Exception {
		// given
		init(mapper, "AK1");
		mapper.recognize();
	}

	@Test
	public void testGenerateReport() throws Exception {
		// given
		init(mapper, "AK1");
		mapper.setRecreate(false);
		StringBuilder result = mapper.getqSegmentExpDao().generateReport("500,500,500,500,500,2500");
		ResultOdsDao dao = new ResultOdsDao();
		dao.save(result);
	}
}

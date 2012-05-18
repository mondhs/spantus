/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.spantus.exp.recognition.synthesis;

import java.io.File;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.odftoolkit.simple.SpreadsheetDocument;
import org.spantus.core.threshold.ClassifierEnum;
import org.spantus.exp.ExpConfig;
import org.spantus.exp.recognition.dao.ChartJFreeDao;
import org.spantus.exp.recognition.dao.QSegmentExpHsqlDao;
import org.spantus.exp.recognition.dao.ResultOdsDao;
import org.spantus.exp.recognition.multi.ModelMultiMapper;
import org.spantus.extractor.impl.ExtractorModifiersEnum;
import org.spantus.segment.SegmentFactory.SegmentatorServiceEnum;

/**
 * 
 * @author mondhs
 */
public class SytheticRecognitionDirExp {

//	private static final Logger log = Logger
//			.getLogger(SytheticRecognitionDirExp.class);

	private ModelMultiMapper mapper;

	@Before
	public void onSetup() {
		mapper = new ModelMultiMapper();
	}

	public final static String ROOT_DIR = 
//			"/home/as/tmp/garsyno.balsas1"//natural speech
			"/home/as/tmp/garsyno.modelis-en"//what is the time en
//			"/home/as/tmp/garsyno.modelis4"//sintesied
//			"/home/as/tmp/garsyno.modelis1"//generated
			;

	protected void init(ModelMultiMapper mapper, String corpusName) {
		ExpConfig config = ExpConfig.createConfig();
		config.setRootPath(ROOT_DIR);
		config.setModifier(ExtractorModifiersEnum.smooth);
		config.setSegmentatorServiceType(SegmentatorServiceEnum.basic.name());
		
		config.setClassifier(ClassifierEnum.rulesOffline);
		
//		config.setClassifier(ClassifierEnum.offline);
//		config.setThreasholdCoef(.05D);

		
		
		mapper.init(config,
				corpusName);


	}

	@After @Ignore
	public void destroy() {
		mapper.destroy();
	}

	@Test 
	public void testRecognize() throws Exception {
		// given
		init(mapper, "AK1");
		mapper.recognize();
	}

	@Test
	public void testGenerateReport() throws Exception {

		// given
		String[] syllabels = new String[]
//				{"a", "e", ""}
//				{ "ga", "ma", "me", "na", "ne", "re",	"ta", "" }
				{"what", "is", "the", "time", "-"}
		;
		init(mapper, "AK1");
		mapper.setRecreate(false);
		((QSegmentExpHsqlDao)mapper.getqSegmentExpDao()).setAcceptThreshold(180);
		// then
		StringBuilder result = mapper.getqSegmentExpDao().generateReport(
				"500,500,500,500,500,2500", syllabels);
		// System.out.println(result);
		ResultOdsDao dao = new ResultOdsDao();
		File ods = new File("./target/data/results.ods");
		ods = dao.save(result, ods.getAbsolutePath());
	}

	@Test  @Ignore
	public void testDrawReport() throws Exception {
		// given
		init(mapper, "AK1");
		ChartJFreeDao chartDao = new ChartJFreeDao();
		mapper.setRecreate(false);
		File ods = new File("./target/data/results.ods");
		// ods = new ResultOdsDao().save(result,ods.getAbsolutePath());
		chartDao.draw(SpreadsheetDocument.loadDocument(ods));
	}
}

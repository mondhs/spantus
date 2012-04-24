package org.spantus.android.service.test;

import junit.framework.Assert;

import org.apache.log4j.Logger;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.spantus.android.service.ReaderSimpleJsonDao;
import org.spantus.core.extractor.IExtractorInputReader;
import org.spantus.core.test.DumyExtractorInputReader;

public class ReaderSimpleJsonDaoTest {
	
	private static final Logger LOG =Logger.getLogger(ReaderSimpleJsonDaoTest.class);

	ReaderSimpleJsonDao readerSimpleJsonDao;
	
	@Before
	public void onSetup() {
		readerSimpleJsonDao = new ReaderSimpleJsonDao();
	}
	
	@Test
	public void test_transfor() throws Exception {
		//given
		IExtractorInputReader reader = new DumyExtractorInputReader();
		reader.getExtractorRegister().add(DumyExtractorInputReader.createExtractor("test1"));
		reader.getExtractorRegister3D().add(DumyExtractorInputReader.createExtractorVector("test2"));
		//when
		JSONObject root = readerSimpleJsonDao.transfor(reader);
		//then
		LOG.debug("json: " + root.toString(0));
		Assert.assertEquals("File exists", 2, root.getJSONArray(ReaderSimpleJsonDao.EXTRACTORS).length());
		
	}

}

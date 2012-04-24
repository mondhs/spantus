package org.spantus.android.service.test;

import java.io.File;
import java.util.List;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.spantus.android.service.RecognitionResultDetailsJsonDao;
import org.spantus.core.beans.RecognitionResultDetails;

public class RecognitionResultDetailsJsonDaoTest {
	
	RecognitionResultDetailsJsonDao recognitionResultDetailsJsonDao;
	File file;
	@Before
	public void onSetup() {
		recognitionResultDetailsJsonDao = new RecognitionResultDetailsJsonDao();
		file = new File("./target/test-classes/response.json");
	}
	
	@Test
	public void test_transfor() throws Exception {
		//given
		//when
		List<RecognitionResultDetails> result = recognitionResultDetailsJsonDao.read(file);
		//then
		Assert.assertEquals("size",3, result.size());
		Assert.assertEquals("1st item","du", result.get(0).getInfo().getMarker().getLabel());
		Assert.assertEquals("2nd item","vienas", result.get(1).getInfo().getMarker().getLabel());
		Assert.assertEquals("3rd item","trys", result.get(2).getInfo().getMarker().getLabel());
	}

}

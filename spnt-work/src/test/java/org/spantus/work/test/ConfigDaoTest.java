package org.spantus.work.test;

import java.io.File;

import org.spantus.core.extractor.DefaultExtractorConfig;
import org.spantus.core.extractor.ExtractorParam;
import org.spantus.core.extractor.IExtractorConfig;
import org.spantus.utils.ExtractorParamUtils;
import org.spantus.work.services.ConfigDao;
import org.spantus.work.services.ConfigPropertiesDao;

import junit.framework.TestCase;

public class ConfigDaoTest extends TestCase {
	
	private ConfigDao configDao;
	public final static String FILE_NAME = "./target/test-classes/config.properties";
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		configDao = new ConfigPropertiesDao();
	}
	
	public void testRead(){
		IExtractorConfig config = configDao.read(new File(FILE_NAME));
		assertEquals(8000F, config.getSampleRate());
		ExtractorParam param = config.getParameters().get(DefaultExtractorConfig.class.getName());
		
		Float threshold_coef = ExtractorParamUtils.<Float>getValue(param,
				ConfigPropertiesDao.key_threshold_coef);
		Long threshold_leaningPeriod = ExtractorParamUtils.<Long>getValue(
				param,
				ConfigPropertiesDao.key_threshold_leaningPeriod);
		assertEquals(6F, threshold_coef);
		assertEquals(5000, threshold_leaningPeriod.longValue());
	}
}

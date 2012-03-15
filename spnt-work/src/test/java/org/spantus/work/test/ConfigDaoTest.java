package org.spantus.work.test;

import java.io.File;

import junit.framework.TestCase;

import org.spantus.core.extractor.DefaultExtractorConfig;
import org.spantus.core.extractor.ExtractorParam;
import org.spantus.core.extractor.IExtractorConfig;
import org.spantus.utils.ExtractorParamUtils;
import org.spantus.work.services.ConfigDao;
import org.spantus.work.services.ConfigPropertiesDao;

public class ConfigDaoTest extends TestCase {
	
	private ConfigDao configDao;
	public final static String FILE_NAME = "./target/test-classes/config.properties";
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		configDao = new ConfigPropertiesDao();
	}
	
	public void testRead(){
		//given
		File file = new File(FILE_NAME);
		//when
		IExtractorConfig config = configDao.read(file);
		ExtractorParam param = config.getParameters().get(DefaultExtractorConfig.class.getName());
		
		//then
		Double threshold_coef = ExtractorParamUtils.<Double>getValue(param,
				ConfigPropertiesDao.key_threshold_coef);
		String classifier = 	ExtractorParamUtils.<String>getValue(param,
				ConfigPropertiesDao.key_segmentation_classifier);

		Boolean smootheModifier  = ExtractorParamUtils.<Boolean>getValue(param,
				ConfigPropertiesDao.key_segmentation_modifier_smooth);
//		Boolean meanModifier  = ExtractorParamUtils.<Boolean>getValue(param,
//				ConfigPropertiesDao.key_segmentation_modifier_mean);
		
		
		Long threshold_leaningPeriod = ExtractorParamUtils.<Long>getValue(
				param,
				ConfigPropertiesDao.key_threshold_leaningPeriod);
		
		assertEquals("rulesOnline", classifier);
		assertEquals(Boolean.TRUE, smootheModifier);
		assertEquals(6D, threshold_coef);
		assertEquals(5000, threshold_leaningPeriod.longValue());
		assertEquals(123, config.getWindowOverlap());
		assertEquals(363, config.getWindowSize());
	}
}

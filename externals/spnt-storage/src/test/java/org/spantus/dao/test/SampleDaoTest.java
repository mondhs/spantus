package org.spantus.dao.test;

import java.util.List;

import org.spantus.dao.SampleDao;
import org.spantus.domain.Sample;
import org.springframework.test.AbstractDependencyInjectionSpringContextTests;

public class SampleDaoTest extends AbstractDependencyInjectionSpringContextTests{
	
	SampleDao sampleDao;
	
	public void setSampleDao(SampleDao sampleDao) {
		this.sampleDao = sampleDao;
	}

	
	protected String[] getConfigLocations() {
		return new String[] {"classpath:/META-INF/spring/*-beans.xml"};
	}
	
	public void testUserDao(){
		Sample sample = createSample();
		sample = sampleDao.store(sample);
		assertNotNull("After save id should be set", sample.getId());
		Sample sample2 = sampleDao.findById(sample.getId());
		assertNotNull(sample2);
		List<Sample> samples = sampleDao.findAll();
		assertEquals(1, samples.size());
	}
	
	private Sample createSample(){
		Sample sample = new Sample();
		sample.setFileName("first.wav");
		return sample;
	}
	

}

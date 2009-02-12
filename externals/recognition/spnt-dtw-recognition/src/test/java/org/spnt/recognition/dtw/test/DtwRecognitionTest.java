package org.spnt.recognition.dtw.test;

import junit.framework.TestCase;

import org.spnt.recognition.dtw.RecognitionServiceDtwImpl;

public class DtwRecognitionTest extends TestCase {
	
	
	Float[] targetArr = new Float[]{1f, 2f, 3f, 4f, 5f};
	
	RecognitionServiceDtwImpl recognition;
	SampleRepositoryTestImpl sampleRepository;
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		recognition = new RecognitionServiceDtwImpl();
		sampleRepository = new SampleRepositoryTestImpl();
		recognition.setSampleRepository(sampleRepository);
	}
	
	public void testMatch(){
		String match = recognition.match(sampleRepository.getVals(targetArr));
		assertEquals("test2", match);
	}
	
	
	
}

package org.spantus.work.test;

import java.io.File;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.spantus.core.extractor.IExtractorInputReader;
import org.spantus.extractor.impl.ExtractorEnum;
import org.spantus.work.services.impl.WorkExtractorReaderServiceImpl;

public class ExtractorReaderServiceImplTest {

	private WorkExtractorReaderServiceImpl extractor;

	@Before
	public void setup() {
		extractor = new WorkExtractorReaderServiceImpl();
	}

	@Test
	public void testReadSignal() {
		// given
		File wavFile = new File("../data/t_1_2.wav");

		// when
		IExtractorInputReader reader = extractor.createReaderAndSave(
				new ExtractorEnum[] { ExtractorEnum.ENERGY_EXTRACTOR, },
				wavFile);

		// then
		Assert.assertEquals(1, reader.getExtractorRegister().size());
	}
}

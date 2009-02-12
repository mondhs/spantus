package org.spantus.work.mpeg7.test;

import java.io.File;
import java.net.MalformedURLException;

import org.spantus.core.extractor.IExtractorConfig;
import org.spantus.exception.ProcessingException;
import org.spantus.mpeg7.Mpeg7ExtractorEnum;
import org.spantus.mpeg7.config.Mpeg7ConfigUtil;
import org.spantus.mpeg7.extractors.Mpeg7ExtractorInputReader;
import org.spantus.mpeg7.io.Mpeg7Factory;

public class Mpeg7Write  {
	public void testWriteMpeg7() throws ProcessingException, MalformedURLException{
		IExtractorConfig config = Mpeg7ConfigUtil.createConfig(Mpeg7ExtractorEnum.values());
		Mpeg7ExtractorInputReader reader = new Mpeg7ExtractorInputReader();
		reader.setConfig(config);
		Mpeg7Factory.createMpeg7Writer().write((new File("./target/test-classes/text1.wav")).toURI().toURL(),
				(new File("../data/text1.mpeg7.xml")),
				reader.getConfig());
	}

}

package org.spantus.core.reader.test;

import java.io.File;
import java.net.URL;

import junit.framework.TestCase;

import org.spantus.core.extractor.SignalFormat;
import org.spantus.core.extractors.test.DumyExtractorInputReader;
import org.spantus.core.io.SignalReader;
import org.spantus.core.io.SimpleSignalReader;

public class SimpleReaderTest extends TestCase {
	private URL inputFile; 
	private URL notSupported; 
	private SignalReader signalReader;
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		inputFile = new File("../data/Ant1.txt").toURI().toURL();
		notSupported = new File("../data/t_1_2.wav").toURI().toURL();
		signalReader = new SimpleSignalReader();
	}
	public void testGetFormat(){
		SignalFormat signalFormat =  signalReader.getFormat(inputFile);
		assertNotNull(signalFormat);
		assertEquals(1.3D, signalFormat.getSampleRate());
		assertEquals(414D, signalFormat.getLength());
	}
	public void testIsSuported(){
		assertTrue(signalReader.isFormatSupported(inputFile));
		assertFalse(signalReader.isFormatSupported(notSupported));
	}
	
	public void testRead(){
		SignalFormat signalFormat =  signalReader.getFormat(inputFile);
		DumyExtractorInputReader reader = new DumyExtractorInputReader();
		signalReader.readSignal(inputFile, reader);
		assertTrue(signalFormat.getLength()== reader.getWindow().size());
	}
}

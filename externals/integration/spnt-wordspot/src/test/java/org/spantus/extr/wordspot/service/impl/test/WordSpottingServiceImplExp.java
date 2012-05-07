package org.spantus.extr.wordspot.service.impl.test;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.spantus.core.beans.SignalSegment;
import org.spantus.extr.wordspot.service.impl.WordSpottingListenerLogImpl;
import org.spantus.extr.wordspot.service.impl.WordSpottingServiceImpl;

public class WordSpottingServiceImplExp extends AbstractSegmentExtractorTest {

	WordSpottingServiceImpl wordSpottingServiceImpl;
	private File wavFile = new File("/home/as/tmp/garsynas.lietuvos-mg/TRAIN/"+"1.wav");
	private File repositoryPathRoot = new File("/home/as/tmp/garsynas.lietuvos-mg/CORPUS/");
	private File repositoryPathLevel1 = new File(repositoryPathRoot,"level1");
	private File repositoryPathLevel2 = new File(repositoryPathRoot,"level2");

	@Before
	public void setUp() throws Exception {
		super.setUp();
		wordSpottingServiceImpl = new WordSpottingServiceImpl();
		wordSpottingServiceImpl.setSegmentExtractorService(getSegmentExtractorService());
	}

	@Test
	public void test_wordSpotting() throws MalformedURLException {
		//given
		WordSpottingListenerLogImpl listener = new WordSpottingListenerLogImpl("Lietuvos", repositoryPathLevel2.getAbsolutePath());
		URL url = getWavFile().toURI().toURL() ;
		//when 
		wordSpottingServiceImpl.wordSpotting(url, listener);
		List<SignalSegment> segments = listener.getSignalSegments();
		//then
		Assert.assertEquals("One segment", 1, segments.size(),0);
	}

	public File getWavFile() {
		return wavFile;
	}

	public File getRepositoryPath() {
		return repositoryPathLevel1;
	}
}

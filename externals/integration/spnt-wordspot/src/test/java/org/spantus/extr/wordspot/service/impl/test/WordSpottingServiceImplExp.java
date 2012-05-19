package org.spantus.extr.wordspot.service.impl.test;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.spantus.core.beans.SignalSegment;
import org.spantus.core.threshold.ClassifierEnum;
import org.spantus.core.wav.AudioManagerFactory;
import org.spantus.extr.wordspot.domain.SegmentExtractorServiceConfig;
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
		wordSpottingServiceImpl = new WordSpottingServiceImpl(repositoryPathLevel1.getAbsolutePath());
		wordSpottingServiceImpl.setSegmentExtractorService(getSegmentExtractorService());
	}
	
	@Override
	protected void changeOtherParams(SegmentExtractorServiceConfig config) {
		super.changeOtherParams(config);
		getSegmentExtractorService().getConfig().setClassifier(ClassifierEnum.rulesOnline);
	}

	@Test
	public void test_wordSpotting() throws MalformedURLException {
		//given
		Long length = 1000L*AudioManagerFactory.createAudioManager().findLength(wavFile.toURI().toURL()).longValue();
		WordSpottingListenerLogImpl listener = new WordSpottingListenerLogImpl("Lietuvos", repositoryPathLevel2.getAbsolutePath());
		URL url = getWavFile().toURI().toURL() ;
		//when 
		long started = System.currentTimeMillis();
		wordSpottingServiceImpl.wordSpotting(url, listener);
		long ended= System.currentTimeMillis();
		List<SignalSegment> segments = listener.getSignalSegments();
		//then
		Assert.assertTrue("read time ", length > ended-started);
		Assert.assertEquals("One segment", 1, segments.size(),0);

	}

	public File getWavFile() {
		return wavFile;
	}

//	public File getRepositoryPath() {
//		return repositoryPathLevel1;
//	}
}

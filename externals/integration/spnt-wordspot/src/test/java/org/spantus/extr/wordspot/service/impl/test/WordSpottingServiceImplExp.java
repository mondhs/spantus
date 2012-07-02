package org.spantus.extr.wordspot.service.impl.test;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.spantus.core.beans.RecognitionResult;
import org.spantus.core.beans.SignalSegment;
import org.spantus.core.threshold.ClassifierEnum;
import org.spantus.core.wav.AudioManagerFactory;
import org.spantus.extr.wordspot.domain.SegmentExtractorServiceConfig;
import org.spantus.extr.wordspot.service.impl.WordSpottingListenerLogImpl;
import org.spantus.extr.wordspot.service.impl.SyllableSpottingServiceImpl;

public class WordSpottingServiceImplExp extends AbstractSegmentExtractorTest {

	SyllableSpottingServiceImpl wordSpottingServiceImpl;
	private File wavFile = new File("/home/as/tmp/garsynas.lietuvos-mg/TRAIN/","1.wav");
	private File repositoryPathRoot = new File("/home/as/tmp/garsynas.lietuvos-mg/CORPUS/");
	private File repositoryPathWord = new File(repositoryPathRoot,"word");
	private File repositoryPathSyllable = new File(repositoryPathRoot,"syllable");

	@Before
	public void setUp() throws Exception {
		super.setUp();
		wordSpottingServiceImpl = new SyllableSpottingServiceImpl(repositoryPathSyllable.getAbsolutePath());
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
		WordSpottingListenerLogImpl listener = new WordSpottingListenerLogImpl("Lietuvos", repositoryPathWord.getAbsolutePath());
		URL url = getWavFile().toURI().toURL() ;
		//when 
		long started = System.currentTimeMillis();
		wordSpottingServiceImpl.wordSpotting(url, listener);
		long ended= System.currentTimeMillis();
		List<RecognitionResult> segments = listener.getWordMatches();
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

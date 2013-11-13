package org.spantus.extr.wordspot.sphinx.service;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.MessageFormat;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spantus.core.beans.RecognitionResult;
import org.spantus.core.beans.SignalSegment;
import org.spantus.core.marker.Marker;
import org.spantus.extr.wordspot.service.SpottingListener;
import org.spantus.extr.wordspot.service.SpottingService;
import org.spantus.extr.wordspot.sphinx.linguist.language.grammar.NoSkipGrammar;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;

import edu.cmu.sphinx.frontend.util.AudioFileDataSource;
import edu.cmu.sphinx.recognizer.Recognizer;
import edu.cmu.sphinx.result.Result;
import edu.cmu.sphinx.result.WordResult;
import edu.cmu.sphinx.util.props.ConfigurationManager;
/**
 * 
 * @author Mindaugas Greibus
 *
 */
public class SpottingServiceSphinxImp implements SpottingService {
	
	private static final Logger LOG = LoggerFactory
			.getLogger(SpottingServiceSphinxImp.class);
	private static final String SPHINX_RESULT = "([\\wąčęėįšųūž]+)\\((\\d+\\.\\d+),(\\d+\\.\\d+)\\)";// family(43.72,44.21)
	private static final Pattern WORD_SPOTED_PATERN = Pattern
			.compile(SPHINX_RESULT);
	private String language = "lt";
	private ConfigurationManager configurationManager;
	

	public SpottingServiceSphinxImp() {
//		String outOfGrammarProbabilityItem = "9E-1";
//		File dirFile = new File("./target/test-classes");
		configurationManager = newConfigurationManager();
	}
	
	
	
	@Override
	public void wordSpotting(URL urlFile, SpottingListener wordSpottingListener) {
		updateDataSourceAnbdGetFileSize(getConfigurationManager(), urlFile);
		String resString = recognitionAudio(getConfigurationManager());
		notifyOnRecognition(resString, wordSpottingListener);
		
	}
	
	private void notifyOnRecognition(String resString, SpottingListener wordSpottingListener) {
		Iterable<String> resultTokens = Splitter.on(" ").omitEmptyStrings()
				.trimResults().split(resString);
		for (String currWordToken : resultTokens) {
			Matcher ma = WORD_SPOTED_PATERN.matcher(currWordToken);
			if (!ma.matches()) {
				continue;
			}
			String label = ma.group(1).toUpperCase();
			String startStr = ma.group(2).toUpperCase();
			String endStr = ma.group(3).toUpperCase();
			Float startFloat = Float.valueOf(startStr);
			Float endFloat = Float.valueOf(endStr);
			Long start = (long) (startFloat.floatValue() * 1000);
			Long end = (long) (endFloat.floatValue()*1000);
			SignalSegment newSegment = new SignalSegment(new Marker(start, (end-start), label));
			List<RecognitionResult> recognitionResults = Lists.newArrayList();
			wordSpottingListener.foundSegment("sourceId", newSegment, recognitionResults);
		}
	}



	/**
	 * 
	 * @param dirFile
	 * @param outOfGrammarProbabilityItem
	 * @return
	 */
	private ConfigurationManager newConfigurationManager() {
		String cfPath = MessageFormat.format("/config/{0}/cmusphinx-config.xml",getLanguage());
		URL url = this.getClass().getResource(cfPath);
		LOG.error("Config: {}", url);
		ConfigurationManager cm = new ConfigurationManager(url);

//		ConfigurationManagerUtils.setProperty(cm, "FlatLinguist", "outOfGrammarProbability", outOfGrammarProbabilityItem);
//		ConfigurationManagerUtils.dumpPropStructure(cm);
		return cm;
	}
	
	public void addKeyword(String keyWord){
		NoSkipGrammar grammar = (NoSkipGrammar) configurationManager.lookup("NoSkipGrammar");
		if(grammar == null){
			LOG.error("Grammar NoSkipGrammar not found. Cannot add: {}", keyWord);
			return;
		}
		grammar.addKeyword(keyWord);
	}

	/**
	 * 
	 * @param cm
	 * @param audioFileItem
	 * @return
	 */
	private int updateDataSourceAnbdGetFileSize(ConfigurationManager cm,
			URL urlFile) {
		LOG.debug("[updateDataSourceAnbdGetFileSize]audioFileItem: {}", urlFile );
		AudioFileDataSource dataSource = (AudioFileDataSource) cm
				.lookup("audioFileDataSource");

		int fileSize = 0;
		try {
			dataSource.setAudioFile(urlFile, urlFile.toString());
			fileSize = (int) new File(urlFile.toURI()).length();
			
		} catch (URISyntaxException e) {
			LOG.error("Exception Error: ", e);
		}
		return fileSize;
	}

	
	/**
	 * 
	 * @param cm
	 * @return
	 */
	private String recognitionAudio(ConfigurationManager cm) {
		Recognizer recognizer = (Recognizer) cm.lookup("recognizer");
		recognizer.allocate();
		Result result = recognizer.recognize();
		if (result == null) {
			return null;
		}
		String resString = result.getTimedBestResult(false, true);
		LOG.debug("[recognitionAudio]Result: {}", resString);
		
		//Lattice lat = new Lattice(result);
		
        for (WordResult wr : result.getWords()) {
            LOG.debug("[performTestEachAudio] \t word: {} [start:{}]", wr.getPronunciation().getWord().getSpelling(), wr.getStartFrame());

        }
		recognizer.deallocate();
		return resString;

	}
	

	private String getLanguage() {
		return this.language;
	}



	public ConfigurationManager getConfigurationManager() {
		return configurationManager;
	}




}

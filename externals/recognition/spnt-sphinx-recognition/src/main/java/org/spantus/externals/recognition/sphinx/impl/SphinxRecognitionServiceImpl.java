package org.spantus.externals.recognition.sphinx.impl;

import java.io.InputStream;
import java.net.URL;
import java.text.MessageFormat;

import org.spantus.core.marker.Marker;
import org.spantus.core.marker.MarkerSet;
import org.spantus.core.marker.MarkerSetHolder;
import org.spantus.core.marker.MarkerSetHolder.MarkerSetHolderEnum;
import org.spantus.externals.recognition.sphinx.SphinxRecognitionService;
import org.spantus.extr.wordspot.sphinx.linguist.language.grammar.NoSkipGrammar;
import org.spantus.logger.Logger;

import edu.cmu.sphinx.frontend.util.StreamDataSource;
import edu.cmu.sphinx.linguist.acoustic.Unit;
import edu.cmu.sphinx.linguist.dictionary.Pronunciation;
import edu.cmu.sphinx.recognizer.Recognizer;
import edu.cmu.sphinx.result.Result;
import edu.cmu.sphinx.result.WordResult;
import edu.cmu.sphinx.util.props.ConfigurationManager;

public class SphinxRecognitionServiceImpl implements SphinxRecognitionService {
	
	private static final Logger LOG = Logger.getLogger(SphinxRecognitionServiceImpl.class);

	private static final String SILENCE = "<sil>";
	private static final String UNKOWN = "<unk>";

	private ConfigurationManager configurationManager = null;
	
	public SphinxRecognitionServiceImpl() {
		configurationManager = newConfigurationManager();
	}

	/* (non-Javadoc)
	 * @see org.spantus.externals.recognition.sphinx.SphinxRecognitionService#recognize(java.io.InputStream, java.lang.String)
	 */
	@Override
	public MarkerSetHolder  recognize(InputStream inputStream, String streamName) {
		Recognizer recognizer = (Recognizer) configurationManager.lookup("recognizer");
		recognizer.allocate();
		StreamDataSource reader = (StreamDataSource)
				configurationManager.lookup("audioFileDataSource");
		reader.setInputStream(inputStream, streamName);
		Result result = recognizer.recognize();		
		MarkerSetHolder markerSetHolder = processResult(result);
		recognizer.deallocate();
		return markerSetHolder;
	}
	
	/**
	 * 
	 * @param result
	 * @return
	 */
	private MarkerSetHolder processResult(Result result) {
		MarkerSetHolder markerSetHolder = new MarkerSetHolder();
		MarkerSet wordMarkerSet = new MarkerSet();
		MarkerSet phoneMarkerSet = new MarkerSet();
		markerSetHolder.getMarkerSets().put(MarkerSetHolderEnum.word.name(), wordMarkerSet);
		markerSetHolder.getMarkerSets().put(MarkerSetHolderEnum.phone.name(), phoneMarkerSet);
		for (WordResult word : result.getWords()) {
			Marker wordMarker = newMarker(word);
			if(wordMarker!=null){
				wordMarkerSet.getMarkers().add(wordMarker);
				Long everyUnitLength = wordMarker.getLength()/word.getPronunciation().getUnits().length;
				Long nextUnitStart = wordMarker.getStart(); 
				for (Unit unit : word.getPronunciation().getUnits()) {
					Marker unitMarker = newMarker(unit, nextUnitStart, everyUnitLength);
					nextUnitStart = unitMarker.getEnd();
					phoneMarkerSet.getMarkers().add(unitMarker);
				}
			}
		}
		return markerSetHolder;
	}
	@Override
	public void addKeyword(String keyWord){
		NoSkipGrammar grammar = (NoSkipGrammar) configurationManager.lookup("NoSkipGrammar");
		if(grammar == null){
			LOG.error("Grammar NoSkipGrammar not found. Cannot add: {}", keyWord);
			return;
		}
		grammar.addKeyword(keyWord);
	}
//	/**
//	 * Creates new Configuration manager from file
//	 * @return
//	 */
//	private ConfigurationManager newConfigurationManager() {
//		String cfPath = MessageFormat.format("/config/{0}/cmusphinx-config.xml",getLanguage());
//		ConfigurationManager configurationManager = new ConfigurationManager(
//				this.getClass().getResource(cfPath));
//		return configurationManager;
//	}
	
	/**
	 * 
	 * @param dirFile
	 * @param outOfGrammarProbabilityItem
	 * @return
	 */
	private ConfigurationManager newConfigurationManager() {
		String cfPath = MessageFormat.format("/config/{0}/cmusphinx-config.xml",getLanguage());
//		String cfPath = "lt_robotas.config.xml";
		URL url = this.getClass().getResource(cfPath);
		LOG.error("Config: {0}", url);
		ConfigurationManager cm = new ConfigurationManager(url);

//		ConfigurationManagerUtils.setProperty(cm, "FlatLinguist", "outOfGrammarProbability", outOfGrammarProbabilityItem);
//		ConfigurationManagerUtils.dumpPropStructure(cm);
		return cm;
	}

	protected String getLanguage() {
		return "lt";
	}

	private Marker newMarker(Unit unit, Long nextUnitStart, Long everyUnitLength) {
		Marker unitMarker = new Marker();
		unitMarker.setLabel(unit.getName());
		unitMarker.setStart(nextUnitStart);
		unitMarker.setLength(everyUnitLength);
		return unitMarker;
	}

	private Marker newMarker(WordResult word) {
		if(word.getEndFrame() < 0){
			return null;
		}
		if(SILENCE.equals(word.getPronunciation().getWord().getSpelling())){
			return null;
		}
		if(Pronunciation.UNKNOWN.equals(word.getPronunciation())){
			return null;
		}
		Marker wordMarker = new Marker();
		wordMarker.setLabel(word.getPronunciation().getWord().getSpelling());
		wordMarker.setStart((long) word.getStartFrame());
		int end = word.getEndFrame();
		wordMarker.setEnd((long)end);
		return wordMarker;
	}

}

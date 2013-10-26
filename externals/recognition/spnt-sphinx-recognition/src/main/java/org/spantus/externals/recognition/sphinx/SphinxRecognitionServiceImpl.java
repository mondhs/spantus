package org.spantus.externals.recognition.sphinx;

import java.io.InputStream;

import org.spantus.core.marker.Marker;
import org.spantus.core.marker.MarkerSet;
import org.spantus.core.marker.MarkerSetHolder;
import org.spantus.core.marker.MarkerSetHolder.MarkerSetHolderEnum;

import edu.cmu.sphinx.decoder.search.Token;
import edu.cmu.sphinx.frontend.util.StreamDataSource;
import edu.cmu.sphinx.linguist.acoustic.Unit;
import edu.cmu.sphinx.linguist.dictionary.Word;
import edu.cmu.sphinx.recognizer.Recognizer;
import edu.cmu.sphinx.result.Result;
import edu.cmu.sphinx.result.WordResult;
import edu.cmu.sphinx.util.props.ConfigurationManager;

public class SphinxRecognitionServiceImpl {


	private static final String SIL = "<sil>";

	public MarkerSetHolder  recognize(InputStream inputStream, String streamName) {
		MarkerSetHolder markerSetHolder = new MarkerSetHolder();
		MarkerSet wordMarkerSet = new MarkerSet();
		MarkerSet phoneMarkerSet = new MarkerSet();
		markerSetHolder.getMarkerSets().put(MarkerSetHolderEnum.word.name(), wordMarkerSet);
		markerSetHolder.getMarkerSets().put(MarkerSetHolderEnum.phone.name(), phoneMarkerSet);
		ConfigurationManager configurationManager = new ConfigurationManager(
				SphinxRecognitionServiceImpl.class.getResource("sphinx-recognition.config.xml"));

		Recognizer recognizer = (Recognizer) configurationManager.lookup("recognizer");
		recognizer.allocate();

		StreamDataSource reader = (StreamDataSource)
				configurationManager.lookup("streamDataSource");
		reader.setInputStream(inputStream, streamName);
		Result result = recognizer.recognize();
		
		Token token = result.getBestToken();
//		while (token != null && !token.isEmitting()) {
//			if (token.isWord()) {
//				 Word word = token.getWord();
//		         float startTime = startFeature == null ? -1 : ((float) startFeature.getFirstSampleNumber() /
//		                 startFeature.getSampleRate());
//		         float endTime = endFeature == null ? -1 : ((float) endFeature.getFirstSampleNumber() /
//		                 endFeature.getSampleRate());
//			}
//		}

		
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
		recognizer.deallocate();
		return markerSetHolder;
	}

	private Marker newMarker(Unit unit, Long nextUnitStart, Long everyUnitLength) {
		Marker unitMarker = new Marker();
		unitMarker.setLabel(unit.getName());
		unitMarker.setStart(nextUnitStart);
		unitMarker.setLength(everyUnitLength);
		return unitMarker;
	}

	private Marker newMarker(WordResult word) {
		Marker wordMarker = new Marker();
		wordMarker.setLabel(word.getPronunciation().toString());
		wordMarker.setStart((long) word.getStartFrame());
		int end = word.getEndFrame();
		if(word.getEndFrame() < 0){
			return null;
		}
		if(SIL.equals(word.getPronunciation().getWord().getSpelling())){
			return null;
		}
		wordMarker.setEnd((long)end);
		return wordMarker;
	}

}

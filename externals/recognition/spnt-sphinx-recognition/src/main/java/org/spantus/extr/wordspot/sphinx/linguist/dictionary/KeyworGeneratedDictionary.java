package org.spantus.extr.wordspot.sphinx.linguist.dictionary;

import java.text.MessageFormat;
import java.util.List;

import org.spantus.extr.wordspot.sphinx.linguist.GraphemeToPhonemeMapperLithuanianImpl;

import edu.cmu.sphinx.linguist.dictionary.FastDictionary;
import edu.cmu.sphinx.linguist.dictionary.Word;
/**
 * Check if dictionary has the word if not generates pronunciation using rules 
 * 
 * @author Mindaugas Greibus
 *
 */
public class KeyworGeneratedDictionary extends FastDictionary{
	GraphemeToPhonemeMapperLithuanianImpl graphemeToPhoneme;
	/**
	 * check if parent implementation finds out word in dictionary if not it just generate one and add to the dictionary.
	 */
	@Override
	public Word getWord(String text) {
		Word word = super.getWord(text);
		if(word == null){
			List<String> phonemes = getGraphemeToPhoneme().transform(text.toUpperCase());
			String entry = phonemes.toString().replaceAll("[\\[\\],]","").toUpperCase();
			logger.warning(MessageFormat.format("The dictionary missign entry for the word ''{0}'' generated phonetic transcription ''{1}''", text, entry));
			dictionary.put(text.toLowerCase(), entry);
			word = super.getWord(text);
		}
		return word;
	}
	public GraphemeToPhonemeMapperLithuanianImpl getGraphemeToPhoneme() {
		if(graphemeToPhoneme == null){
			graphemeToPhoneme = new GraphemeToPhonemeMapperLithuanianImpl();
		}
		return graphemeToPhoneme;
	}
}

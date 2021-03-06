package org.spantus.exp.synthesis;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.common.collect.Lists;


public class SpeechGeneratorEn extends SpeechGeneratorLt {
	
	Map<String, List<String>> syllableCache = new HashMap<String, List<String>>();
	public SpeechGeneratorEn() {
		super();
		setVoice("/home/as/bin/mbrola/en1/en1");
		syllableCache.put("what", Lists.newArrayList("w","Q","t"));
		syllableCache.put("is", Lists.newArrayList("I","z"));
		syllableCache.put("the", Lists.newArrayList("D","@"));
		syllableCache.put("time",Lists.newArrayList("t", "aI", "m"));
		getLengths().clear();
		getLengths().put("w", 65L);
		getLengths().put("Q", 62L);
		getLengths().put("t", 101L);
		getLengths().put("I", 74L);
		getLengths().put("z", 65L);
		getLengths().put("D", 65L);
		getLengths().put("@", 134L);
		getLengths().put("aI", 109L);
		getLengths().put("m", 159L);
		getLengths().put("_", 50L);
	}
	
	@Override
	protected List<String> findSyllable(String word) {
		List<String> wordOneSyllable = syllableCache.get(word);
		if(wordOneSyllable !=null){
			return Lists.newArrayList(word);
		}
		return super.findSyllable(word);
	}
	
	@Override
	protected List<String> findPhones(String syllable) {
		List<String> wordOneSyllable = syllableCache.get(syllable);
		if(syllableCache.get(syllable) !=null){
			return Lists.newArrayList(wordOneSyllable);
		}
		return super.findPhones(syllable);
	}

	@Override
	protected ArrayList<String> findWords(String sentence) {
		ArrayList<String> words = super.findWords(sentence);
		words.add(0, "_");
		words.add(0, "_");
		words.add( "_");
		words.add( "_");
		return words;
	}

	public static void main(String[] args) {
		String text = "What_is_the_time";
		SpeechGeneratorEn speechGenerator = new SpeechGeneratorEn();
		Integer[] levels = new  Integer[]{30, 15, 10, 5, 0};
		for (Integer level : levels) {
			speechGenerator.bulkGeneration(text, "/home/as/tmp/garsyno.modelis-en/TEST",level, 50);
		}
		for (Integer level : levels) {
			speechGenerator.bulkGeneration(text, "/home/as/tmp/garsyno.modelis-en/TRAIN",level, 1);
		}

	}


}

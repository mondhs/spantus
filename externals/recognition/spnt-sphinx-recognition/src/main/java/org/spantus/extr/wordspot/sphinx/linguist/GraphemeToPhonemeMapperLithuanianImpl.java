package org.spantus.extr.wordspot.sphinx.linguist;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Pattern;

public class GraphemeToPhonemeMapperLithuanianImpl {
	private static final Map<Pattern, String> letterToPhoneMap;
	static {
		Map<Pattern, String> letterToPhone = new LinkedHashMap<Pattern, String>();
		letterToPhone.put(Pattern.compile("ch"), " X ");

		letterToPhone.put(Pattern.compile("ai"), " AI ");
		letterToPhone.put(Pattern.compile("au"), " AU ");
		letterToPhone.put(Pattern.compile("ei"), " EI ");
		letterToPhone.put(Pattern.compile("eu"), " EU ");
		letterToPhone.put(Pattern.compile("oi"), " OI ");
		letterToPhone.put(Pattern.compile("ui"), " UI ");
		letterToPhone.put(Pattern.compile("ie"), " IE ");
		letterToPhone.put(Pattern.compile("uo"), " UO ");
		letterToPhone.put(Pattern.compile("el"), " EL ");
		
		//general improvements
		letterToPhone.put(Pattern.compile("ju"), " UH ");

		letterToPhone.put(Pattern.compile("a"), " A ");
		letterToPhone.put(Pattern.compile("ą"), " AA ");
		letterToPhone.put(Pattern.compile("b"), " B ");
		letterToPhone.put(Pattern.compile("c"), " C ");
		letterToPhone.put(Pattern.compile("č"), " CH ");
		letterToPhone.put(Pattern.compile("d"), " D ");
		letterToPhone.put(Pattern.compile("e"), " E ");
		letterToPhone.put(Pattern.compile("ę"), " EA ");
		letterToPhone.put(Pattern.compile("ė"), " EH ");
		letterToPhone.put(Pattern.compile("f"), " F ");
		letterToPhone.put(Pattern.compile("g"), " G ");
		letterToPhone.put(Pattern.compile("h"), " HH ");
		letterToPhone.put(Pattern.compile("i"), " IH ");
		letterToPhone.put(Pattern.compile("į"), " IY ");
		letterToPhone.put(Pattern.compile("y"), " IY ");
		letterToPhone.put(Pattern.compile("j"), " Y ");
		letterToPhone.put(Pattern.compile("k"), " K ");
		letterToPhone.put(Pattern.compile("l"), " L ");
		letterToPhone.put(Pattern.compile("m"), " M ");
		letterToPhone.put(Pattern.compile("n"), " N ");
		letterToPhone.put(Pattern.compile("o"), " OO ");
		letterToPhone.put(Pattern.compile("p"), " P ");
		letterToPhone.put(Pattern.compile("r"), " R ");
		letterToPhone.put(Pattern.compile("s"), " S ");
		letterToPhone.put(Pattern.compile("š"), " SH ");
		letterToPhone.put(Pattern.compile("t"), " T ");
		letterToPhone.put(Pattern.compile("u"), " UH ");
		letterToPhone.put(Pattern.compile("ų"), " UW ");
		letterToPhone.put(Pattern.compile("ū"), " UW ");
		letterToPhone.put(Pattern.compile("v"), " V ");
		letterToPhone.put(Pattern.compile("z"), " Z ");
		letterToPhone.put(Pattern.compile("ž"), " ZH ");
		letterToPhoneMap = Collections.unmodifiableMap(letterToPhone);
	}

	private static final Map<String, String> availablePhoneMap;
	static {
		Map<String, String> availablePhones = new LinkedHashMap<String, String>();
		availablePhones.put("A", "A");
		availablePhones.put("A1", "A1");
		availablePhones.put("AA", "AA");
		availablePhones.put("AA1", "AA1");
		availablePhones.put("AI", "AI");
		availablePhones.put("AI2", "AI2");
		availablePhones.put("AU", "AU");
		availablePhones.put("AU1", "AU1");
		availablePhones.put("AU2", "AU2");
		availablePhones.put("B", "B");
		availablePhones.put("CH", "CH");
		availablePhones.put("D", "D");
		availablePhones.put("E", "E");
		availablePhones.put("E1", "E1");
		availablePhones.put("EA", "EA");
		availablePhones.put("EA2", "EA2");
		availablePhones.put("EH", "EH");
		availablePhones.put("EH1", "EH");
		availablePhones.put("EH2", "EH2");
		availablePhones.put("EI", "EI");
		availablePhones.put("EI2", "EI2");
		availablePhones.put("EL", "EL");
		availablePhones.put("EU", "EU");
		availablePhones.put("F", "F");
		availablePhones.put("G", "G");
		availablePhones.put("IE", "IE");
		availablePhones.put("IE1", "IE1");
		availablePhones.put("IE2", "IE2");
		availablePhones.put("IH", "IH");
		availablePhones.put("IH1", "IH1");
		availablePhones.put("IH2", "IH2");
		availablePhones.put("IY", "IY");
		availablePhones.put("IY1", "IY1");
		availablePhones.put("IY2", "IY2");
		availablePhones.put("K", "K");
		availablePhones.put("L", "L");
		availablePhones.put("L2", "L2");
		availablePhones.put("M", "M");
		availablePhones.put("M2", "M2");
		availablePhones.put("N", "N");
		availablePhones.put("N2", "N2");
		availablePhones.put("O", "O");
		availablePhones.put("OO", "OO");
		availablePhones.put("P", "P");
		availablePhones.put("R", "R");
		availablePhones.put("R2", "R2");
		availablePhones.put("S", "S");
		availablePhones.put("SH", "SH");
		availablePhones.put("SIL", "SIL");
		availablePhones.put("T", "T");
		availablePhones.put("UH", "UH");
		availablePhones.put("UH1", "UH1");
		availablePhones.put("UI", "UI");
		availablePhones.put("UO", "UO");
		availablePhones.put("UW", "UW");
		availablePhones.put("UW1", "UW1");
		availablePhones.put("V", "V");
		availablePhones.put("X", "X");
		availablePhones.put("Y", "Y");
		availablePhones.put("Z", "Z");
		availablePhones.put("ZH", "ZH");
		availablePhoneMap = Collections.unmodifiableMap(availablePhones);
	}

	private Locale locale = new Locale("lt", "LT");
	
	protected Locale getLocale(){
		return locale; 
	}
	/**
	 *  
	 * @param word in graphemes eg word
	 * @return same word in phonemes separated by space: W O R D
	 */
	public List<String> transform(String word) {
		List<String> phons = new ArrayList<String>();
		word = word.toLowerCase(getLocale());
		for (Entry<Pattern, String> entry : letterToPhoneMap.entrySet()) {
			word = entry.getKey().matcher(word).replaceAll(entry.getValue());
		}
		for (String ch : word.split(" ")) {
			String phon = filterNonPhoneme(ch);
			if (phon != null) {
				phons.add(phon);
			}
		}
		return phons;
	}

	private static String filterNonPhoneme(String str) {
		return availablePhoneMap.get(str);
	}
}

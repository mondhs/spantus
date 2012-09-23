package org.spantus.exp.synthesis;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.io.Files;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.UnsupportedAudioFileException;
import org.spantus.core.marker.Marker;
import org.spantus.core.marker.MarkerSetHolder.MarkerSetHolderEnum;
import org.spantus.core.wav.WorkAudioManager;
import org.spantus.utils.Assert;
import org.spantus.utils.StringUtils;

public class SpeechGeneratorLt extends AbstractSpeechGenerator{
	private static final String PHONEME_FORMAT = "{0} {1}\n";
	private Map<String, Long> lengths = null;



	File tmpDir = Files.createTempDir();

	String voice = "/home/as/bin/mbrola/lt1/lt1";
	
	public SpeechGeneratorLt() {
		lengths = Maps.newHashMap();
		lengths.put("a", 130L);
		lengths.put("e", 130L);
		lengths.put("r", 45L);
		lengths.put("t", 65L);
		lengths.put("g", 59L);
		lengths.put("m", 67L);
		lengths.put("n", 57L);
		lengths.put("_", 250L);
	}

	/**
	 * Translate to po standard
	 * 
	 * @param phonemes
	 * @return
	 */
        @Override
	public Transcribtion translate(String sentence, float lengthCoef) {
		Transcribtion transcribtion = new Transcribtion();

		StringBuilder sb = transcribtion.getTransctiption();
		sb.append(";").append(sentence).append("\n");
		
		String previousWord = "";
		for (String word : findWords(sentence)) {
			if (StringUtils.hasText(word)) {
				appendWord(word, previousWord, transcribtion, lengthCoef);
			} else {
				appendSilence(previousWord, transcribtion, lengthCoef);
			}
			previousWord = word;
		}
		return transcribtion;
	}

	protected ArrayList<String> findWords(String sentence) {
		ArrayList<String> words = Lists.newArrayList(sentence.split("_"));
		return words;
	}

	/**
	 * 
	 * @param sb
	 * @param previousSyllable
	 * @param transcribtion
	 */
	protected void appendSilence(String previousSyllable,
			Transcribtion transcribtion, float lenghtCoef) {
		long silenceLength = (long) (getLengths().get("_") * lenghtCoef);
		transcribtion.getTransctiption().append(
				MessageFormat.format(PHONEME_FORMAT, "_", silenceLength));
		transcribtion.incFinished(silenceLength);
	}

	/**
	 * 
	 * @param word
	 * @param sb
	 * @param previousWord
	 * @param transcribtion
	 * @return
	 */
	protected StringBuilder appendWord(String word, String previousWord,
			final Transcribtion transcribtion, float lengthCoef) {
		long start = transcribtion.getFinished();

		String previousSyllable = "";
		for (String syllable : findSyllable(word)) {
			appendSyllable(syllable, previousSyllable, transcribtion,
					lengthCoef);
			previousSyllable = syllable;
		}
		Marker marker = new Marker();
		marker.setLabel(word);
		marker.setStart(start);
		marker.setEnd(transcribtion.getFinished());
		transcribtion.getHolder().getMarkerSets()
				.get(MarkerSetHolderEnum.word.name()).getMarkers().add(marker);

		appendSilence(previousSyllable, transcribtion, rand());
		return transcribtion.getTransctiption();

	}

	protected List<String> findPhones(String syllable) {
		List<String> result = Lists.newArrayList();
		for (int i = 0; i < syllable.length(); i++) {
			result.add(""+syllable.charAt(i));
		}
		return result;
	}
	
	protected List<String> findSyllable(String word) {
		String tunedWord = word;
		tunedWord = tunedWord.replaceAll("a", "a-");
		tunedWord = tunedWord.replaceAll("e", "e-");
		return Lists.newArrayList(tunedWord.split("-"));
	}

	

	/**
	 * 
	 * @param syllable
	 * @param sb
	 * @param previousSyllable
	 * @param transcribtion
	 * @return
	 */
	private StringBuilder appendSyllable(String syllable,
			String previousSyllable, final Transcribtion transcribtion,
			float lengthCoef) {
		long start = transcribtion.getFinished();

		for (String phoneItem : findPhones(syllable)) {
			Long length =  (lengths.get(phoneItem));
			Assert.isTrue(length != null, "Not found " + phoneItem);
			length = (long) (length * lengthCoef);
			transcribtion.getTransctiption().append(
					MessageFormat.format(PHONEME_FORMAT, phoneItem, length));
			transcribtion.incFinished(length);
		}

		transcribtion.getTransctiption().append(
				MessageFormat.format(PHONEME_FORMAT, "_", 20));
		transcribtion.incFinished(10L);
		Marker marker = new Marker();
		marker.setLabel(syllable);
		marker.setStart(start);
		marker.setEnd(transcribtion.getFinished());
		transcribtion.getHolder().getMarkerSets()
				.get(MarkerSetHolderEnum.phone.name()).getMarkers().add(marker);
		return transcribtion.getTransctiption();
	}


	/**
	 * 
	 * @param trascribtion
	 * @return
	 */
        @Override
	public AudioInputStream generate(Transcribtion trascribtion) {
		
		long id = System.currentTimeMillis();
		File poFile = new File(tmpDir, id +"-zodis.po");
		File wavFile = new File(tmpDir, id +"-zodis.wav");
		try {
			Files.write(trascribtion.getTransctiption(), poFile,
					Charset.defaultCharset());
		} catch (IOException e) {
			throw new IllegalArgumentException(e);
		}
		try {
			String command = MessageFormat.format(
					"mbrola -l 11025 {0} {1} {2}", getVoice(),
					poFile.getAbsolutePath(), wavFile.getAbsolutePath());
			System.out.println(command);
			Process p = Runtime.getRuntime().exec(command);
			p.waitFor();
			BufferedReader stdInput = new BufferedReader(new InputStreamReader(
					p.getInputStream()));


			String s ;
			// read the output from the command
			System.out.println("Here is the standard output of the command:\n");
			while ((s = stdInput.readLine()) != null) {
				System.out.println(s);
			}

                        printError(p);
                        
			return WorkAudioManager.createAudioInputStream(wavFile.toURI()
					.toURL());
		} catch (IOException | InterruptedException | UnsupportedAudioFileException e) {
			throw new IllegalArgumentException(e);
		}
	}
	


	public Map<String, Long> getLengths() {
		return lengths;
	}

	public void setLengths(Map<String, Long> lengths) {
		this.lengths = lengths;
	}

	public String getVoice() {
		return voice;
	}

	public void setVoice(String voice) {
		this.voice = voice;
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		SpeechGeneratorLt speechGenerator = new SpeechGeneratorLt();
                
                String text = "Lietuvos kariuomenė buvo kuriama sunkiomis sąlygomis – 1918 metų pabaigoje, po Pirmojo pasaulinio karo, iš Lietuvos traukėsi vokiečių kariuomenė";

                                
		Transcribtion trascribtion = speechGenerator.translate(
				"_regata_mane__rega_mename_", speechGenerator.rand());
//		AudioInputStream ais = speechGenerator.generate(trascribtion);
//		String fileName = speechGenerator.persist(ais, "./target/zodis.wav");
//		AudioManagerFactory.createAudioManager().play(
//				wavFile.toURI().toURL());
//		speechGenerator.persist(trascribtion, fileName + ".mspnt.xml");
//		speechGenerator.addNoize(fileName, 0);
		
//		Integer[] levels = new  Integer[]{30, 15, 10, 5, 0};
//		for (Integer level : levels) {
//			speechGenerator.bulkGeneration(text, "/home/as/tmp/garsyno.modelis/TEST",level, 50);
//		}
//		for (Integer level : levels) {
//			speechGenerator.bulkGeneration(text, "/home/as/tmp/garsyno.modelis/TRAIN",level, 1);
//		}

		
	}

    @Override
    protected void processMarker(MarkerMbrola phoneMarker) {
        //Do nothing
    }



}

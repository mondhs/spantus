package org.spantus.exp.recognition.synthesis;

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

import org.spantus.core.extractor.dao.MarkerDao;
import org.spantus.core.marker.Marker;
import org.spantus.core.marker.MarkerSetHolder.MarkerSetHolderEnum;
import org.spantus.core.wav.AudioManagerFactory;
import org.spantus.core.wav.WorkAudioManager;
import org.spantus.utils.Assert;
import org.spantus.utils.StringUtils;
import org.spantus.work.services.WorkServiceFactory;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.io.Files;

public class SpeechGeneratorLt {
	private static final String PHONEME_FORMAT = "{0} {1}\n";
	private Map<String, Integer> lengths = null;


	MarkerDao markerDao;
	File tmpDir = Files.createTempDir();

	String voice = "/home/as/bin/mbrola/lt1/lt1";
	
	public SpeechGeneratorLt() {
		lengths = Maps.newHashMap();
		lengths.put("a", 130);
		lengths.put("e", 130);
		lengths.put("r", 45);
		lengths.put("t", 65);
		lengths.put("g", 59);
		lengths.put("m", 67);
		lengths.put("n", 57);
		lengths.put("_", 250);
	}

	/**
	 * Translate to po standard
	 * 
	 * @param phonemes
	 * @return
	 */
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

	public float rand() {
		float speed = 1.5f; 
		float rand = 
				 (float) (.95f* (Math.random() - .5f));
//				0f;
		return speed -rand;
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
			Integer length =  (lengths.get(phoneItem));
			Assert.isTrue(length != null, "Not found " + phoneItem);
			length = (int) (length * lengthCoef);
			transcribtion.getTransctiption().append(
					MessageFormat.format(PHONEME_FORMAT, phoneItem, length));
			transcribtion.incFinished(length);
		}

		transcribtion.getTransctiption().append(
				MessageFormat.format(PHONEME_FORMAT, "_", 20));
		transcribtion.incFinished(10);
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

			BufferedReader stdError = new BufferedReader(new InputStreamReader(
					p.getErrorStream()));

			String s = null;
			// read the output from the command
			System.out.println("Here is the standard output of the command:\n");
			while ((s = stdInput.readLine()) != null) {
				System.out.println(s);
			}

			// read any errors from the attempted command
			System.out
					.println("Here is the standard error of the command (if any):\n");
			while ((s = stdError.readLine()) != null) {
				System.out.println(s);
			}
			return WorkAudioManager.createAudioInputStream(wavFile.toURI()
					.toURL());
		} catch (IOException e) {
			throw new IllegalArgumentException(e);
		} catch (InterruptedException e) {
			throw new IllegalArgumentException(e);
		} catch (UnsupportedAudioFileException e) {
			throw new IllegalArgumentException(e);
		}
	}
	

	/**
	 * Noising
	 * 
	 * @param ais
	 * @return
	 */
	public void addNoize(String filename, int snr) {
//		DoubleDataSource audio = new AudioDoubleDataSource(ais);
//		float samplingRate = ais.getFormat().getSampleRate();
//		double cutOff = 100 / samplingRate;
//		double transition = 100 / samplingRate;
//		marytts.signalproc.filter.HighPassFilter hFilter = new marytts.signalproc.filter.HighPassFilter(
//				cutOff, transition);
//		DoubleDataSource filtered = hFilter.apply(audio);
//		return new DDSAudioInputStream(filtered, ais.getFormat());
	
		
		String command = MessageFormat.format(
//				"octave --path /home/as/src/ocatave/ --silent --eval 'noisefication(\"{0}\",{1});'"
//				"octave --silent --eval ''[y,Fs,bits]=wavread(\"{0}\",);y=awgn(y'''',{1},\"measured\");wavwrite(\"{0}1\",y'''',Fs);''"
				"octave --path /home/as/src/ocatave/ --silent --eval [y,Fs,bits]=wavread(''{0}'');y=awgn(y'''',{1},''measured'');wavwrite(\"{0}\",y'''',Fs);"
				, filename, snr);
		Process p;
		try {
			p = Runtime.getRuntime().exec(command);
			p.waitFor();
			BufferedReader stdInput = new BufferedReader(new InputStreamReader(
					p.getInputStream()));

			BufferedReader stdError = new BufferedReader(new InputStreamReader(
					p.getErrorStream()));

			String s = null;
			// read the output from the command
			System.out.println("Here is the standard output of the command:\n");
			while ((s = stdInput.readLine()) != null) {
				System.out.println(s);
			}

			System.out
			.println(command);
			// read any errors from the attempted command
			System.out
					.println("Here is the standard error of the command (if any):\n");
			while ((s = stdError.readLine()) != null) {
				System.out.println(s);
			}
		} catch (IOException e) {
			throw new IllegalArgumentException(e);
		} catch (InterruptedException e) {
			throw new IllegalArgumentException(e);
		}
	
	}

	/**
	 * 
	 * @param ais
	 * @param string
	 */
	public String persist(AudioInputStream ais, String fileName) {
		String st = AudioManagerFactory.createAudioManager()
				.save(ais, fileName);
		return st;
	}

	public Transcribtion persist(Transcribtion transcribtion, String fileName) {
		getMarkerDao().write(transcribtion.getHolder(), new File(fileName));
		return transcribtion;
	}
	
	public void bulkGeneration(String text, String path, int snr, int count){
		for (int i = 0; i < count; i++) {
			Transcribtion trascribtion = translate(text.toLowerCase(), rand());
			AudioInputStream ais = generate(trascribtion);
			String fileName=MessageFormat.format("{0}/sin_zodis_{1}_{2}", path, snr, i);
			File wavFile = new File(fileName+".wav");
			persist(ais,wavFile.getAbsolutePath());
			persist(trascribtion,fileName+ ".mspnt.xml");
			addNoize(wavFile.getAbsolutePath(), snr);
		}
	}

	public Map<String, Integer> getLengths() {
		return lengths;
	}

	public void setLengths(Map<String, Integer> lengths) {
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
//		Transcribtion trascribtion = speechGenerator.translate(
//				"_regata_mane__rega_mename_", speechGenerator.rand());
//		AudioInputStream ais = speechGenerator.generate(trascribtion);
//		String fileName = speechGenerator.persist(ais, "./target/zodis.wav");
//		AudioManagerFactory.createAudioManager().play(
//				wavFile.toURI().toURL());
//		speechGenerator.persist(trascribtion, fileName + ".mspnt.xml");
//		speechGenerator.addNoize(fileName, 0);
		String text = "_regata_mane__rega_mename_";
		
		Integer[] levels = new  Integer[]{30, 15, 10, 5, 0};
		for (Integer level : levels) {
			speechGenerator.bulkGeneration(text, "/home/as/tmp/garsyno.modelis/TEST",level, 50);
		}
		for (Integer level : levels) {
			speechGenerator.bulkGeneration(text, "/home/as/tmp/garsyno.modelis/TRAIN",level, 1);
		}

		
	}

	public MarkerDao getMarkerDao() {
		if (markerDao == null) {
			markerDao = WorkServiceFactory.createMarkerDao();
		}
		return markerDao;
	}

	public void setMarkerDao(MarkerDao markerDao) {
		this.markerDao = markerDao;
	}

}

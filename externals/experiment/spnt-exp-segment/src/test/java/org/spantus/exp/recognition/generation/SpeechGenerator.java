package org.spantus.exp.recognition.generation;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.text.MessageFormat;
import java.util.Map;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.UnsupportedAudioFileException;

import marytts.util.data.DoubleDataSource;
import marytts.util.data.audio.AudioConverterUtils;
import marytts.util.data.audio.AudioDoubleDataSource;
import marytts.util.data.audio.DDSAudioInputStream;

import org.spantus.core.marker.Marker;
import org.spantus.core.marker.MarkerSetHolder.MarkerSetHolderEnum;
import org.spantus.core.wav.AudioManagerFactory;
import org.spantus.core.wav.WorkAudioManager;
import org.spantus.utils.Assert;
import org.spantus.utils.FileUtils;
import org.spantus.utils.StringUtils;
import org.spantus.work.services.MarkerDao;
import org.spantus.work.services.WorkServiceFactory;

import com.google.common.collect.Maps;
import com.google.common.io.Files;

public class SpeechGenerator {
	private static final String PHONEME_FORMAT = "{0} {1}\n";
	Map<String, Integer> lengths = null;
	MarkerDao markerDao;

	public SpeechGenerator() {
		lengths = Maps.newHashMap();
		lengths.put("a", 195);
		lengths.put("e", 345);
		lengths.put("r", 45);
		lengths.put("t", 45);
		lengths.put("g", 45);
		lengths.put("m", 45);
		lengths.put("n", 45);
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
		String[] phonemesArr = sentence.split("_");
		String previousWord = "";
		for (String word : phonemesArr) {
			if (StringUtils.hasText(word)) {
				appendWord(word, previousWord, transcribtion, lengthCoef);
			} else {
				appendSilence(previousWord, transcribtion, lengthCoef);
			}
			previousWord = word;
		}
		return transcribtion;
	}

	/**
	 * 
	 * @param sb
	 * @param previousSyllable
	 * @param transcribtion
	 */
	private void appendSilence(String previousSyllable,
			Transcribtion transcribtion, float lenghtCoef) {
		long silenceLength = (long) (250 * lenghtCoef);
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
	private StringBuilder appendWord(String word, String previousWord,
			final Transcribtion transcribtion, float lengthCoef) {
		long silenceLength = 250;
		long start = transcribtion.getFinished();
		String tunedWord = word;
		tunedWord = tunedWord.replaceAll("a", "a,");
		tunedWord = tunedWord.replaceAll("e", "e,");
		String previousSyllable = "";
		for (String syllable : tunedWord.split(",")) {
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

		transcribtion.getTransctiption().append(
				MessageFormat.format(PHONEME_FORMAT, "_", 250));
		transcribtion.incFinished(silenceLength);
		return transcribtion.getTransctiption();

	}

	public float rand() {
		return (float) (1.0 - (Math.random() - .5f));
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
		for (int i = 0; i < syllable.length(); i++) {
			String phoneItem = "" + syllable.charAt(i);
			Integer length = (int) (lengths.get(phoneItem));
			if ("ae".contains(phoneItem)) {
				length = (int) (length * lengthCoef);
			}
			Assert.isTrue(length != null, "Not found " + phoneItem);
			transcribtion.getTransctiption().append(
					MessageFormat.format(PHONEME_FORMAT, phoneItem, length));
			transcribtion.incFinished(length);
		}

		transcribtion.getTransctiption().append(
				MessageFormat.format(PHONEME_FORMAT, "_", 10));
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
		String voice = "/home/as/bin/mbrola/lt/lt1/lt1";
		File tmpDir = Files.createTempDir();
		File poFile = new File(tmpDir, "zodis.po");
		File wavFile = new File(tmpDir, "zodis.wav");
		try {
			Files.write(trascribtion.getTransctiption(), poFile,
					Charset.defaultCharset());
		} catch (IOException e) {
			throw new IllegalArgumentException(e);
		}
		try {
			String command = MessageFormat.format(
					"mbrola -l 16000 {0} {1} {2}", voice,
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
	
		File file = new File(filename);
		
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
	
	public void bulkGeneration(String path, int snr, int count){
		for (int i = 0; i < count; i++) {
			Transcribtion trascribtion = translate("_regata_mane__rega_mename_", rand());
			AudioInputStream ais = generate(trascribtion);
			String fileName=MessageFormat.format("{0}/sin_zodis_{1}_{2}", path, snr, i);
			File wavFile = new File(fileName+".wav");
			persist(ais,wavFile.getAbsolutePath());
			persist(trascribtion,fileName+ ".mspnt.xml");
			addNoize(wavFile.getAbsolutePath(), snr);
		}
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		SpeechGenerator speechGenerator = new SpeechGenerator();
//		Transcribtion trascribtion = speechGenerator.translate(
//				"_regata_mane__rega_mename_", speechGenerator.rand());
//		AudioInputStream ais = speechGenerator.generate(trascribtion);
//		String fileName = speechGenerator.persist(ais, "./target/zodis.wav");
//		AudioManagerFactory.createAudioManager().play(
//				wavFile.toURI().toURL());
//		speechGenerator.persist(trascribtion, fileName + ".mspnt.xml");
//		speechGenerator.addNoize(fileName, 0);
		
		Integer[] levels = new  Integer[]{30, 15, 10, 5, 0};
		for (Integer level : levels) {
			speechGenerator.bulkGeneration("/home/as/tmp/garsyno.modelis/TEST",level, 51);
		}
		for (Integer level : levels) {
			speechGenerator.bulkGeneration("/home/as/tmp/garsyno.modelis/TRAIN",level, 1);
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

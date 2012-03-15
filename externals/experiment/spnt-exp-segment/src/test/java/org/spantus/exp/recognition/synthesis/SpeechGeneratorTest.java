package org.spantus.exp.recognition.synthesis;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import javax.sound.sampled.AudioInputStream;

import org.junit.Before;
import org.junit.Test;
import org.spantus.core.marker.Marker;
import org.spantus.core.marker.MarkerSet;
import org.spantus.core.marker.MarkerSetHolder.MarkerSetHolderEnum;
import org.spantus.core.wav.AudioManagerFactory;

public class SpeechGeneratorTest {

	SpeechGeneratorLt speechGenerator;
	@Before
	public void before() {
		speechGenerator = new SpeechGeneratorLt();
	}
	
	@Test
	public void testTranslate() {
		//given
		String phonemes = "_regata_mane__rega_mename_";
		//when
		Transcribtion trascribtion = speechGenerator.translate(phonemes, 1);
		String translated = trascribtion.getTransctiption().toString();
		MarkerSet syllables = trascribtion.getHolder().getMarkerSets().get(MarkerSetHolderEnum.phone.name());
		//then
		String[] translatedArr = translated.split("\n");
		assertEquals(37, translatedArr.length);
		assertEquals(";"+phonemes, translatedArr[0]);
		assertEquals("_ 250", translatedArr[1]);
		assertEquals("r 45", translatedArr[2]);
		assertEquals("e 345", translatedArr[3]);
		assertEquals("_ 10", translatedArr[4]);
		assertEquals("g 45", translatedArr[5]);
		assertEquals(4750, trascribtion.getFinished());
		Marker re = 	syllables.getMarkers().get(0);
		Marker ga = 	syllables.getMarkers().get(1);
		assertEquals(250, re.getStart().intValue());
		assertEquals(400, re.getLength().intValue());
		assertEquals(650, ga.getStart().intValue());
		assertEquals(250, ga.getLength().intValue());
	}
	
	@Test
	public void testGenerateAndPersist() {
		//given
		Transcribtion trascribtion = new Transcribtion();
		trascribtion.getTransctiption().append( "_ 250\n" +"a 250\n" + "_ 250\n");
		//when
		AudioInputStream ais = speechGenerator.generate(trascribtion);
		String file = speechGenerator.persist(ais, "./target/test.wav");
		//then
		assertNotNull(ais);
		assertNotNull(file);
	}
	
	@Test
	public void testGenerateAndPlay() {
		//given
		Transcribtion trascribtion = new Transcribtion();
		trascribtion.getTransctiption().append( "_ 250\n" +"a 250\n" + "_ 250\n");
		//when
		AudioInputStream ais = speechGenerator.generate(trascribtion);
		AudioManagerFactory.createAudioManager().play(ais, null, null);
		//then
		assertNotNull(ais);
	}

}

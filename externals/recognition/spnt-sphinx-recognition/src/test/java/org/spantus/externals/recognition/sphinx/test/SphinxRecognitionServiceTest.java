package org.spantus.externals.recognition.sphinx.test;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.spantus.core.marker.MarkerSet;
import org.spantus.core.marker.MarkerSetHolder;
import org.spantus.core.marker.MarkerSetHolder.MarkerSetHolderEnum;
import org.spantus.externals.recognition.sphinx.SphinxRecognitionServiceImpl;

public class SphinxRecognitionServiceTest {
	private SphinxRecognitionServiceImpl shinxRecognitionServiceImpl;
	
	@Before
	public void onSetup() {
		shinxRecognitionServiceImpl = new SphinxRecognitionServiceImpl();
	}
	
	@Test
	public void testRecognise() throws UnsupportedAudioFileException, IOException {
		//given
		URL audioFileURL = new File("../../../data/text1.wav").toURI().toURL();
		AudioInputStream ais = AudioSystem.getAudioInputStream(audioFileURL);
		//when
		MarkerSetHolder markerSetHolder = shinxRecognitionServiceImpl.recognize(ais, audioFileURL.getFile());
		//then
		MarkerSet wordMarkerSet = markerSetHolder.getMarkerSets().get(MarkerSetHolderEnum.word.name());
		MarkerSet phoneMarkerSet = markerSetHolder.getMarkerSets().get(MarkerSetHolderEnum.phone.name());
		Assert.assertEquals("word number",2, wordMarkerSet.getMarkers().size(),  0);
		Assert.assertEquals("phone number",7, phoneMarkerSet.getMarkers().size(),  0);
	}

}

package org.spantus.externals.recognition.sphinx.test;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Map;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.spantus.core.marker.Marker;
import org.spantus.core.marker.MarkerSet;
import org.spantus.core.marker.MarkerSetHolder;
import org.spantus.core.marker.MarkerSetHolder.MarkerSetHolderEnum;
import org.spantus.externals.recognition.sphinx.impl.SphinxRecognitionServiceImpl;

public class SphinxRecognitionServiceTest {
	private SphinxRecognitionServiceImpl shinxRecognitionServiceImpl;
	
	@Before
	public void onSetup() {
		shinxRecognitionServiceImpl = new SphinxRecognitionServiceImpl();
	}
	
	@Test
	public void testRecognise() throws UnsupportedAudioFileException, IOException {
		//given
		URL audioFileURL =  new File("../../../data/fa-ak1.wav").toURI().toURL();
		AudioInputStream ais = AudioSystem.getAudioInputStream(audioFileURL);
		shinxRecognitionServiceImpl.addKeyword("AKMUO");
		shinxRecognitionServiceImpl.addKeyword("SUTVARKYTI");
		//when
		MarkerSetHolder markerSetHolder = shinxRecognitionServiceImpl.recognize(ais, audioFileURL.getFile());
		//then
		MarkerSet wordMarkerSet = markerSetHolder.getMarkerSets().get(MarkerSetHolderEnum.word.name());
		MarkerSet phoneMarkerSet = markerSetHolder.getMarkerSets().get(MarkerSetHolderEnum.phone.name());
		Assert.assertEquals("word number",2, wordMarkerSet.getMarkers().size(),  0);
		List<Marker> foundSegment = wordMarkerSet.getMarkers();
		assertEquals("First word", "akmuo(A K M UO )",foundSegment.get(0).getLabel());
		assertEquals("First word starts", 770,foundSegment.get(0).getStart(),0);
		assertEquals("First word length", 560,foundSegment.get(0).getLength(),0);
		assertEquals("Second word", "sutvarkyti(S UH T V A R K IY T IH )",foundSegment.get(1).getLabel());
		assertEquals("Second word starts", 3390,foundSegment.get(1).getStart(),0);
		assertEquals("Second word length", 1100,foundSegment.get(1).getLength(),0);
		Assert.assertEquals("phone number",14, phoneMarkerSet.getMarkers().size(),  0);
	}

}

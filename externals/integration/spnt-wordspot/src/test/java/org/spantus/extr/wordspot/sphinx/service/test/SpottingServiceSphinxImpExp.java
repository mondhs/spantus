package org.spantus.extr.wordspot.sphinx.service.test;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spantus.core.marker.Marker;
import org.spantus.core.marker.MarkerSet;
import org.spantus.core.marker.MarkerSetHolder;
import org.spantus.core.marker.MarkerSetHolder.MarkerSetHolderEnum;
import org.spantus.externals.recognition.sphinx.impl.SphinxRecognitionServiceImpl;

public class SpottingServiceSphinxImpExp {
	private static final Logger LOG = LoggerFactory.getLogger(SpottingServiceSphinxImpExp.class);
	private SphinxRecognitionServiceImpl shinxRecognitionServiceImpl;
	
	
	@Before
	public void onSetup() {
		shinxRecognitionServiceImpl = new SphinxRecognitionServiceImpl();
	}
	
	@Test
	public void testRecognise() throws UnsupportedAudioFileException, IOException {
		//given
		URL audioFileURL =  new File("/home/as/src/garsynai/darbiniai/garsynas_2lietuvos/samples/000-30_1.wav").toURI().toURL();
		AudioInputStream ais = AudioSystem.getAudioInputStream(audioFileURL);
		shinxRecognitionServiceImpl.addKeyword("LIETUVOS");
		//when
		MarkerSetHolder markerSetHolder = shinxRecognitionServiceImpl.recognize(ais, audioFileURL.getFile());
		//then
		MarkerSet wordMarkerSet = markerSetHolder.getMarkerSets().get(MarkerSetHolderEnum.word.name());
		MarkerSet phoneMarkerSet = markerSetHolder.getMarkerSets().get(MarkerSetHolderEnum.phone.name());
		LOG.debug("foundSegment {}", wordMarkerSet.getMarkers());
		Assert.assertEquals("word number",2, wordMarkerSet.getMarkers().size(),  0);
		
	}

}

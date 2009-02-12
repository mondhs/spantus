package org.spantus.work.wav;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;

public class AudioMerge {
	public void mergeAudio(File audio1, File audio2) throws UnsupportedAudioFileException, IOException{
		AudioInputStream ais1 = AudioSystem.getAudioInputStream(audio1);		
		AudioInputStream ais2 = AudioSystem.getAudioInputStream(audio2);
		Collection<AudioInputStream> list=new ArrayList<AudioInputStream>();
		list.add(ais1);
		list.add(ais2);
		
		
	}
}

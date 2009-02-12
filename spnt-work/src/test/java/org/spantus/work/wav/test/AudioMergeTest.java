package org.spantus.work.wav.test;

import java.io.File;
import java.io.IOException;

import javax.sound.sampled.UnsupportedAudioFileException;

import org.spantus.work.wav.AudioMerge;

import junit.framework.TestCase;

public class AudioMergeTest extends TestCase {
	public void testMerge() throws UnsupportedAudioFileException, IOException{
		AudioMerge merge = new AudioMerge();
		File audio1 = new File("../data/t_1_2.wav");
		File audio2 = new File("../data/text1.wav");
		merge.mergeAudio(audio1, audio2);
	}
}

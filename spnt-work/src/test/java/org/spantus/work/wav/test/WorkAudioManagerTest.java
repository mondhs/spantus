/*
 * Part of program for analyze speech signal 
 * Copyright (c) 2008 Mindaugas Greibus (spantus@gmail.com)
 * http://spantus.sourceforge.net
 * 
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the
 * Free Software Foundation; either version 2 of the License, or (at your
 * option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 675 Mass Ave, Cambridge, MA 02139, USA.
 * 
 */
package org.spantus.work.wav.test;

import java.io.File;
import java.io.IOException;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.UnsupportedAudioFileException;

import junit.framework.TestCase;

import org.spantus.work.wav.AudioManager;
import org.spantus.work.wav.AudioManagerFactory;
import org.spantus.work.wav.WorkAudioManager;
/**
 * Audio Manager test
 * @author Mindaugas Greibus
 * @since 0.0.1
 * Created Sep 28, 2009
 */
public class WorkAudioManagerTest extends TestCase {
	
	AudioManager audioManager;
	
	File inputWavFile = null;
	String outputWavFilePrefered = null;
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		audioManager =AudioManagerFactory.createAudioManager();
		inputWavFile = new File("../data/t_1_2.wav");
		outputWavFilePrefered = "./target/test1.wav";
	}
	/**
	 * Test if file saved is correct
	 * @throws UnsupportedAudioFileException
	 * @throws IOException
	 */
	public void testExport() throws UnsupportedAudioFileException, IOException{
//		AudioInputStream inputStream = WorkAudioManager.createAudioInputStream(inputWavFile.toURI().toURL());
		Float start = .877F,
			length = .937F;
		String filePathSaved = audioManager.save(inputWavFile.toURI().toURL(), start, length, outputWavFilePrefered);
		File fileSaved = new File(filePathSaved);
		AudioInputStream outputStream = WorkAudioManager.createAudioInputStream(fileSaved.toURI().toURL());
		assertEquals("same frame length",41321,outputStream.getFrameLength());
//		inputStream.close();
		outputStream.close();
		if(fileSaved.exists()){
			fileSaved.delete();
		}
	}
	
}

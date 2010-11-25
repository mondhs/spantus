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

import junit.framework.Assert;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import org.spantus.work.wav.AudioManager;
import org.spantus.work.wav.AudioManagerFactory;
import org.spantus.work.wav.WorkAudioManager;
/**
 * Audio Manager test
 * @author Mindaugas Greibus
 * @since 0.0.1
 * Created Sep 28, 2009
 */
public class WorkAudioManagerTest {
	
	AudioManager audioManager;
	
	File inputWavFile = null;
	String outputWavFilePrefered = null;
	
	@Before
	public void setUp() throws Exception {
		audioManager =AudioManagerFactory.createAudioManager();
		inputWavFile = new File("../data/t_1_2.wav");
		outputWavFilePrefered = "./target/test1.wav";
	}
	/**
	 * Test if file saved is correct
	 * @throws UnsupportedAudioFileException
	 * @throws IOException
	 */
        @Test
	public void testExport() throws UnsupportedAudioFileException, IOException{
		//given
                Float start = .877F,
			length = .937F;
                //when
                String filePathSaved = audioManager.save(inputWavFile.toURI().toURL(),
                        start, length, outputWavFilePrefered);
		File fileSaved = new File(filePathSaved);
                
		AudioInputStream outputStream = WorkAudioManager.createAudioInputStream(
                        fileSaved.toURI().toURL());
                outputStream.close();

                //then
                Assert.assertTrue("File ends with " +
                        filePathSaved + " " + outputWavFilePrefered
                        ,
                        filePathSaved.endsWith(outputWavFilePrefered));
                Assert.assertEquals("same frame length",41321,outputStream.getFrameLength());

	}
        
        @After
        public void cleanup(){
            File fileSaved = new File(outputWavFilePrefered);
            //finalize
		if(fileSaved.exists()){
			fileSaved.delete();
		}
        }
	
}

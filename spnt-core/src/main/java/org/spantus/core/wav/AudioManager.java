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
package org.spantus.core.wav;

import java.io.ByteArrayOutputStream;
import java.net.URL;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
/**
 * 
 * 
 * @author Mindaugas Greibus
 *
 * @since 0.0.1
 * 
 * Created Aug 26, 2008
 *
 */
public interface AudioManager {
	public void play(URL file, Float starts, Float length);
	public void play(AudioInputStream stream, Float from, Float length) ;
	public void playinMils(URL file, Long starts, Long length);
        public void play(URL file);
        /**
         * 
         * @param file - wav file
         * @param starts - in mills
         * @param length - in mills
         * @param pathToSave
         * @return
         */
	public String save(URL file, Float starts, Float length, String pathToSave);
	public String save(AudioInputStream ais , String pathToSave);
        public Float findLength(URL file);
        /**
         * Finds stream in seconds
         * @param file
         * @param starts - in mills
         * @param length - in mills
         * @return
         */
        public AudioInputStream findInputStream(URL file, Float starts, Float length);
        /**
         *Finds audio file in mils in buffered audio stream
         * @param file
         * @param starts
         * @param length
         * @return
         */
        public AudioInputStream findInputStreamInMils(URL file, Long starts, Long length);
        /**
         * Finds input stream in mils in buffered audio stream
         * 
         * @param outputStream
         * @param starts
         * @param length
         * @return
         */
        public AudioInputStream findInputStreamInMils(ByteArrayOutputStream outputStream, Long starts, Long length,
                AudioFormat audioFormat);
}

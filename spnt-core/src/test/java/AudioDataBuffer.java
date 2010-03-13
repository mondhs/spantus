/*
 *	AudioDataBuffer.java
 *
 *	This file is part of jsresources.org
 */

/*
 * Copyright (c) 2003 by Matthias Pfisterer
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * - Redistributions of source code must retain the above copyright notice,
 *   this list of conditions and the following disclaimer.
 * - Redistributions in binary form must reproduce the above copyright
 *   notice, this list of conditions and the following disclaimer in the
 *   documentation and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS
 * FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE
 * COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT,
 * STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED
 * OF THE POSSIBILITY OF SUCH DAMAGE.
 */

/*
|<---            this code is formatted to fit into 80 columns             --->|
*/

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;



/**	<titleabbrev>AudioDataBuffer</titleabbrev>
	<title>Buffering of audio data in memory</title>

	<formalpara><title>Purpose</title>
	<para>This example serves no useful purpose itself. It is
	only used to demonstrate a programming technique on how
	to buffer audio data in memory.</para>
	</formalpara>

	<formalpara><title>Usage</title>
	<para>
	<cmdsynopsis>
	<command>java AudioDataBuffer</command>
	<arg choice="plain"><replaceable class="parameter">sourcefile</replaceable></arg>
	<arg choice="plain"><replaceable class="parameter">targetfile</replaceable></arg>
	</cmdsynopsis>
	</para></formalpara>

	<formalpara><title>Parameters</title>
	<variablelist>
	<varlistentry>
	<term><replaceable class="parameter">sourcefilefile</replaceable></term>
	<listitem><para>the file name of the file the audio data should be read from.</para></listitem>
	</varlistentry>
	<varlistentry>
	<term><replaceable class="parameter">targetfile</replaceable></term>
	<listitem><para>the file name of the file the audio data should be written to.</para></listitem>
	</varlistentry>
	</variablelist>
	</formalpara>

	<formalpara><title>Bugs, limitations</title>
	<para>no known.</para>
	</formalpara>

	<formalpara><title>Source code</title>
	<para>
	<ulink url="AudioDataBuffer.java.html">AudioDataBuffer.java</ulink>
	</para>
	</formalpara>

*/
public class AudioDataBuffer
{
	/**	Flag for debugging messages.
	 *	If true, some messages are dumped to the console
	 *	during operation.	
	 */
	private static final boolean	DEBUG = false;

	/** The size of the temporary read buffer, in frames.
	 */
	private static final int	BUFFER_LENGTH = 1024;



	public static void main(String[] args)
		throws Exception
	{
		if (args.length != 2)
		{
			printUsageAndExit();
		}
		File	sourceFile = new File(args[0]);
		File	targetFile = new File(args[1]);

		/* Get the type of the source file. We need this information
		   later to write the audio data to a file of the same type.
		*/
		AudioFileFormat fileFormat = AudioSystem.getAudioFileFormat(sourceFile);
		AudioFileFormat.Type	targetFileType = fileFormat.getType();
		AudioFormat audioFormat = fileFormat.getFormat();

		/* Read the audio data into a memory buffer.
		 */
		AudioInputStream inputAIS = AudioSystem.getAudioInputStream(sourceFile);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		int nBufferSize = BUFFER_LENGTH * audioFormat.getFrameSize();
		byte[]	abBuffer = new byte[nBufferSize];
		while (true)
		{
			if (DEBUG) { out("trying to read (bytes): " + abBuffer.length); }
			int	nBytesRead = inputAIS.read(abBuffer);
			if (DEBUG) { out("read (bytes): " + nBytesRead); }
			if (nBytesRead == -1)
			{
				break;
			}
			baos.write(abBuffer, 0, nBytesRead);
		}

		/* Here's the byte array everybody wants.
		 */
		byte[] abAudioData = baos.toByteArray();

		/* And now, write it to a file again.
		 */
		ByteArrayInputStream bais = new ByteArrayInputStream(abAudioData);
		AudioInputStream outputAIS = new AudioInputStream(
			bais, audioFormat,
			abAudioData.length / audioFormat.getFrameSize());
		int	nWrittenBytes = AudioSystem.write(outputAIS,
							  targetFileType,
							  targetFile);
		if (DEBUG) { out("Written bytes: " + nWrittenBytes); }
	}



	private static void printUsageAndExit()
	{
		out("AudioDataBuffer: usage:");
		out("\tjava AudioDataBuffer <sourcefile> <targetfile>");
		System.exit(0);
	}



	private static void out(String strMessage)
	{
		System.out.println(strMessage);
	}
}



/*** AudioDataBuffer.java ***/


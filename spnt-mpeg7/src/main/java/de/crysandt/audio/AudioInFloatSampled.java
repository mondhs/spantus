/*
  Copyright (c) 2002-2006, Holger Crysandt

  This file is part of the MPEG7AudioEnc project.
*/

package de.crysandt.audio;

import java.io.*;
import javax.sound.sampled.*;
import javax.sound.sampled.AudioFormat.Encoding;

/**
* @author <a href="mailto:crysandt@ient.rwth-aachen.de">Holger Crysandt</a>
*/

public class AudioInFloatSampled
	extends AudioInFloat
{

 private final int SIGNAL_LENGTH;

 private DataInputStream dis = null;

 private final boolean is_mono;
 private final float sample_rate;
 private final int bits_per_sample; 
 
 private final AudioFormat src_format;

 public AudioInFloatSampled(AudioInputStream ais,
                             int signal_length)
 {
   this.SIGNAL_LENGTH = signal_length;
   this.src_format = ais.getFormat();

   AudioFormat format = ais.getFormat();

   if( ( format.isBigEndian() == true ) &&
       ( format.getEncoding().equals(Encoding.PCM_SIGNED)) )
   {
     this.dis = new DataInputStream(
                  new BufferedInputStream(ais));
   } else {
   	int size_in_bits = format.getSampleSizeInBits();
   	if (size_in_bits != 8 && size_in_bits!= 16)
   		size_in_bits = 16; 
   		
     format = new AudioFormat(
         format.getSampleRate(),  	// keep Samplerate
         size_in_bits,					// sample size in Bits
         format.getChannels(),    	// keep mono or stereo
         true,                    	// signed
         true);                    	// bigEndian

     this.dis = new DataInputStream(
                  new BufferedInputStream(
                    AudioSystem.getAudioInputStream(
                      format,
                      ais ) ) );
   }
   
   this.is_mono = format.getChannels() == 1;
   this.sample_rate = format.getSampleRate();
   this.bits_per_sample = format.getSampleSizeInBits();
 }

 public AudioInFloatSampled(AudioInputStream ais) {
   this(ais, 4096);
 }

 public AudioInFloatSampled(InputStream is)
     throws IOException, UnsupportedAudioFileException
 {
   this(AudioSystem.getAudioInputStream(is));
 }


 public AudioInFloatSampled(File file)
     throws IOException, UnsupportedAudioFileException
 {
   this(AudioSystem.getAudioInputStream(file));
 }

 public float[] get( )
 {
   float[] buffer = new float[SIGNAL_LENGTH];

   int index=0;
   try {
   	float amplitude = 1 << (bits_per_sample-1);
   	switch (this.bits_per_sample) {
   		case 8: 
   			for (index = 0; index < buffer.length; ++index)
   				buffer[index] = (float) dis.readByte() / amplitude;
   			break;
   		case 16:
   			for (index = 0; index < buffer.length; ++index)
   				buffer[index] = (float) dis.readShort() / amplitude;
   			break;
   		default:
   			throw new java.lang.IllegalArgumentException(
   					this.bits_per_sample + " bits/sample not supported");
   			
		}
   } catch (IOException e) {
     if (index == 0) {
       buffer = null;
     } else {
       float[] buffer_new = new float[index];
       System.arraycopy(buffer, 0, buffer_new, 0, buffer_new.length);
       buffer = buffer_new;
     }
   }
   
   return buffer;
 }

 public AudioFormat getSourceFormat() {
   return src_format;
 }

 public float getSampleRate() {
   return sample_rate;
 }

 public boolean isMono() {
   return is_mono;
 }
}
	
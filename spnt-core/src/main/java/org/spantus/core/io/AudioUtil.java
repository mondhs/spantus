/*
 	Copyright (c) 2009 Mindaugas Greibus (spantus@gmail.com)
 	Part of program for analyze speech signal 
 	http://spantus.sourceforge.net

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>
*/
package org.spantus.core.io;

import javax.sound.sampled.AudioFormat;
/**
 * 
 * @author Mindaugas Greibus
 * 
 * Created Feb 22, 2010
 *
 */
public abstract class AudioUtil {
	public static Double read8(Byte b1, AudioFormat format){
		return b1.doubleValue();
	}
	
//	public static Float read16(int b1, int b2, AudioFormat format){
//		return read16((byte)b1, (byte)b2, format);
//	}
	
	public static Double read16(byte b1, byte b2, AudioFormat format) {
		boolean signed = (format.getEncoding() == AudioFormat.Encoding.PCM_SIGNED);
		boolean bigEndian = (format.isBigEndian());
		int value = 0;
		// deal with endianness
		int hiByte = (bigEndian ? b1 : b2);
		int loByte = (bigEndian ? b2 : b1);
		if (signed) {
			short shortVal = (short) hiByte;
			shortVal = (short) ((shortVal << 8) | (byte) loByte);
			value = shortVal;
		} else {
			value = (hiByte << 8) | loByte;
		}
		return (double) value ;// Short.MAX_VALUE;
	}
	
	public static Byte[] get16(Double f1, AudioFormat format) {
//		Byte[] bs = new Byte[2];
//		boolean signed = (format.getEncoding() == AudioFormat.Encoding.PCM_SIGNED);
		boolean bigEndian = (format.isBigEndian());
		int value = f1.intValue();
		byte b1 =0, b2=0;
		// deal with endianness
//		if (signed) {
			b1 = (byte) (value >> 8);
			b2 = (byte) (value - (b2 << 8));
//		} else {
//			b2 = (byte) (value >> 8);
//		}
		byte hiByte = (bigEndian ? b1 : b2);
		byte loByte = (bigEndian ? b2 : b1);

		return new Byte[]{hiByte, loByte} ;// Short.MAX_VALUE;
	}
	public static Byte[] get8(Double f1, AudioFormat format) {
		return new Byte[]{f1.byteValue()};
	}
}

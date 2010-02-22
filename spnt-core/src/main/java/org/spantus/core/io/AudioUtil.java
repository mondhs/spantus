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
	public static Float read8(Byte b1, AudioFormat format){
		return b1.floatValue();
	}
	
	public static Float read16(Integer b1, Integer b2, AudioFormat format){
		return read16(b1.byteValue(), b2.byteValue(), format);
	}
	
	public static Float read16(Byte b1, Byte b2, AudioFormat format) {
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
		return (float) value ;// Short.MAX_VALUE;
	}
}

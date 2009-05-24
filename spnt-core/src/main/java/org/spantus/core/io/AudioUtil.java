package org.spantus.core.io;

import javax.sound.sampled.AudioFormat;

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

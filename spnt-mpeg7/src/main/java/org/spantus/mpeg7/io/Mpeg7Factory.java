package org.spantus.mpeg7.io;

import org.spantus.core.io.AudioReader;

public abstract class Mpeg7Factory {
	public static AudioReader createAudioReader(){
		return new Mpeg7ReaderImpl();
	}
	public static Mpeg7Writer createMpeg7Writer(){
		return new Mpeg7WriterImpl();
	}
}

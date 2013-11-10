package org.spantus.externals.recognition.sphinx;

import java.io.InputStream;

import org.spantus.core.marker.MarkerSetHolder;

public interface SphinxRecognitionService {

	public abstract MarkerSetHolder recognize(InputStream inputStream,
			String streamName);

	void addKeyword(String keyWord);

}
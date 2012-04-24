package org.spantus.server.wami.dto;

import java.io.InputStream;

public class AudioElement {
	public InputStream stream;

	public AudioElement(InputStream stream) {
		this.stream = stream;
	}

	public InputStream getStream() {
		return stream;
	}

	public void setStream(InputStream stream) {
		this.stream = stream;
	}
}

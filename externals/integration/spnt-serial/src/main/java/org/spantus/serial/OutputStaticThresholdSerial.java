package org.spantus.serial;

import java.io.IOException;
import java.io.Writer;

import org.spantus.core.threshold.OutputStaticThreshold;

public class OutputStaticThresholdSerial extends OutputStaticThreshold {

	SerialWriter out = null;

	@Override
	public Writer getWriter() throws IOException {
		if (out == null) {
			out = new SerialWriter("COM42");
		}
		return out;
	}
}

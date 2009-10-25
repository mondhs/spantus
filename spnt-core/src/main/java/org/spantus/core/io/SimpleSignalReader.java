package org.spantus.core.io;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.net.URL;

import org.spantus.core.extractor.IExtractorInputReader;
import org.spantus.core.extractor.SignalFormat;
import org.spantus.exception.ProcessingException;
import org.spantus.logger.Logger;

public class SimpleSignalReader implements SignalReader {
	Logger log = Logger.getLogger(SimpleSignalReader.class);

	public SignalFormat getFormat(URL url) {
		SignalFormat config = new SignalFormat();

		try {
			// Open the file that is the first
			// command line parameter
			FileInputStream fstream = new FileInputStream(url.getFile());
			// Get the object of DataInputStream
			DataInputStream in = new DataInputStream(fstream);
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			String strLine;
			// Read File Line By Line
			int i = 0;
			while ((strLine = br.readLine()) != null) {
				switch (i++) {
				case 0:
					if (!strLine.matches("\\d*\\.\\d*")) {
						log.debug("Format not supported");
						return null;
					}
					config.setSampleRate(Float.valueOf(strLine));
					break;
				case 1:
					if (!strLine.matches("\\d*")) {
						log.debug("Format not supported");
						return null;
					}
					config.setLength(Integer.valueOf(strLine));
					// length
					break;

				default:
					break;
				}

			}
			// Close the input stream
			in.close();
		} catch (Exception e) {// Catch exception if any
			log.error(e);
		}
		return config;
	}

	public boolean isFormatSupported(URL url) {
		SignalFormat signalFormat = getFormat(url);
		return signalFormat != null;
	}

	public void readSignal(URL url, IExtractorInputReader reader)
			throws ProcessingException {

		try {
			// Open the file that is the first
			// command line parameter
			FileInputStream fstream = new FileInputStream(url.getFile());
			// Get the object of DataInputStream
			DataInputStream in = new DataInputStream(fstream);
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			String strLine;
			// Read File Line By Line
			long sample = 0;
			while ((strLine = br.readLine()) != null) {
				if (sample > 1) {
					Float value = Float.valueOf(strLine);
					reader.put(sample, value);
				}
				sample++;
			}
			reader.pushValues(sample);
			// Close the input stream
			in.close();
		} catch (Exception e) {// Catch exception if any
			log.error(e);
		}
	}

}

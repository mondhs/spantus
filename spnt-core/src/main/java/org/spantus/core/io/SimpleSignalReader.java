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

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.spantus.core.extractor.IExtractorInputReader;
import org.spantus.core.extractor.SignalFormat;
import org.spantus.exception.ProcessingException;
import org.spantus.logger.Logger;
import org.spantus.utils.StringUtils;
/**
 * 
 * @author Mindaugas Greibus
 * 
 * Created Feb 22, 2010
 *
 */
public class SimpleSignalReader extends AbstractSignalReader {
	Logger log = Logger.getLogger(SimpleSignalReader.class);

	private WraperExtractorReader wraperExtractorReader;
	
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
					if (!( strLine.matches("\\d*\\.\\d*")
							|| strLine.matches("\\d*"))) {
						log.debug("Format not supported");
						return null;
					}
					config.setSampleRate(Double.valueOf(strLine));
					break;
				case 1:
					if (!strLine.matches("\\d*")) {
						log.debug("Format not supported");
						return null;
					}
					config.setLength(Double.valueOf(strLine));
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
		wraperExtractorReader = new WraperExtractorReader(reader, 1);
		log.debug("[readSignal]Reading signal with config: {0}", reader.getConfig() );
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
					Double value = Double.valueOf(strLine);
					wraperExtractorReader.put(value);
//					reader.put(sample, value);
				}
				sample++;
			}
			wraperExtractorReader.pushValues();
			// Close the input stream
			in.close();
		} catch (Exception e) {// Catch exception if any
			log.error(e);
		}
	}

	public void readSignal(List<URL> urls, IExtractorInputReader reader)
			throws ProcessingException {
		log.debug("[readSignal]Reading signal with config: {0}", reader.getConfig() );
		List<BufferedReader> bfreaders = new ArrayList<BufferedReader>();
		List<DataInputStream> inputs = new ArrayList<DataInputStream>();
		wraperExtractorReader = new WraperExtractorReader(reader, urls.size());
		
 		try {
 			for (URL file : urls) {
				if(file!=null){
					// Open the file that is the first
					// command line parameter
					FileInputStream fstream = new FileInputStream(file.getFile());
					// Get the object of DataInputStream
					DataInputStream in = new DataInputStream(fstream);
					inputs.add(in);
					BufferedReader br = new BufferedReader(new InputStreamReader(in));
					bfreaders.add(br);
				}
			}
 			boolean filesEnded = false;
 			Long total = 0L;
 			//read sample rate
				for (BufferedReader bufferedReader : bfreaders) {
					total = Long.valueOf(bufferedReader.readLine());
				}
				//read samples size
				for (BufferedReader bufferedReader : bfreaders) {
						bufferedReader.readLine();
				}
				//notify listeners
	 			started(total);

// 			long sample = 0;
 			// Read File Line By Line
 			while(!filesEnded){
 				Double sum = 0D;
 				
 				for (BufferedReader bufferedReader : bfreaders) {
 					String strLine = bufferedReader.readLine();
 					if(StringUtils.hasText(strLine)){
 						sum += Double.valueOf(strLine);
 					}else{
 						filesEnded = true;
 					}
 				}	
// 				if (sample > 1) {
 					wraperExtractorReader.put(sum);
 					processed(wraperExtractorReader.getSample(), total);
// 					reader.put(sample, sum);
// 				}
// 				sample++;
			}
 			wraperExtractorReader.pushValues();
			// Close the input stream
			for (DataInputStream in : inputs) {
				in.close();
			}
			ended();
 		} catch (NumberFormatException e) {
			log.error(e);
			throw new ProcessingException(e);
 		} catch (Exception e) {// Catch exception if any
			log.error(e);
			throw new ProcessingException(e);
		}
	}
	

}

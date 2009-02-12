package org.spantus.demo.services;

import java.io.File;
import java.io.FilenameFilter;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.spantus.demo.dto.SampleDto;
import org.spantus.logger.Logger;

public class SamplesServiceImpl implements SampleService {
	Logger log = Logger.getLogger(getClass());

	
	public List<SampleDto> getSamples() {
		List<SampleDto> samples = new ArrayList<SampleDto>();
		String[] sampleNames = new String[]{"text1.wav", "text1.mpeg7.xml", "sohn.wav", "sohn.mpeg7.xml"};
		for (String sampleName : sampleNames) {
			samples.add(createSample("/",sampleName));
		}
		try{
			ResourceBundle wavListBundle = ResourceBundle.getBundle("work.wavlist");
			String wavlist = wavListBundle.getString("wavlist");
			wavlist.split(", ");
			for (String fileName: wavlist.split(", ")) {
				samples.add(createSample("/work/",fileName));
			}
		}catch (MissingResourceException e) {
			log.debug("additional work samples is not loaded: " + e.getMessage());
		}
		return samples;
	}
	private SampleDto createSample(String path, String name){
		SampleDto sample = new SampleDto();
		sample.setTitle(name);
		URL url = getClass().getResource(path + name);
		if(url == null){
			throw new IllegalArgumentException("Filen not found: " + path + name);
		}
		sample.setUrl(url);
		return sample;

	}
	
	class WavFilter implements FilenameFilter{
		
		public boolean accept(File dir, String name) {
			return (name.endsWith(".wav"));
		}
		
	}

}

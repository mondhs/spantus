package org.spantus.extr.wordspot.service.impl;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.thoughtworks.xstream.XStream;

public class AcceptableSyllableThresholdDaoImpl {
	private static final String SEGMENT_THRESHOLD_XML = "segment-threshold.xml";

	private XStream xstream;
	private static final Logger LOG = LoggerFactory
			.getLogger(AcceptableSyllableThresholdDaoImpl.class);
	
	public AcceptableSyllableThresholdDaoImpl() {
	}
	
	public void write( Map<String, Double> acceptableSyllableThresholdMap) {
		FileWriter outputFile;
		try {
			outputFile = new FileWriter("./target/"+SEGMENT_THRESHOLD_XML,
					false);
			getXsteam().toXML(acceptableSyllableThresholdMap, outputFile);
		} catch (IOException e) {
			LOG.error("Cannot read", e);
		}
	}
	
	public Map<String, Double> read(String repositoryPathWord, String type) {
		Map<String, Double> acceptableSyllableThresholdMap = null;
		try {
			String targetRepository = repositoryPathWord
					+ "/"+SEGMENT_THRESHOLD_XML;
			if(type!=null){
				targetRepository = repositoryPathWord
						+ "/../"+ type +"/"+SEGMENT_THRESHOLD_XML;
			}
			@SuppressWarnings("unchecked")
			Map<String, Double> testAcceptableSyllableThresholdMap = (Map<String, Double>) getXsteam()
					.fromXML(
							new FileReader(targetRepository));
			if (testAcceptableSyllableThresholdMap != null) {
				acceptableSyllableThresholdMap = testAcceptableSyllableThresholdMap;
			} else {
				LOG.debug("There is no segment-threashold.xml in {}",
						repositoryPathWord);
			}
		} catch (IOException e) {
			LOG.error("Cannot read", e);
		}
		return acceptableSyllableThresholdMap;
	}
	

	protected XStream getXsteam() {
		if (xstream == null) {
			xstream = new XStream();
		}
		return xstream;
	}
}

package org.spantus.work.services.impl;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import org.spantus.core.extractor.dao.MarkerDao;
import org.spantus.core.marker.MarkerSetHolder;
import org.spantus.logger.Logger;

// import com.thoughtworks.xstream.XStream;
// import com.thoughtworks.xstream.converters.enums.EnumConverter;

public class MarkerYamlDaoImpl implements MarkerDao {

	// private XStream xstream = null;

	protected Logger log = Logger.getLogger(getClass());
	

	public void write(MarkerSetHolder holder, File file) {
		try {
			FileWriter outputFile = new FileWriter(file,false);
			ObjectMapper om = getObjectMapper();
			om.writeValue(file,holder);
			// getXsteam().toXML(holder, outputFile);
			log.debug("Markers are exported: " + file.toString());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public void write(MarkerSetHolder holder, OutputStream outputStream) {
		// getXsteam().toXML(holder, outputStream);		
	}

	
	public MarkerSetHolder read(File file) {
		MarkerSetHolder holder = new MarkerSetHolder();
		try {
//			FileReader inFile = new FileReader(file);
			ObjectMapper om = getObjectMapper();
			holder = om.readValue(file,MarkerSetHolder.class);
			// getXsteam().fromXML(inFile, holder);
			log.debug("markers file read correctly. info: " + file.getAbsolutePath());
		} catch (FileNotFoundException e) {
			log.debug("Marker file not found: " + file);
			holder = null;
		} catch (JsonParseException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return holder;
	}
	public MarkerSetHolder read(InputStream inputStream) {
		MarkerSetHolder holder = new MarkerSetHolder();
		ObjectMapper om = getObjectMapper();
		try {
			holder = om.readValue(inputStream, MarkerSetHolder.class);
		} catch (IOException e) {
			e.printStackTrace();
		}
		// getXsteam().fromXML(inputStream, holder);
		log.debug("markers file read correctly. info: ");
		return holder;
	}

	private ObjectMapper getObjectMapper(){
		ObjectMapper om = new ObjectMapper(new YAMLFactory());
		return om;
	}
	
// 	protected XStream getXsteam(){
// 		if(xstream == null){
// 			xstream = new XStream();
// //			xstream.omitField(type, fieldName);
// 			xstream.alias(MarkerSetHolder.class.getSimpleName(), MarkerSetHolder.class);
// 			xstream.alias(MarkerSet.class.getSimpleName(), MarkerSet.class);
// 			xstream.alias(Marker.class.getSimpleName(), Marker.class);
// 			xstream.registerConverter(new EnumConverter());
// 		}
// 		return xstream;
// 	}
	
}

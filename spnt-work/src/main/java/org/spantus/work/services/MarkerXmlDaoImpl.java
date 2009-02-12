package org.spantus.work.services;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.spantus.core.marker.Marker;
import org.spantus.core.marker.MarkerSet;
import org.spantus.core.marker.MarkerSetHolder;
import org.spantus.logger.Logger;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.enums.EnumConverter;

public class MarkerXmlDaoImpl implements MarkerDao {

	private XStream xstream = null;

	protected Logger log = Logger.getLogger(getClass());
	

	public void write(MarkerSetHolder holder, File file) {
		try {
			FileWriter outputFile = new FileWriter(file,false);	
			getXsteam().toXML(holder, outputFile);
			log.debug("Markers are exported: " + file.toString());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public void write(MarkerSetHolder holder, OutputStream outputStream) {
		getXsteam().toXML(holder, outputStream);		
	}

	
	public MarkerSetHolder read(File file) {
		MarkerSetHolder holder = new MarkerSetHolder();
		try {
			FileReader inFile = new FileReader(file);
			getXsteam().fromXML(inFile, holder);
			log.debug("markers file read correctly. info: " + file.getAbsolutePath());
		} catch (FileNotFoundException e) {
			log.debug("Marker file not found: " + file);
			holder = null;
		}

		return holder;
	}
	public MarkerSetHolder read(InputStream inputStream) {
		MarkerSetHolder holder = new MarkerSetHolder();
		getXsteam().fromXML(inputStream, holder);
		log.debug("markers file read correctly. info: ");
		return holder;
	}
	
	protected XStream getXsteam(){
		if(xstream == null){
			xstream = new XStream();
			xstream.alias(MarkerSetHolder.class.getSimpleName(), MarkerSetHolder.class);
			xstream.alias(MarkerSet.class.getSimpleName(), MarkerSet.class);
			xstream.alias(Marker.class.getSimpleName(), Marker.class);
			xstream.registerConverter(new EnumConverter());
		}
		return xstream;
	}
	
}

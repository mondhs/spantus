package org.spantus.work.services.impl;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import org.spantus.core.FrameValues;
import org.spantus.core.FrameVectorValues;
import org.spantus.core.extractor.DefaultExtractorConfig;
import org.spantus.core.extractor.ExtractorOutputHolder;
import org.spantus.core.extractor.ExtractorVectorOutputHolder;
import org.spantus.core.extractor.IExtractor;
import org.spantus.core.extractor.IExtractorInputReader;
import org.spantus.core.extractor.IExtractorVector;
import org.spantus.core.extractor.dao.ReaderDao;
import org.spantus.exception.ProcessingException;
import org.spantus.logger.Logger;
// import org.spantus.work.services.converter.FrameValues3DConverter;
// import org.spantus.work.services.converter.FrameValuesConverter;

// import com.thoughtworks.xstream.XStream;
// import com.thoughtworks.xstream.converters.ConversionException;
// import com.thoughtworks.xstream.converters.enums.EnumConverter;
import org.spantus.work.services.converter.*;

public class ReaderYamlDaoImpl implements ReaderDao {

	// private XStream xstream = null;

	protected Logger log = Logger.getLogger(getClass());


	public IExtractorInputReader read(File file) {
		IExtractorInputReader reader = null;
		try {
//			FileReader inFile = new FileReader(file);
			ObjectMapper om = getYamlMapper();
			reader = om.readValue(file, ExportExtractorInputReader.class);
//			reader = (IExtractorInputReader)getXsteam().fromXML(inFile);
			log.debug("extractors file read correctly. info: " + file.getAbsolutePath());
			// }catch (ConversionException e) {
			// 	log.debug("error while reading: " + file.getAbsolutePath());
			// 	throw new ProcessingException(e);
		} catch (FileNotFoundException e) {
			throw new ProcessingException(e);
		} catch (JsonParseException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return reader;
	}

	public IExtractorInputReader read(InputStream inputStream) {
		IExtractorInputReader reader = null;
		ObjectMapper om = getYamlMapper();
		try {
			reader = om.readValue(inputStream, IExtractorInputReader.class);
		} catch (IOException e) {
			e.printStackTrace();
		}
		// reader = (IExtractorInputReader)getXsteam().fromXML(inputStream);
		log.debug("extractors file read correctly. info ");
		return reader;
	}

	public void write(IExtractorInputReader holder, File file) {
		try {
			FileWriter outputFile = new FileWriter(file, false);
			ObjectMapper om = getYamlMapper();
			ExportExtractorInputReader preparedToSave = prepareToSave(holder);
			om.writeValue(file, preparedToSave);
			// getXsteam().toXML(prepareToSave(holder), outputFile);
			log.debug("reader are exported: " + file.toString());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void write(IExtractorInputReader holder, OutputStream outputStream) {
		// getXsteam().toXML(prepareToSave(holder), outputStream);
	}

	protected ExportExtractorInputReader prepareToSave(IExtractorInputReader reader) {
		ExportExtractorInputReader rtnReader = new ExportExtractorInputReader();
		for (IExtractor extractor : reader.getExtractorRegister()) {
			ExtractorOutputHolder holder = new ExtractorOutputHolder();
			holder.setName(extractor.getName());
			holder.setOutputValues(extractor.getOutputValues());
			rtnReader.registerExtractor(holder);
		}
		for (IExtractorVector extractor : reader.getExtractorRegister3D()) {
			ExtractorVectorOutputHolder holder = new ExtractorVectorOutputHolder();
			holder.setName(extractor.getName());
			holder.setOutputValues(extractor.getOutputValues());
			rtnReader.registerExtractor(holder);
		}
		rtnReader.setConfig(new DefaultExtractorConfig(reader.getConfig()));
		return rtnReader;
	}

	ObjectMapper getYamlMapper() {
		ObjectMapper om = new ObjectMapper(new YAMLFactory());
		SimpleModule module = new SimpleModule();
		module.addSerializer(FrameValues.class, new FrameValuesSerializer());
		module.addSerializer(FrameVectorValues.class, new FrameVectorValuesSerializer());

		//module.addDeserializer(FrameValues.class, new FrameValuesDestabilizer());
		module.addDeserializer(IExtractor.class, new IExtractorDeserializer());
		module.addDeserializer(IExtractorVector.class, new IExtractorVectorDeserializer());


//		module.addDeserializer(AudioFormat.Encoding.class, new EncodingDeserializer());
		om.registerModule(module);
		return om;
	}

	// protected XStream getXsteam(){
	// 	if(xstream == null){
	// 		xstream = new XStream();
	// 		xstream.registerConverter(new EnumConverter());
	// 		xstream.registerConverter(new FrameValuesConverter());
	// 		xstream.registerConverter(new FrameValues3DConverter());
	// 	}
	// 	return xstream;
	// }

}

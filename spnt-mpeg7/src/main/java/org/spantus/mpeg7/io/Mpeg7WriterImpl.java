package org.spantus.mpeg7.io;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.spantus.core.extractor.IExtractorConfig;
import org.spantus.exception.ProcessingException;
import org.spantus.mpeg7.config.Mpeg7ConfigUtil;
import org.w3c.dom.Document;

import de.crysandt.audio.mpeg7audio.MP7DocumentBuilder;
import de.crysandt.xml.Namespace;

public class Mpeg7WriterImpl implements Mpeg7Writer {

	public void write(URL inUrl, File outFile, IExtractorConfig conf)
			throws ProcessingException {
		try {
			Document mpeg7doc = MP7DocumentBuilder.encode(AudioSystem
					.getAudioInputStream(inUrl), Mpeg7ConfigUtil
					.getConfig(conf));

			// create MPEG-7 DocumentBuilder
			MP7DocumentBuilder mp7out = new MP7DocumentBuilder();
			mp7out
					.addSchemaLocation(Namespace.MPEG7,
							"http://www.ient.rwth-aachen.de/team/crysandt/mpeg7mds/mpeg7ver1.xsd");

			// MediaInformation mi = MediaHelper.createMediaInformation();
			// MediaHelper.setMediaLocation(mi, inUri);
			// mp7out.setMediaInformation(mi);

			// get MPEG-7 description
			// Document mp7 = mp7out.getDocument();
			write(mpeg7doc, outFile);
		} catch (ParserConfigurationException e) {
			throw new ProcessingException(e);
		} catch (UnsupportedAudioFileException e) {
			throw new ProcessingException(e);
		} catch (IOException e) {
			throw new ProcessingException(e);
		}

	}

	public void write(Document mpeg7doc, File outFile)
			throws ProcessingException {

		MP7DocumentBuilder mp7out = new MP7DocumentBuilder();
		mp7out
				.addSchemaLocation(Namespace.MPEG7,
						"http://www.ient.rwth-aachen.de/team/crysandt/mpeg7mds/mpeg7ver1.xsd");

		// MediaInformation mi = MediaHelper.createMediaInformation();
		// MediaHelper.setMediaLocation(mi, inUri);
		// mp7out.setMediaInformation(mi);

		// get MPEG-7 description
		// Document mp7 = mp7out.getDocument();

		// initialize output format
		TransformerFactory transformerFactory = TransformerFactory
				.newInstance();
		Transformer transformer;
		try {
			transformer = transformerFactory.newTransformer();
		} catch (TransformerConfigurationException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
		transformer.setOutputProperty(OutputKeys.METHOD, "xml");
		transformer.setOutputProperty(OutputKeys.INDENT, "yes");

		Result result = null;
		if (outFile == null) {
			result = new StreamResult(System.out);
		} else {
			result = new StreamResult(outFile);
		}
		// write MPEG-7 description to file
		try {
			transformer.transform(new DOMSource(mpeg7doc), result);
		} catch (TransformerException e) {
			throw new RuntimeException(e);
		}

	}
}

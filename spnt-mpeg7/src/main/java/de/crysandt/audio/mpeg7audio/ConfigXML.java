/*
  Copyright (c) 2002-2006, Holger Crysandt

  This file is part of MPEG7AudioEnc.
*/

package de.crysandt.audio.mpeg7audio;

/**
 * @author <a href="mailto:crysandt@ient.rwth-aachen.de">Holger Crysandt</a>
 */
import java.io.*;
import java.net.URL;
import java.util.*;

import javax.xml.parsers.*;

import org.w3c.dom.*;
import org.xml.sax.*;
import org.xml.sax.helpers.XMLReaderFactory;

import de.crysandt.util.Debug;
import de.crysandt.xml.Namespace;
@SuppressWarnings("unchecked")
public class ConfigXML {
//	private static String MP7AE = "http://mpeg7audioenc.sf.net/mpeg7audioenc.xsd"; 
	
	public static Config parse(Reader reader, Config config)
		throws SAXException, IOException
	{
		XMLReader xml_reader = XMLReaderFactory.createXMLReader();
		ConfigXMLContentHandler ch = new ConfigXMLContentHandler(config);

		/*
		 * enable validation of config file;
		 * only works with xerces, crimson needs a DTD.
		 */
		try {
			xml_reader.setFeature(
					"http://apache.org/xml/features/validation/schema",
					true);

			xml_reader.setFeature(
					"http://apache.org/xml/features/validation/schema-full-checking",
					true);

			// try to use schema within archive file
			URL url = Thread.currentThread().getContextClassLoader().getResource(
					"mpeg7audioenc.xsd");

			if (url != null) {
				assert Debug.println(
						System.err,
						"Using mpeg7audioenc.xsd included in CLASSPATH");

				xml_reader.setProperty(
						"http://java.sun.com/xml/jaxp/properties/schemaSource",
						url.openStream());
			} else {
				assert Debug.println(
						System.err,
						"Can't open local copy of mpeg7audioenc.xsd");
			}

		} catch (SAXNotRecognizedException e) {
			assert Debug.println(System.err,
					"SAXNotRecognizedException:" + e.getMessage());
		}

		// parse xml-file
		xml_reader.setContentHandler(ch);
		xml_reader.setErrorHandler(ch);
		xml_reader.parse(new InputSource(reader));

		return ch.getConfig();
	}

	public static Config parse(Reader reader)
		throws SAXException, IOException
	{
		Config config = new ConfigDefault();
		config.enableAll(false);
		return parse(reader, config);
	}

	/**
	 * Creates (XML-) Document of a configuration
	 * @param config Configuration to be transformed
	 * @return Returns (XML-) Documnet
	 */
	public static Document toDocument(Config config)
		throws ParserConfigurationException
	{
		DocumentBuilderFactory doc_factory = DocumentBuilderFactory.newInstance();

		doc_factory.setNamespaceAware(true);
//		doc_factory.setValidating(true);
//		doc_factory.setExpandEntityReferences(true);
//		doc_factory.setAttribute(JAXP_SCHEMA_LANGUAGE, W3C_XML_SCHEMA);

		DocumentBuilder doc_builder = doc_factory.newDocumentBuilder();

		Document doc = doc_builder.newDocument();

		// create root element
		Element root = doc.createElementNS(Namespace.MPEG7AE, "Config");
		doc.appendChild(root);

		// add namespace
		root.setAttributeNS(Namespace.XMLNS, "xmlns", Namespace.MPEG7AE);
		root.setAttributeNS(Namespace.XMLNS, "xmlns:mp7ae", Namespace.MPEG7AE);
		root.setAttributeNS(Namespace.XMLNS, "xmlns:xsi", Namespace.XSI);

		Config config_default = new ConfigDefault();

		SortedSet keys = new TreeSet(config_default.config.keySet());
		
		for (Iterator i=keys.iterator(); i.hasNext(); ) {
			String key = i.next().toString();
			if (key.endsWith("_enable")) {
				String module = key.substring(0, key.indexOf("_enable"));
				if (config.getBoolean(module, "enable")) {
					Element child = doc.createElementNS(Namespace.MPEG7AE, "Module");
					child.setAttributeNS(Namespace.XSI, "xsi:type", module);
					child.setAttributeNS(Namespace.MPEG7AE, "mp7ae:enable", Boolean.TRUE.toString());
					root.appendChild(child);
					
					for (Iterator j=keys.iterator(); j.hasNext(); ) {
						String module_name = j.next().toString();
						if (module_name.startsWith(module)) {
							String name = module_name.substring(
									module.length()+1,module_name.length());
							if (!name.equals("enable")) {
								Element parameter = doc.createElement(name);
								parameter.appendChild(doc.createTextNode(
									config.getString(module, name)));
								child.appendChild(parameter);
							}
						}
					}
				}
			}
		}

		return doc;
	}
}

class ConfigXMLContentHandler
	implements ContentHandler, ErrorHandler
{
	private static final String XSI = "http://www.w3.org/2001/XMLSchema-instance";

	private final Config config;
	private String module = null;
	private String name = null;
	private StringBuffer buffer = null;

	public ConfigXMLContentHandler(Config config) {
		this.config = config;
	}

	public ConfigXMLContentHandler() {
		this(getDefaultConfig());
	}

	public Config getConfig() {
		return config;
	}

	private static Config getDefaultConfig() {
		Config config = new ConfigDefault();
		config.enableAll(false);
		return config;
	}

	public void startElement(
			String namespaceURI,
			String localName,
			String rawName,
			Attributes atts)
	{
		if (localName.equals("Module")) {
			module = atts.getValue( XSI, "type");
			String enabled = atts.getValue("enable");
			if ((enabled==null) || (enabled.equals("")))
				enabled = "" + true;
			config.setValue(module, "enable", enabled);
		} else if (module != null) {
			name = localName;
			buffer = new StringBuffer();
		}
	}

	public void endElement(
			String namspaceURI,
			String localName,
			String rawName)
	{
		if (localName.equals("Module")) {
			module = null;
		} else if ((module != null)) {
			assert(name.equals(localName));
			config.setValue(module, name, buffer.toString().trim());
			buffer = null;
		}
	}

	public void characters(char[] ch, int start, int length) {
		if (buffer != null)
			buffer.append(ch, start, length);
	}

	public void error(SAXParseException e)
		throws SAXParseException
	{
		throw e;
	}

	public void fatalError(SAXParseException e)
		throws SAXParseException
	{
		throw e;
	}

	public void warning(SAXParseException e) {
		assert Debug.println(System.err, "warning: "+ e.getMessage());
	}

	public void startDocument(){}

	public void endDocument(){}

	public void ignorableWhitespace(char[] ch, int start, int length) { }

	public void processingInstruction(String target, String data) { }

	public void setDocumentLocator(Locator locator) { }

	public void skippedEntity(String s){ }

	public void startPrefixMapping(String prefix, String uri) { }

	public void endPrefixMapping(String prefix) { }
}

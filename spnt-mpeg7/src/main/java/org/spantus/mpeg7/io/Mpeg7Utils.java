package org.spantus.mpeg7.io;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.spantus.core.extractor.IExtractor;
import org.spantus.core.extractor.IExtractorInputReader;
import org.spantus.core.extractor.IExtractorVector;
import org.spantus.core.extractor.IGeneralExtractor;
import org.spantus.exception.ProcessingException;
import org.spantus.utils.Assert;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public abstract class Mpeg7Utils {
	
	public static final String DEFAULT_MEDIA_DURATION_PATTERN = "^PT(\\d+)N1000F$";
	
	enum Mpeg7nodes {
		Mpeg7, AudioDescriptor, SeriesOfScalar, SeriesOfVector, Scalar, Raw, Weight, Min, Max, Mean
	}

	enum Mpeg7attrs {
		totalNumOfSamples("totalNumOfSamples"), vectorSize("vectorSize"), xsi_type(
				"xsi:type"), hopSize("hopSize"), octaveResolution(
				"octaveResolution");
		private String attr;

		Mpeg7attrs(String attr) {
			this.attr = attr;
		}

		public String getAttr() {
			return this.attr;
		}
	}

	public static Document readDocument(URI uriFile) throws ProcessingException {
		DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory
				.newInstance();
		DocumentBuilder docBuilder;
		Document doc = null;
		try {
			docBuilder = docBuilderFactory.newDocumentBuilder();
			doc = docBuilder.parse(uriFile.toString());
			if (!Mpeg7nodes.Mpeg7.name().equals(
					doc.getDocumentElement().getNodeName())) {
				throw new IllegalArgumentException(
						"root node is not acceptible: "
								+ doc.getDocumentElement().getNodeName());
			}
		} catch (ParserConfigurationException e) {
			throw new ProcessingException(e);
		} catch (SAXException e) {
			throw new ProcessingException(e);
		} catch (IOException e) {
			throw new ProcessingException(e);
		}
		return doc;
	}

	public static List<Element> getAudioDescriptors(Document doc) {
		List<Element> descriptors = new ArrayList<Element>();
		NodeList list = doc.getElementsByTagName(Mpeg7nodes.AudioDescriptor
				.name());
		for (int i = 0; i < list.getLength(); i++) {
			Element descriptor = (Element) list.item(i);
			if("AudioHarmonicityType".equals(getAttr(descriptor, Mpeg7attrs.xsi_type))){
				for (int j = 0; j < descriptor.getChildNodes().getLength(); j++) {
					Node node = descriptor.getChildNodes().item(j); 
					if( node instanceof Element){
						Element subdescriptor = (Element)(node);
						subdescriptor.setAttribute(Mpeg7attrs.xsi_type.getAttr(), subdescriptor.getNodeName());
						descriptors.add(subdescriptor);
					}
				}
			}else{
				descriptors.add(descriptor);
			}
					
//			if(descriptor.getChildNodes().getLength()==1){
//				
//			}
//			else{
//				for (int j = 0; j < descriptor.getChildNodes().getLength(); j++) {
//					Node node = descriptor.getChildNodes().item(j); 
//					if( node instanceof Element){
//						Element subdescriptor = (Element)(node.getParentNode());
//						descriptors.add(subdescriptor);
//					}
//				}
//			}
		}
		return descriptors;
	}

	public static Element getFirstElement(Element element, Mpeg7nodes mpeg7nodes) {
		NodeList list = element.getElementsByTagName(mpeg7nodes.name());
		Element seriesOfScalar = null;
		if(list.getLength()==1){
			seriesOfScalar = (Element) list.item(0);
		}
		
		return seriesOfScalar;

	}

	/**
	 * 
	 * @param seriesOfScalar
	 * @param name
	 * @return
	 */
	public static String[] readRaw(Element seriesOfValues, Mpeg7nodes node) {
		NodeList list = seriesOfValues.getElementsByTagName(node.name());
		if (list.getLength() == 0) {
			return null;
		}
		Element raw = (Element) list.item(0);
		String values = raw.getFirstChild().getNodeValue();
		String[] strs = values.split("[\\s]+");
		return strs;
	}

	public static String readScalar(Element scalar) {
		if (scalar == null) {
			return null;
		}
		String value = scalar.getFirstChild().getNodeValue();
		return value;
	}

	public static String getAttr(Element element, Mpeg7attrs mpeg7attrs) {
		String attr = element.getAttribute(mpeg7attrs.getAttr());
		return attr;
	}

	public static boolean register(IExtractorInputReader reader,
			IGeneralExtractor extractor) {
		Assert.isTrue(extractor!=null, "Extractor is null");
		if (extractor instanceof IExtractorVector) {
			reader.getExtractorRegister3D().add((IExtractorVector) extractor);
			return true;
		} else if (extractor instanceof IExtractor) {
			reader.getExtractorRegister().add((IExtractor) extractor);
			return true;
		}
		return false;
	}

	public static int getMediaDuration(String durationStr) {
		// PT10N1000F
		int duration = 0;
		Pattern mediaDurationExp = Pattern
				.compile(DEFAULT_MEDIA_DURATION_PATTERN);
		Matcher matcher = mediaDurationExp.matcher(durationStr);
		if (matcher.matches()) {
			MatchResult matchResult = matcher.toMatchResult();
			Object[] objs = new Object[matchResult.groupCount()];
			for (int i = 0; i < matchResult.groupCount(); i++) {
				objs[i] = matchResult.group(i + 1);
			}
			if (objs.length == 1) {
				duration = Integer.valueOf(objs[0].toString());
			}
		}

		return duration;

	}

	public static void traverseDOMBranch(Node node, StringBuilder bld) {
		// do what you want with this node here...
		bld.append(node.getNodeName()).append(":").append(node.getNodeValue())
				.append("\n");
		if (node.hasChildNodes()) {
			NodeList nl = node.getChildNodes();
			int size = nl.getLength();
			for (int i = 0; i < size; i++) {
				traverseDOMBranch(nl.item(i), bld);
			}
		}
	}

}

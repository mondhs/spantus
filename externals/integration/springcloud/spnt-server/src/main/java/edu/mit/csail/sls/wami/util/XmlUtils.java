package edu.mit.csail.sls.wami.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/**
 * Basic utils for XML manipulation.
 */
public class XmlUtils {
        // //////////////////////////
        // // XML STUFF ////////
        // //////////////////////////
        private static final DocumentBuilderFactory factory = DocumentBuilderFactory
                        .newInstance();

        private static final TransformerFactory tFactory = TransformerFactory
                        .newInstance();

        public static Document toXMLDocument(String xmlString) {
                return toXMLDocument(new InputSource(new StringReader(xmlString)));
        }

        public static Document toXMLDocument(InputStream in) {
                return toXMLDocument(new InputSource(in));
        }

        public static Document toXMLDocument(InputSource source) {
                Document xmlDoc = null;

                try {
                        xmlDoc = getBuilder().parse(source);
                } catch (SAXException e) {
                        e.printStackTrace();
                } catch (IOException e) {
                        e.printStackTrace();
                }

                return xmlDoc;
        }

        public static DocumentBuilder getBuilder() {
                try {
                        return factory.newDocumentBuilder();
                } catch (ParserConfigurationException e) {
                        return null;
                }
        }

        public static Document newXMLDocument() {
                Document document = getBuilder().newDocument();
                return document;
        }

        /**
         * Convert an XML node to a string. Node can be a document or an element.
         * 
         * @param node
         * @return
         */
        public static String toXMLString(Node node) {
                Transformer transformer = null;
                try {
                        transformer = tFactory.newTransformer();
                        // System.err.format("Using transformer: %s%n", transformer);
                } catch (TransformerConfigurationException e) {
                        e.printStackTrace();
                }
                DOMSource source = new DOMSource(node);
                StringWriter xmlWriter = new StringWriter();
                StreamResult result = new StreamResult(xmlWriter);
                try {
                        transformer.transform(source, result);
                } catch (TransformerException e) {
                        e.printStackTrace();
                }
                return xmlWriter.toString();
        }

        public static class ValidationErrorHandler implements ErrorHandler {
                public void error(SAXParseException e) throws SAXException {
                        System.out.println(e);
                }

                public void fatalError(SAXParseException e) throws SAXException {
                        System.out.println(e);
                }

                public void warning(SAXParseException e) throws SAXException {
                        System.out.println(e);
                }
        }

}

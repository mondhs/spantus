package com.giantflyingsaucer;

import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

/**
 * 
 * @author Chad Lung Posted on August 19, 2010 by Chad Lung
 *         http://www.giantflyingsaucer.com/blog/?p=1462
 * 
 * 
 */
// Code from:
// http://lukencode.com/2010/04/27/calling-web-services-in-android-using-httpclient/
public class RestClientDOM extends RestClientString {

	public RestClientDOM(String url) {
		super(url);
	}

	Document response;

	public Document getResponseDOM() {
		return response;
	}

	@Override
	protected void convertStreamToResponse(InputStream is) {
		DocumentBuilder builder;
		try {
			builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			Document doc = builder.parse(is);
			response = doc;
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
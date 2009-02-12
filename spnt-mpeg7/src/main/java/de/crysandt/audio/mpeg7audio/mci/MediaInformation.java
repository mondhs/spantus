/*
 * Created on Jun 30, 2004
 *
 * This file is part of the MPEG7AudioEnc project.
 */
package de.crysandt.audio.mpeg7audio.mci;

import java.io.*;

import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.*;
import javax.xml.transform.stream.*;

import org.w3c.dom.*;

import de.crysandt.util.*;
import de.crysandt.xml.*;

/**
 * @author Michael.Lambertz@rwth-aachen.de
 *
 */
public class MediaInformation implements DescriptionSchemeI
{
/*	<!-- ########################################### -->
	<!--  Definition of MediaInformation DS (8.2.1)  -->
	<!-- ########################################### -->
	<!-- Definition of MediaInformation DS -->
	<complexType name="MediaInformationType">
		<complexContent>
			<extension base="mpeg7v1:DSType">
				<sequence>
					<element name="MediaIdentification" type="mpeg7v1:MediaIdentificationType" minOccurs="0"/>
					<element name="MediaProfile" type="mpeg7v1:MediaProfileType" maxOccurs="unbounded"/>
				</sequence>
			</extension>
		</complexContent>
	</complexType>	*/

private MediaIdentification				media_identity;		// cardinality: 0 - 1
private VectorTyped						media_profiles;		// cardinality: 1 - n

public MediaInformation( MediaProfile profile )
{
	media_identity = null;
	media_profiles = new VectorTyped( MediaProfile.class );
	media_profiles.add(profile);
}

public Element toXML( Document doc, String name )
{
Element		info_ele;
int			i;

	info_ele = doc.createElementNS(Namespace.MPEG7, name);
	info_ele.setAttributeNS(Namespace.XSI, "xsi:type", "MediaInformationType");

	if ( media_identity != null )
		info_ele.appendChild(media_identity.toXML(doc, "MediaIdentification"));

	for ( i = 0; i < media_profiles.size(); ++i )
		info_ele.appendChild(((MediaProfile)media_profiles.get(i)).toXML(doc, "MediaProfile"));

	return(info_ele);
}

public MediaIdentification getMediaIdentification( )
{
	return(media_identity);
}

public void setMediaIdentification( MediaIdentification media_identity )
{
	this.media_identity = media_identity;
}

/**
 * @return "VectorTyped" with elements of type "MediaProfile"
 */
public VectorTyped getMediaProfiles( )
{
	return(media_profiles);
}

/**
 * Inserted for testing purposes. 
 */
public static void main( String[] args )
{
MediaInformation		media_info;

DocumentBuilderFactory	doc_factory;
DocumentBuilder			doc_builder;
Document				doc;
TransformerFactory		trans_factory;
Transformer				trans;

	try
	{
		media_info = MediaHelper.createMediaInformation();
	
		MediaHelper.setMediaLocation(media_info, new File("test.mp3").toURI());
		MediaHelper.setContentType(media_info, MFContent.AUDIO);
		MediaHelper.setChannels(media_info, 2);
		MediaHelper.setSampleRate(media_info, 48000.0f);
		MediaHelper.setBitsPerSample(media_info, 16);
		MediaHelper.setBitRate(media_info, 196);
		MediaHelper.setBitRateVariable(media_info, true);
		MediaHelper.setBitRateMaximum(media_info, 320);
		MediaHelper.setBitRateMinimum(media_info, 128);
		MediaHelper.setBitRateAverage(media_info, 193);
		MediaHelper.setBandwidth(media_info, 20000.0f);
		
		doc_factory = DocumentBuilderFactory.newInstance();
		doc_factory.setNamespaceAware(true);
		doc_builder = doc_factory.newDocumentBuilder();
		doc = doc_builder.newDocument();
		doc.appendChild(media_info.toXML(doc, "MediaInformation"));

		trans_factory = TransformerFactory.newInstance();
		trans = trans_factory.newTransformer();
		trans.setOutputProperty(OutputKeys.METHOD, "xml");
		trans.setOutputProperty(OutputKeys.INDENT, "yes");
		trans.transform(
			new DOMSource(doc),
			new StreamResult(new File("testMI.mp7")));
	}
	catch( Exception exc )
	{
		Debug.printStackTrace(System.err, exc);
	}

	return;
}

}

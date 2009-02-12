/*
 * Created on 01.07.2004
 *
 * This file is part of the MPEG7AudioEnc project.
 */
package de.crysandt.audio.mpeg7audio.mci;

import java.net.*;

import org.w3c.dom.*;

import de.crysandt.xml.*;

/**
 * @author Michael.Lambertz@rwth-aachen.de
 *
 */
public class MediaLocator
{
/*	<!-- ############################################# -->
	<!--  Definition of MediaLocator datatype (6.5.2)  -->
	<!-- ############################################# -->
	<!-- Definition of MediaLocator datatype -->
	<complexType name="MediaLocatorType">
		<sequence>
			<choice minOccurs="0">
				<element name="MediaUri" type="anyURI"/>
				<element name="InlineMedia" type="mpeg7v1:InlineMediaType"/>
			</choice>
			<element name="StreamID" type="nonNegativeInteger" minOccurs="0"/>
		</sequence>
	</complexType> */

// I only implement a media URI.
// An implementation of inline media does not seem to be necessary here.

private URI					media_uri;		// cardinality: 1
private Integer				stream_id;		// cardinality: 0 - 1

public MediaLocator( URI media_uri )
{
	this.media_uri = media_uri;
	this.stream_id = null;
}

public Element toXML( Document doc, String name )
{
Element		loc_ele;
Element		uri_ele;
Element		str_id_ele;

	loc_ele = doc.createElementNS(Namespace.MPEG7, name);
	loc_ele.setAttributeNS(Namespace.XSI, "xsi:type", "MediaLocatorType");
	
	uri_ele = doc.createElementNS(Namespace.MPEG7, "MediaUri");
	Utils.setContent(doc, uri_ele, media_uri.toString());
	loc_ele.appendChild(uri_ele);

	if ( stream_id != null )
	{
		str_id_ele = doc.createElementNS(Namespace.MPEG7, "StreamID");
		Utils.setContent(doc, str_id_ele, stream_id.toString());
		loc_ele.appendChild(str_id_ele);
	}
	
	return(loc_ele);
}

public URI getMediaUri( )
{
	return(media_uri);
}

public int getStreamId( )
{
	return(stream_id.intValue());
}

public void setMediaUri( URI uri )
{
	this.media_uri = uri;
}

public void setStreamId( int id )
{
	this.stream_id = new Integer(id);
}

}

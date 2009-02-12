/*
 * Created on Jun 30, 2004
 *
 * This file is part of the MPEG7AudioEnc project.
 */
package de.crysandt.audio.mpeg7audio.mci;

import org.w3c.dom.*;

import de.crysandt.xml.*;
import de.crysandt.util.*;

/**
 * @author Michael.Lambertz@rwth-aachen.de
 *
 */
public class MediaProfile implements DescriptionSchemeI
{
/*	<!-- ####################################### -->
	<!--  Definition of MediaProfile DS (8.2.3)  -->
	<!-- ####################################### -->
	<!-- Definition of MediaProfile DS -->
	<complexType name="MediaProfileType">
		<complexContent>
			<extension base="mpeg7v1:DSType">
				<sequence>
					<element name="ComponentMediaProfile" type="mpeg7v1:MediaProfileType" minOccurs="0" maxOccurs="unbounded"/>
					<element name="MediaFormat" type="mpeg7v1:MediaFormatType" minOccurs="0"/>
					<element name="MediaTranscodingHints" type="mpeg7v1:MediaTranscodingHintsType" minOccurs="0"/>
					<element name="MediaQuality" type="mpeg7v1:MediaQualityType" minOccurs="0"/>
					<element name="MediaInstance" type="mpeg7v1:MediaInstanceType" minOccurs="0" maxOccurs="unbounded"/>
				</sequence>
				<attribute name="master" type="boolean" use="optional" default="false"/>
			</extension>
		</complexContent>
	</complexType> */
	
private Boolean					master;						// use: optional, default: false
private VectorTyped				component_media_profiles;	// cardinality: 0 - n
private MediaFormat				media_format;				// cardinality: 0 - 1
private MediaTranscodingHints	media_transcoding_hints;	// cardinality: 0 - 1
private MediaQuality			media_quality;				// cardinality: 0 - 1
private VectorTyped				media_instances;			// cardinality: 0 - n


public MediaProfile( )
{
	master = null;
	component_media_profiles = new VectorTyped( MediaProfile.class );
	media_format = null;
	media_transcoding_hints = null;
	media_quality = null;
	media_instances = new VectorTyped( MediaInstance.class );
}

public Element toXML( Document doc, String name )
{
Element		profile_ele;
int			i;

	profile_ele = doc.createElementNS(Namespace.MPEG7, name);
	profile_ele.setAttributeNS(Namespace.XSI, "xsi:type", "MediaProfileType");

	for ( i = 0; i < component_media_profiles.size(); ++i )
		profile_ele.appendChild(((MediaProfile)component_media_profiles.get(i)).toXML(doc, "ComponentMediaProfile"));
	
	if ( media_format != null )
		profile_ele.appendChild(media_format.toXML(doc, "MediaFormat"));

	if ( media_transcoding_hints != null )
		profile_ele.appendChild(media_transcoding_hints.toXML(doc, "MediaTranscodingHints"));

	if ( media_quality != null )
		profile_ele.appendChild(media_quality.toXML(doc, "MediaQuality"));

	for ( i = 0; i < media_instances.size(); ++i )
		profile_ele.appendChild(((MediaInstance)media_instances.get(i)).toXML(doc, "MediaInstance"));

	return(profile_ele);
}

public boolean isMaster( )
{
	if (master != null)
		return(master.booleanValue());
	else
		return(false);
}

public void setMaster( boolean master )
{
	this.master = new Boolean(master);
}

/**
 * @return "VectorTyped" with elements of type "MediaProfile"
 */
public VectorTyped getMediaProfiles( )
{
	return(component_media_profiles);
}

public MediaFormat getMediaFormat( )
{
	return(media_format);
}

public void setMediaFormat( MediaFormat media_format )
{
	this.media_format = media_format;
}

public MediaTranscodingHints getMediaTranscodingHints( )
{
	return(media_transcoding_hints);
}

public void setMediaTranscodingHints( MediaTranscodingHints hints )
{
	this.media_transcoding_hints = hints;
}

public MediaQuality getMediaQuality( )
{
	return(media_quality);
}

public void setMediaQuality( MediaQuality quality )
{
	this.media_quality = quality;
}

/**
 * @return "VectorTyped" with elements of type "MediaInstance"
 */
public VectorTyped getMediaInstances( )
{
	return(media_instances);
}

}

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
public class MediaIdentification implements DescriptorI
{
/*	<!-- ############################################# -->
	<!--  Definition of MediaIdentification D (8.2.2)  -->
	<!-- ############################################# -->
	<!-- Definition of MediaIdentification D -->
	<complexType name="MediaIdentificationType">
		<complexContent>
			<extension base="mpeg7v1:DType">
				<sequence>
					<element name="EntityIdentifier" type="mpeg7v1:UniqueIDType"/>
					<element name="AudioDomain" type="mpeg7v1:ControlledTermUseType" minOccurs="0" maxOccurs="unbounded"/>
					<element name="VideoDomain" type="mpeg7v1:ControlledTermUseType" minOccurs="0" maxOccurs="unbounded"/>
					<element name="ImageDomain" type="mpeg7v1:ControlledTermUseType" minOccurs="0" maxOccurs="unbounded"/>
				</sequence>
			</extension>
		</complexContent>
	</complexType> */

private UniqueID		entity_identifier;		// cardinality: 1
private VectorTyped		audio_domains;			// cardinality: 0 - n
private VectorTyped		video_domains;			// cardinality: 0 - n
private VectorTyped		image_domains;			// cardinality: 0 - n

public MediaIdentification( UniqueID entity_identifier )
{
	this.entity_identifier = entity_identifier;
	this.audio_domains = new VectorTyped( ControlledTermUse.class );
	this.video_domains = new VectorTyped( ControlledTermUse.class );
	this.image_domains = new VectorTyped( ControlledTermUse.class );
}

public Element toXML( Document doc, String name )
{
Element		iden_ele;
int			i;

	iden_ele = doc.createElementNS(Namespace.MPEG7, name);
	iden_ele.setAttributeNS(Namespace.XSI, "xsi:type", "MediaIdentificationType");

	iden_ele.appendChild(entity_identifier.toXML(doc, "EntityIdentifier"));

	for ( i = 0; i < audio_domains.size(); ++i )
		iden_ele.appendChild(((ControlledTermUse)audio_domains.get(i)).toXML(doc, "AudioDomain"));

	for ( i = 0; i < video_domains.size(); ++i )
		iden_ele.appendChild(((ControlledTermUse)video_domains.get(i)).toXML(doc, "VideoDomain"));

	for ( i = 0; i < image_domains.size(); ++i )
		iden_ele.appendChild(((ControlledTermUse)image_domains.get(i)).toXML(doc, "ImageDomain"));

	return(iden_ele);
}

public UniqueID getEntityIdentifier( )
{
	return(entity_identifier);
}

public void setEntityIdentifier( UniqueID entity_identifier )
{
	this.entity_identifier = entity_identifier;
}

/**
 * @return "VectorTyped" with elements of type "ControlledTermUse"
 */
public VectorTyped getAudioDomains( )
{
	return(audio_domains);
}

/**
 * @return "VectorTyped" with elements of type "ControlledTermUse"
 */
public VectorTyped getVideoDomains( )
{
	return(video_domains);
}

/**
 * @return "VectorTyped" with elements of type "ControlledTermUse"
 */
public VectorTyped getImageDomains( )
{
	return(image_domains);
}

}

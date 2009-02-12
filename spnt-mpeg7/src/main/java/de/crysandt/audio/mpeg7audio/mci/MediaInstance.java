/*
 * Created on Jun 30, 2004
 *
 * This file is part of the MPEG7AudioEnc project.
 */
package de.crysandt.audio.mpeg7audio.mci;

import org.w3c.dom.*;

import de.crysandt.xml.*;

/**
 * @author Michael.Lambertz@rwth-aachen.de
 *
 */
public class MediaInstance implements DescriptionSchemeI
{
/*	<!-- ######################################## -->
	<!--  Definition of MediaInstance DS (8.2.7)  -->
	<!-- ######################################## -->
	<!-- Definition of MediaInstance DS -->
	<complexType name="MediaInstanceType">
		<complexContent>
			<extension base="mpeg7v1:DSType">
				<sequence>
					<element name="InstanceIdentifier" type="mpeg7v1:UniqueIDType"/>
					<choice>
						<element name="MediaLocator" type="mpeg7v1:MediaLocatorType"/>
						<element name="LocationDescription" type="mpeg7v1:TextualType"/>
					</choice>
				</sequence>
			</extension>
		</complexContent>
	</complexType> */

private UniqueID		instance_identifier;	// cardinality: 1
private MediaLocator	media_locator;			// choice with "location_description"
private String			location_description;	// choice with "media_locator"

public MediaInstance( UniqueID instance_identifier, MediaLocator media_locator )
{
	this.instance_identifier = instance_identifier;
	this.media_locator = media_locator;
	this.location_description = null;
}

public MediaInstance( UniqueID instance_identifier, String location_description )
{
	this.instance_identifier = instance_identifier;
	this.media_locator = null;
	this.location_description = location_description;	
}

public Element toXML( Document doc, String name )
{
Element		inst_ele;
Element		desc_ele;

	inst_ele = doc.createElementNS(Namespace.MPEG7, name);
	inst_ele.setAttributeNS(Namespace.XSI, "xsi:type", "MediaInstanceType");

	inst_ele.appendChild(instance_identifier.toXML(doc, "InstanceIdentifier"));	

	if ( media_locator != null )
	{
		inst_ele.appendChild(media_locator.toXML(doc, "MediaLocator"));	
	}
	else
	{
		desc_ele = doc.createElement("LocationDescription");
		desc_ele.setAttributeNS(Namespace.XSI, "xsi:type", "TextualType");
		Utils.setContent(doc, desc_ele, location_description);
		inst_ele.appendChild(desc_ele);
	}

	return(inst_ele);
}

public UniqueID getInstanceIdentifier( )
{
	return(instance_identifier);
}

public String getLocationDescription( )
{
	return(location_description);
}

public MediaLocator getMediaLocator( )
{
	return(media_locator);
}

public void setInstanceIdentifier( UniqueID identifier )
{
	this.instance_identifier = identifier;
}

public void setLocationDescription( String desc )
{
	if ( desc != null )
	{
		this.location_description = desc;
		this.media_locator = null;
	}
}

public void setMediaLocator( MediaLocator locator )
{
	if ( locator != null )
	{
		this.media_locator = locator;
		this.location_description = null;
	}
}

}

/*
 * Created on Jul 20, 2004
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
public class Creation implements DescriptionSchemeI
{
/*	<!-- ################################### -->
	<!--  Definition of Creation DS (9.2.2)  -->
	<!-- ################################### -->
	<!-- Definition of Creation DS  -->
	<complexType name="CreationType">
		<complexContent>
			<extension base="mpeg7v1:DSType">
				<sequence>
					<element name="Title" type="mpeg7v1:TitleType" maxOccurs="unbounded"/>
					<element name="TitleMedia" type="mpeg7v1:TitleMediaType" minOccurs="0"/>
					<element name="Abstract" type="mpeg7v1:TextAnnotationType" minOccurs="0" maxOccurs="unbounded"/>
					<element name="Creator" type="mpeg7v1:CreatorType" minOccurs="0" maxOccurs="unbounded"/>
					<element name="CreationCoordinates" minOccurs="0" maxOccurs="unbounded">
						<complexType>
							<sequence>
								<element name="Location" type="mpeg7v1:PlaceType" minOccurs="0"/>
								<element name="Date" type="mpeg7v1:TimeType" minOccurs="0"/>
							</sequence>
						</complexType>
					</element>
					<element name="CreationTool" type="mpeg7v1:CreationToolType" minOccurs="0" maxOccurs="unbounded"/>
					<element name="CopyrightString" type="mpeg7v1:TextualType" minOccurs="0" maxOccurs="unbounded"/>
				</sequence>
			</extension>
		</complexContent>
	</complexType> */

private VectorTyped				titles;			// cardinality: 1 - n
private VectorTyped				creators;		// cardinality: 0 - n
private VectorTyped				coordinates;	// cardinality: 0 - n
// incomplete
private VectorTyped				copyrights;		// cardinality: 0 - n

public Creation( )
{
	this.titles = new VectorTyped(Title.class);
	this.creators = new VectorTyped(Creator.class);
	this.coordinates = new VectorTyped(CrCreationCoordinate.class);
	this.copyrights = new VectorTyped(String.class);
}

/**
 * @return "VectorTyped" with elements of type "Title"
 */
public VectorTyped getTitles( )
{
	return(this.titles);
}

/**
 * @return "VectorTyped" with elements of type "Creator"
 */
public VectorTyped getCreators( )
{
	return(this.creators);
}

/**
 * @return "VectorTyped" with elements of type "CrCreationCoordinate"
 */
public VectorTyped getCoordinates( )
{
	return(this.coordinates);
}

/**
 * @return "VectorTyped" with elements of type "String"
 */
public VectorTyped getCopyrights( )
{
	return(this.copyrights);
}

public Element toXML( Document doc, String name )
{
Element		cre_ele;
Element		copy_ele;
int			i;

	cre_ele = doc.createElementNS(Namespace.MPEG7, name);
	cre_ele.setAttributeNS(Namespace.XSI, "xsi:type", "CreationType");

	for ( i = 0; i < titles.size(); ++i )
		cre_ele.appendChild(((Title)titles.get(i)).toXML(doc, "Title"));

	for ( i = 0; i < creators.size(); ++i )
		cre_ele.appendChild(((Creator)creators.get(i)).toXML(doc, "Creator"));

	for ( i = 0; i < coordinates.size(); ++i )
		cre_ele.appendChild(((CrCreationCoordinate)coordinates.get(i)).toXML(doc, "CreationCoordinates"));
	
	for ( i = 0; i < copyrights.size(); ++i )
	{
		copy_ele = doc.createElementNS(Namespace.MPEG7, "CopyrightString");
		Utils.setContent(doc, copy_ele, (String)copyrights.get(i));
		cre_ele.appendChild(copy_ele);
	}

	return(cre_ele);
}

}

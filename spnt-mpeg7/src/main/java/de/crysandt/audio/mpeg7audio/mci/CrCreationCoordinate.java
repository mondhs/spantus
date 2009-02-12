/*
 * Created on Oct 27, 2004
 *
 * This file is part of the MPEG7AudioEnc project.
 */
package de.crysandt.audio.mpeg7audio.mci;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import de.crysandt.util.*;
import de.crysandt.xml.Namespace;

/**
 * @author Michael.Lambertz@rwth-aachen.de
 */
public class CrCreationCoordinate
{
/*	<element name="CreationCoordinates" minOccurs="0" maxOccurs="unbounded">
		<complexType>
			<sequence>
				<element name="Location" type="mpeg7v1:PlaceType" minOccurs="0"/>
				<element name="Date" type="mpeg7v1:TimeType" minOccurs="0"/>
			</sequence>
		</complexType>
	</element> */

private VectorTyped			dates;

public CrCreationCoordinate( )
{
	dates = new VectorTyped(Time.class);
}

public Element toXML( Document doc, String name )
{
Element				cre_coord_ele;
int					i;

	cre_coord_ele = doc.createElementNS(Namespace.MPEG7, name);

	for (i = 0; i < dates.size(); ++i)
		cre_coord_ele.appendChild(((Time)dates.get(i)).toXML(doc, "Date"));
	
	return(cre_coord_ele);
}

/**
 * @return "VectorTyped" with elements of type "Time"
 */
public VectorTyped getDates( )
{
	return(this.dates);
}

}

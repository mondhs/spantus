/*
 * Created on 25.07.2004
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
public class Creator extends MediaAgent
{
/*	<!-- Definition of Creator datatype -->
	<complexType name="CreatorType">
		<complexContent>
			<extension base="mpeg7v1:MediaAgentType">
				<sequence>
					<element name="Character" type="mpeg7v1:PersonNameType" minOccurs="0" maxOccurs="unbounded"/>
					<element name="Instrument" type="mpeg7v1:CreationToolType" minOccurs="0" maxOccurs="unbounded"/>
				</sequence>
			</extension>
		</complexContent>
	</complexType> */

public Creator( )
{
	super();
}

public Element toXML( Document doc, String name )
{
Element		creator_ele;

	creator_ele = super.toXML(doc, name);
	creator_ele.setAttributeNS(Namespace.XSI, "xsi:type", "CreatorType");
	
	return(creator_ele);
}

}

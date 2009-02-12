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
public class CreationTool
{
/*	<!-- Definition of CreationTool datatype -->
	<complexType name="CreationToolType">
		<sequence>
			<element name="Tool" type="mpeg7v1:TermUseType"/>
			<element name="Setting" minOccurs="0" maxOccurs="unbounded">
				<complexType>
					<attribute name="name" type="string" use="required"/>
					<attribute name="value" type="string" use="required"/>
				</complexType>
			</element>
		</sequence>
	</complexType> */

public Element toXML( Document doc, String name )
{
Element		tool_ele;

	tool_ele = doc.createElementNS(Namespace.MPEG7, name);
	tool_ele.setAttributeNS(Namespace.XSI, "xsi:type", "CreationToolType");

	return(tool_ele);
}

}

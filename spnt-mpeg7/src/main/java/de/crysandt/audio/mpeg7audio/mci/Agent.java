/*
  This file is part of the MPEG7AudioEnc project.
*/

package de.crysandt.audio.mpeg7audio.mci;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import de.crysandt.xml.Namespace;

/**
 * @author Michael.Lambertz@rwth-aachen.de
 */
public class Agent implements DescriptionSchemeI
{
/*	<!-- ################################ -->
	<!--  Definition of Agent DS (7.4.2)  -->
	<!-- ################################ -->
	<!-- Definition of Agent DS  -->
	<complexType name="AgentType" abstract="true">
		<complexContent>
			<extension base="mpeg7v1:DSType">
				<sequence>
					<element name="Icon" type="mpeg7v1:MediaLocatorType" minOccurs="0" maxOccurs="unbounded"/>
				</sequence>
			</extension>
		</complexContent>
	</complexType> */

public Element toXML( Document doc, String name )
{
Element		agent_ele;

	agent_ele = doc.createElementNS(Namespace.MPEG7, name);
	agent_ele.setAttributeNS(Namespace.XSI, "xsi:type", "AgentType");

	return(agent_ele);
}

}

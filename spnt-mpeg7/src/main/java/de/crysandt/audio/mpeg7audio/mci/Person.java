/*
 * Created on Oct 22, 2004
 *
 * This file is part of the MPEG7AudioEnc project.
 */
package de.crysandt.audio.mpeg7audio.mci;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import de.crysandt.xml.Namespace;
import de.crysandt.util.*;

/**
 * @author Michael.Lambertz@rwth-aachen.de
 */
public class Person extends Agent
{
/*	<!-- ################################# -->
	<!--  Definition of Person DS (7.4.3)  -->
	<!-- ################################# -->
	<!-- Definition of Person DS  -->
	<complexType name="PersonType">
		<complexContent>
			<extension base="mpeg7v1:AgentType">
				<sequence>
					<choice maxOccurs="unbounded">
						<element name="Name" type="mpeg7v1:PersonNameType"/>
						<element name="NameTerm" type="mpeg7v1:ControlledTermUseType"/>
					</choice>
					<element name="Affiliation" minOccurs="0" maxOccurs="unbounded">
						<complexType>
							<choice>
								<element name="Organization" type="mpeg7v1:OrganizationType"/>
								<element name="OrganizationRef" type="mpeg7v1:ReferenceType"/>
								<element name="PersonGroup" type="mpeg7v1:PersonGroupType"/>
								<element name="PersonGroupRef" type="mpeg7v1:ReferenceType"/>
							</choice>
						</complexType>
					</element>
					<element name="Citizenship" type="mpeg7v1:countryCode" minOccurs="0" maxOccurs="unbounded"/>
					<choice minOccurs="0">
						<element name="Address" type="mpeg7v1:PlaceType"/>
						<element name="AddressRef" type="mpeg7v1:ReferenceType"/>
					</choice>
					<element name="ElectronicAddress" type="mpeg7v1:ElectronicAddressType" minOccurs="0" maxOccurs="unbounded"/>
				</sequence>
			</extension>
		</complexContent>
	</complexType> */

private VectorTyped		names;		// cardinality: 1 - n

public Person( PersonName name )
{
	this.names = new VectorTyped(PersonName.class);
	this.names.add(name);
}

public Element toXML( Document doc, String name )
{
Element		person_ele;
int			i;

	person_ele = doc.createElementNS(Namespace.MPEG7, name);
	person_ele.setAttributeNS(Namespace.XSI, "xsi:type", "PersonType");

	for (i = 0; i < names.size(); ++i)
		person_ele.appendChild(((PersonName)names.get(i)).toXML(doc, "Name"));
		
	return(person_ele);
}

/**
 * @return "VectorTyped" with elements of type "PersonName"
 */
public VectorTyped getNames( )
{
	return(this.names);
}

}

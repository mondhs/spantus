/*
 * Created on Oct 25, 2004
 *
 * This file is part of the MPEG7AudioEnc project.
 */
package de.crysandt.audio.mpeg7audio.mci;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import de.crysandt.util.VectorTyped;
import de.crysandt.xml.Namespace;

/**
 * @author Michael.Lambertz@rwth-aachen.de
 */
public class PersonGroup extends Agent
{
/*	<!-- ###################################### -->
	<!--  Definition of PersonGroup DS (7.4.4)  -->
	<!-- ###################################### -->
	<!-- Definition of PersonGroup DS  -->
	<complexType name="PersonGroupType">
		<complexContent>
			<extension base="mpeg7v1:AgentType">
				<sequence>
					<element name="Name" minOccurs="0" maxOccurs="unbounded">
						<complexType>
							<simpleContent>
								<extension base="mpeg7v1:TextualType">
									<attribute name="type" use="optional">
										<simpleType>
											<restriction base="NMTOKEN">
												<enumeration value="former"/>
												<enumeration value="variant"/>
												<enumeration value="main"/>
											</restriction>
										</simpleType>
									</attribute>
								</extension>
							</simpleContent>
						</complexType>
					</element>
					<element name="NameTerm" minOccurs="0" maxOccurs="unbounded">
						<complexType>
							<complexContent>
								<extension base="mpeg7v1:ControlledTermUseType">
									<attribute name="type" use="optional">
										<simpleType>
											<restriction base="NMTOKEN">
												<enumeration value="former"/>
												<enumeration value="variant"/>
												<enumeration value="main"/>
											</restriction>
										</simpleType>
									</attribute>
								</extension>
							</complexContent>
						</complexType>
					</element>
					<element name="Kind" type="mpeg7v1:TermUseType" minOccurs="0"/>
					<choice minOccurs="0" maxOccurs="unbounded">
						<element name="Member" type="mpeg7v1:PersonType"/>
						<element name="MemberRef" type="mpeg7v1:ReferenceType"/>
					</choice>
					<choice minOccurs="0">
						<element name="Jurisdiction" type="mpeg7v1:PlaceType"/>
						<element name="JurisdictionRef" type="mpeg7v1:ReferenceType"/>
					</choice>
					<choice minOccurs="0">
						<element name="Address" type="mpeg7v1:PlaceType"/>
						<element name="AddressRef" type="mpeg7v1:ReferenceType"/>
					</choice>
					<element name="ElectronicAddress" type="mpeg7v1:ElectronicAddressType" minOccurs="0"/>
				</sequence>
			</extension>
		</complexContent>
	</complexType> */

private VectorTyped		names;		// cardinality: 1 - n
private VectorTyped		members;	// cardinality: 0 - n

public PersonGroup( String name )
{
	this.names = new VectorTyped(String.class);
	this.names.add(name);
	this.members = new VectorTyped(Person.class);
}

public Element toXML( Document doc, String name )
{
Element		person_group_ele;
Element		name_ele;
int			i;

	person_group_ele = doc.createElementNS(Namespace.MPEG7, name);
	person_group_ele.setAttributeNS(Namespace.XSI, "xsi:type", "PersonGroupType");

	for (i = 0; i < names.size(); ++i)
	{
		name_ele = doc.createElement("Name");
		Utils.setContent(doc, name_ele, (String)names.get(i));
		person_group_ele.appendChild(name_ele);
	}
		
	for (i = 0; i < members.size(); ++i)
		person_group_ele.appendChild(((Person)members.get(i)).toXML(doc, "Member"));

	return(person_group_ele);
}

/**
 * @return "VectorTyped" with elements of type "String"
 */
public VectorTyped getNames( )
{
	return(this.names);
}

/**
 * @return "VectorTyped" with elements of type "Person"
 */
public VectorTyped getMembers( )
{
	return(this.members);
}

}

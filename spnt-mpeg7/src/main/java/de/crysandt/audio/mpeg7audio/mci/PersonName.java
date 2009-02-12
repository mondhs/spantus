/*
 * Created on Oct 22, 2004
 *
 * This file is part of the MPEG7AudioEnc project.
 */
package de.crysandt.audio.mpeg7audio.mci;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import de.crysandt.xml.Namespace;

/**
 * @author Michael.Lambertz@rwth-aachen.de
 */
public class PersonName
{
/*	<!-- ########################################### -->
	<!--  Definition of PersonName datatype (7.4.6)  -->
	<!-- ########################################### -->
	<!-- Definition of PersonaName datatype  -->
	<complexType name="PersonNameType">
		<sequence>
			<choice maxOccurs="unbounded">
				<element name="GivenName" type="mpeg7v1:NameComponentType"/>
				<element name="FamilyName" type="mpeg7v1:NameComponentType" minOccurs="0"/>
				<element name="Title" type="mpeg7v1:NameComponentType" minOccurs="0"/>
				<element name="Numeration" type="string" minOccurs="0"/>
			</choice>
		</sequence>
		<attribute name="dateFrom" type="mpeg7v1:timePointType" use="optional"/>
		<attribute name="dateTo" type="mpeg7v1:timePointType" use="optional"/>
		<attribute name="type" use="optional">
			<simpleType>
				<restriction base="NMTOKEN">
					<enumeration value="former"/>
					<enumeration value="variant"/>
					<enumeration value="main"/>
				</restriction>
			</simpleType>
		</attribute>
		<attribute ref="xml:lang" use="optional"/>
	</complexType> */

private String		family_name;
private String		given_name;
	
public PersonName( String given_name )
{
	this.given_name = given_name;
	this.family_name = null;
}

public PersonName( String given_name, String family_name )
{
	this.given_name = given_name;
	this.family_name = family_name;
}

public Element toXML( Document doc, String name )
{
Element		person_name_ele;
Element		name_ele;

	person_name_ele = doc.createElementNS(Namespace.MPEG7, name);
	person_name_ele.setAttributeNS(Namespace.XSI, "xsi:type", "PersonNameType");

	name_ele = doc.createElementNS(Namespace.MPEG7, "GivenName");
	Utils.setContent(doc, name_ele, given_name);
	person_name_ele.appendChild(name_ele);
	
	if ( family_name != null )
	{
		name_ele = doc.createElementNS(Namespace.MPEG7, "FamilyName");
		Utils.setContent(doc, name_ele, family_name);
		person_name_ele.appendChild(name_ele);
	}
	
	return(person_name_ele);
}

public void setGivenName( String given_name )
{
	this.given_name = given_name;
}

public String getFamilyName( )
{
	return(this.family_name);
}

public void setFamilyName( String family_name )
{
	this.family_name = family_name;
}

public String getGivenName( )
{
	return(this.given_name);
}

}

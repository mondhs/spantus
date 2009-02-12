/*
 * Created on Jun 30, 2004
 *
 * This file is part of the MPEG7AudioEnc project.
 */
package de.crysandt.audio.mpeg7audio.mci;

import de.crysandt.util.*;

import org.w3c.dom.*;

import de.crysandt.xml.*;

/**
 * @author Michael.Lambertz@rwth-aachen.de
 *
 */
public class InlineTermDefinition
{
/*	<!-- ######################################## -->
	<!--  Definition of TermUse datatype (7.3.4)  -->
	<!-- ######################################## -->
	<!-- Definition of InlineTermDefinition datatype  -->
	<complexType name="InlineTermDefinitionType" abstract="true">
		<sequence>
			<element name="Name" minOccurs="0" maxOccurs="unbounded">
				<complexType>
					<simpleContent>
						<extension base="mpeg7v1:TextualType">
							<attribute name="preferred" type="boolean" use="optional"/>
						</extension>
					</simpleContent>
				</complexType>
			</element>
			<element name="Definition" type="mpeg7v1:TextualType" minOccurs="0" maxOccurs="unbounded"/>
			<element name="Term" minOccurs="0" maxOccurs="unbounded">
				<complexType>
					<complexContent>
						<extension base="mpeg7v1:InlineTermDefinitionType">
							<attribute name="relation" type="mpeg7v1:termRelationQualifierType" use="optional" default="NT"/>
							<attribute name="termID" type="NMTOKEN"/>
						</extension>
					</complexContent>
				</complexType>
			</element>
		</sequence>
	</complexType> */

private VectorTyped				names; 				// cardinality: 0 - n
private VectorTyped				definitions;		// cardinality: 0 - n
private VectorTyped				terms;				// cardinality: 0 - n

public InlineTermDefinition( )
{
	names = new VectorTyped( String.class );
	definitions = new VectorTyped( String.class );
	terms = new VectorTyped( ITDTerm.class );
}

public Element toXML( Document doc, String name )
{
Element		term_def_ele;
Element		name_ele;
Element		def_ele;
Element		term_ele;
int			i;

	term_def_ele = doc.createElementNS(Namespace.MPEG7, name);
	term_def_ele.setAttributeNS(Namespace.XSI, "xsi:type", "InlineTermDefinitionType");

	for ( i = 0; i < names.size(); ++i )
	{
		name_ele = doc.createElement("Name");
		Utils.setContent(doc, name_ele, (String)names.get(i));
		term_def_ele.appendChild(name_ele);
	}

	for ( i = 0; i < definitions.size(); ++i )
	{
		def_ele = doc.createElement("Definition");
		def_ele.setAttributeNS(Namespace.XSI, "xsi:type", "TextualType");
		Utils.setContent(doc, def_ele, (String)definitions.get(i));
		term_def_ele.appendChild(def_ele);
	}

	for ( i = 0; i < terms.size(); ++i )
	{
		term_ele = ((ITDTerm)terms.get(i)).toXML(doc, "Term");
		term_def_ele.appendChild(term_ele);
	}

	return(term_def_ele);
}

public VectorTyped getDefinitions( )
{
	return(definitions);
}

public VectorTyped getNames( )
{
	return(names);
}

public VectorTyped getTerms( )
{
	return(terms);
}

}

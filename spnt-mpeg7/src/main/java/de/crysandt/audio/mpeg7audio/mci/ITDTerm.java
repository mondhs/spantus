/*
 * Created on 01.07.2004
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
public class ITDTerm extends InlineTermDefinition
{
/*			<element name="Term" minOccurs="0" maxOccurs="unbounded">
				<complexType>
					<complexContent>
						<extension base="mpeg7v1:InlineTermDefinitionType">
							<attribute name="relation" type="mpeg7v1:termRelationQualifierType" use="optional" default="NT"/>
							<attribute name="termID" type="NMTOKEN"/>
						</extension>
					</complexContent>
				</complexType>
			</element> */

private String		relation;		// use: optional, default: "NT"
private String		term_id;

public ITDTerm( String term_id )
{
	this.relation = null;
	this.term_id = term_id;
}

public Element toXML( Document doc, String name )
{
Element		term_ele;

	term_ele = super.toXML(doc, name);
	term_ele.removeAttributeNS(Namespace.XSI, "type");

	if ( relation != null )
		term_ele.setAttribute("relation", relation);
	
	term_ele.setAttribute("termID", term_id);
	
	return(term_ele);
}

public String getRelation( )
{
	return(relation);
}

public String getTermId( )
{
	return(term_id);
}

public void setRelation( String relation )
{
	this.relation = relation;
}

public void setTermId( String id )
{
	this.term_id = id;
}

}



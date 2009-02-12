/*
 * Created on 08.07.2004
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
public class UniqueID
{
/*	<!-- ######################################### -->
	<!--  Definition of UniqueID datatype (6.3.1)  -->
	<!-- ######################################### -->
	<!-- Definition of UniqueID datatype -->
	<complexType name="UniqueIDType">
		<simpleContent>
			<extension base="string">
				<attribute name="type" type="NMTOKEN" use="optional" default="URI"/>
				<attribute name="organization" type="NMTOKEN" use="optional"/>
				<attribute name="authority" type="NMTOKEN" use="optional"/>
				<attribute name="encoding" use="optional" default="text">
					<simpleType>
						<restriction base="string">
							<enumeration value="text"/>
							<enumeration value="base16"/>
							<enumeration value="base64"/>
						</restriction>
					</simpleType>
				</attribute>
			</extension>
		</simpleContent>
	</complexType> */
	
private String			unique_id;		// content
private String			type;			// use: optional, default="URI"
private String			organization;	// use: optional
private String			authority;		// use: optional
private String			encoding;		// use: optional, default="text"

private static long		counter = 0;

public UniqueID( )
{
String		num;

	num = Long.toString(counter);
	counter++;
	while ( num.length() < 10 )
		num = "0" + num;

	this.unique_id = "UniqueID" + num;
	this.type = null;
	this.organization = null;
	this.authority = null;
	this.encoding = null;
}

public UniqueID( String unique_id )
{
	this.unique_id = unique_id;
	this.type = null;
	this.organization = null;
	this.authority = null;
	this.encoding = null;
}

public Element toXML( Document doc, String name )
{
Element		id_ele;

	id_ele = doc.createElementNS(Namespace.MPEG7, name);
	id_ele.setAttributeNS(Namespace.XSI, "xsi:type", "UniqueIDType");

	Utils.setContent(doc, id_ele, unique_id);
	
	if ( type != null )
		id_ele.setAttribute("type", type);

	if ( organization != null )
		id_ele.setAttribute("organization", organization);
		
	if ( authority != null )
		id_ele.setAttribute("authority", authority);
	
	if ( encoding != null )
		id_ele.setAttribute("encoding", encoding);

	return(id_ele);
}

public String getAuthority( )
{
	return(this.authority);
}

public String getEncoding( )
{
	return(this.encoding);
}

public String getOrganization( )
{
	return(this.organization);
}

public String getType( )
{
	return(this.type);
}

public String getUniqueId( )
{
	return(this.unique_id);
}

public void setAuthority( String authority )
{
	this.authority = authority;
}

public void setEncoding( String encoding )
{
	this.encoding = encoding;
}

public void setOrganization( String organization )
{
	this.organization = organization;
}

public void setType( String type )
{
	this.type = type;
}

public void setUniqueId( String unique_id )
{
	this.unique_id = unique_id;
}

}

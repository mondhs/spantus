/*
 * Created on Jul 20, 2004
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
public class Title
{
/*	<!-- Definition of Title datatype  -->
	<complexType name="TitleType">
		<simpleContent>
			<extension base="mpeg7v1:TextualBaseType">
				<attribute name="type" use="optional" default="main">
					<simpleType>
						<union>
							<simpleType>
								<restriction base="NMTOKEN">
									<enumeration value="main"/>
									<enumeration value="secondary"/>
									<enumeration value="alternative"/>
									<enumeration value="original"/>
									<enumeration value="popular"/>
									<enumeration value="opusNumber"/>
									<enumeration value="songTitle"/>
									<enumeration value="albumTitle"/>
									<enumeration value="seriesTitle"/>
									<enumeration value="episodeTitle"/>
								</restriction>
							</simpleType>
							<simpleType>
								<restriction base="mpeg7v1:termReferenceType"/>
							</simpleType>
						</union>
					</simpleType>
				</attribute>
			</extension>
		</simpleContent>
	</complexType> */

private String				title;		// content
private String				type;		// use: optional, default: main

public Title( String title )
{
	this.title = title;
	this.type = null;
}

public Title( String title, String type )
{
	this.title = title;
	this.type = type;
}

public Element toXML( Document doc, String name )
{
Element		title_ele;

	title_ele = doc.createElementNS(Namespace.MPEG7, name);
	title_ele.setAttributeNS(Namespace.XSI, "xsi:type", "TitleType");
	
	Utils.setContent(doc, title_ele, title);
	
	if ( type != null )
	{
		if (	type.equals("main") ||
				type.equals("secondary") ||
				type.equals("alternative") ||
				type.equals("original") ||
				type.equals("popular") ||
				type.equals("opusNumber") ||
				type.equals("songTitle") ||
				type.equals("albumTitle") ||
				type.equals("seriesTitle") ||
				type.equals("episodeTitle")	)
		{
			title_ele.setAttribute("type", type);
		}
	}

	return(title_ele);
}

public String getTitle( )
{
	return(title);
}

public String getType( )
{
	return(type);
}

public void setTitle( String title )
{
	this.title = title;
}

public void setType( String type )
{
	this.type = type;
}

}

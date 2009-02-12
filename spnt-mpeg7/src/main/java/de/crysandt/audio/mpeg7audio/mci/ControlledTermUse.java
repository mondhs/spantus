/*
 * Created on Jun 30, 2004
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
public class ControlledTermUse extends InlineTermDefinition
{
/*	<!-- ################################################## -->
	<!--  Definition of ControlledTermUse datatype (7.3.5)  -->
	<!-- ################################################## -->
	<!-- Definition of ControlledTermUse datatype  -->
	<complexType name="ControlledTermUseType">
		<complexContent>
			<extension base="mpeg7v1:InlineTermDefinitionType">
				<attribute name="href" type="mpeg7v1:termReferenceType" use="required"/>
			</extension>
		</complexContent>
	</complexType> */

private String			href;			// use: required

public ControlledTermUse( String href )
{
	super( );
	this.href = href;
}

public Element toXML( Document doc, String name )
{
Element		term_use_ele;

	term_use_ele = super.toXML(doc, name); 
	term_use_ele.setAttributeNS(Namespace.XSI, "xsi:type", "ControlledTermUseType");

	term_use_ele.setAttribute("href", href);

	return(term_use_ele);
}

public String getHref( )
{
	return(href);
}

public void setHref( String href )
{
	this.href = href;
}

}

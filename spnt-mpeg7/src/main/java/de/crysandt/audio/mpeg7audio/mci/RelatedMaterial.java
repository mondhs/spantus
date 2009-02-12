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
public class RelatedMaterial
{

public Element toXML( Document doc, String name )
{
Element		mat_ele;

	mat_ele = doc.createElementNS(Namespace.MPEG7, name);
	mat_ele.setAttributeNS(Namespace.XSI, "xsi:type", "RelatedMaterialType");

	return(mat_ele);
}

}

/*
 * Created on 02.07.2004
 *
 * This file is part of the MPEG7AudioEnc project.
 */
package de.crysandt.audio.mpeg7audio.mci;

import org.w3c.dom.*;

/**
 * @author Michael.Lambertz@rwth-aachen.de
 *
 */
public class Utils
{

public static Element setContent( Document doc, Element ele, String content )
{
Text	text;

	text = doc.createTextNode(content);
	ele.appendChild(text);

	return(ele);
}

}

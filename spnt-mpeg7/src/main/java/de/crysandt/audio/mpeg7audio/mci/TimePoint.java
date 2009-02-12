/*
 * Created on Oct 27, 2004
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
public class TimePoint
{
/*	<!-- ########################################## -->
	<!--  Definition of timePoint datatype (6.4.3)  -->
	<!-- ########################################## -->
	<!-- Definition of timePoint datatype -->
	<simpleType name="timePointType">
		<restriction base="mpeg7v1:basicTimePointType">
			<pattern value="(\-?\d+(\-\d{2}(\-\d{2})?)?)?(T\d{2}(:\d{2}(:\d{2}(:\d+)?)?)?)?(F\d+)?((\-|\+)\d{2}:\d{2})?"/>
		</restriction>
	</simpleType> */
	
private String		time_point;

public TimePoint( String time_point )
{
	this.time_point = time_point;
}

public Element toXML( Document doc, String name )
{
Element				time_point_ele;

	time_point_ele = doc.createElementNS(Namespace.MPEG7, name);
	time_point_ele.setAttributeNS(Namespace.XSI, "xsi:type", "timePointType");
	
	Utils.setContent(doc, time_point_ele, time_point);

	return(time_point_ele);
}

public String getTimePoint( )
{
	return(this.time_point);
}

public void setTimePoint( String time_point )
{
	this.time_point = time_point;
}

}

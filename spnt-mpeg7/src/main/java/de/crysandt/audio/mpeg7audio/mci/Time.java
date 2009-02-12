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
public class Time
{
/*	<!-- ##################################### -->
	<!--  Definition of Time datatype (6.4.2)  -->
	<!-- ##################################### -->
	<!-- Definition of Time datatype -->
	<complexType name="TimeType">
		<sequence>
			<choice>
				<element name="TimePoint" type="mpeg7v1:timePointType"/>
				<element name="RelTimePoint" type="mpeg7v1:RelTimePointType"/>
				<element name="RelIncrTimePoint" type="mpeg7v1:RelIncrTimePointType"/>
			</choice>
			<choice minOccurs="0">
				<element name="Duration" type="mpeg7v1:durationType"/>
				<element name="IncrDuration" type="mpeg7v1:IncrDurationType"/>
			</choice>
		</sequence>
	</complexType> */

private TimePoint			time_point;		// cardinality: 1

public Time( TimePoint time_point )
{
	this.time_point = time_point;
}

public Element toXML( Document doc, String name )
{
Element				time_ele;

	time_ele = doc.createElementNS(Namespace.MPEG7, name);
	time_ele.setAttributeNS(Namespace.XSI, "xsi:type", "TimeType");
	
	time_ele.appendChild(time_point.toXML(doc, "TimePoint"));

	return(time_ele);
}

public TimePoint getTimePoint( )
{
	return(this.time_point);
}

public void setTimePoint( TimePoint time_point )
{
	this.time_point = time_point;
}

}

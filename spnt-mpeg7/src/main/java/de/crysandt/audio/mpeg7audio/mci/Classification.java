/*
 * Created on Jul 20, 2004
 *
 * This file is part of the MPEG7AudioEnc project.
 */
package de.crysandt.audio.mpeg7audio.mci;

import org.w3c.dom.*;

import de.crysandt.xml.*;
import de.crysandt.util.*;

/**
 * @author Michael.Lambertz@rwth-aachen.de
 *
 */
public class Classification implements DescriptionSchemeI
{
/*	<!-- ######################################### -->
	<!--  Definition of Classification DS (9.2.3)  -->
	<!-- ######################################### -->
	<!-- Definition of Classification DS  -->
	<complexType name="ClassificationType">
		<complexContent>
			<extension base="mpeg7v1:DSType">
				<sequence>
					<element name="Form" type="mpeg7v1:ControlledTermUseType" minOccurs="0"/>
					<element name="Genre" minOccurs="0" maxOccurs="unbounded">
						<complexType>
							<complexContent>
								<extension base="mpeg7v1:ControlledTermUseType">
									<attribute name="type" use="optional" default="main">
										<simpleType>
											<restriction base="NMTOKEN">
												<enumeration value="main"/>
												<enumeration value="secondary"/>
											</restriction>
										</simpleType>
									</attribute>
								</extension>
							</complexContent>
						</complexType>
					</element>
					<element name="Subject" type="mpeg7v1:TextAnnotationType" minOccurs="0"/>
					<element name="Purpose" type="mpeg7v1:ControlledTermUseType" minOccurs="0" maxOccurs="unbounded"/>
					<element name="Language" type="mpeg7v1:ExtendedLanguageType" minOccurs="0" maxOccurs="unbounded"/>
					<element name="CaptionLanguage" minOccurs="0" maxOccurs="unbounded">
						<complexType>
							<simpleContent>
								<extension base="language">
									<attribute name="closed" type="boolean" use="optional" default="true"/>
									<attribute name="supplemental" type="boolean" use="optional" default="false"/>
								</extension>
							</simpleContent>
						</complexType>
					</element>
					<element name="SignLanguage" minOccurs="0" maxOccurs="unbounded">
						<complexType>
							<simpleContent>
								<extension base="language">
									<attribute name="primary" type="boolean" use="optional"/>
									<attribute name="translation" type="boolean" use="optional"/>
								</extension>
							</simpleContent>
						</complexType>
					</element>
					<element name="Release" minOccurs="0">
						<complexType>
							<sequence>
								<element name="Region" type="mpeg7v1:regionCode" minOccurs="0" maxOccurs="unbounded"/>
							</sequence>
							<attribute name="date" type="mpeg7v1:timePointType" use="optional"/>
						</complexType>
					</element>
					<element name="Target" minOccurs="0">
						<complexType>
							<sequence>
								<element name="Market" type="mpeg7v1:ControlledTermUseType" minOccurs="0" maxOccurs="unbounded"/>
								<element name="Age" minOccurs="0">
									<complexType>
										<attribute name="min" type="nonNegativeInteger" use="optional"/>
										<attribute name="max" type="nonNegativeInteger" use="optional"/>
									</complexType>
								</element>
								<element name="Region" type="mpeg7v1:regionCode" minOccurs="0" maxOccurs="unbounded"/>
							</sequence>
						</complexType>
					</element>
					<element name="ParentalGuidance" type="mpeg7v1:ParentalGuidanceType" minOccurs="0" maxOccurs="unbounded"/>
					<element name="MediaReview" type="mpeg7v1:MediaReviewType" minOccurs="0" maxOccurs="unbounded"/>
				</sequence>
			</extension>
		</complexContent>
	</complexType> */

private VectorTyped			genres;

public Classification( )
{
	genres = new VectorTyped(ClGenre.class);
}

public Element toXML( Document doc, String name )
{
Element		class_ele;
int			i;

	class_ele = doc.createElementNS(Namespace.MPEG7, name);
	class_ele.setAttributeNS(Namespace.XSI, "xsi:type", "ClassificationType");
	
	for (i = 0; i < genres.size(); ++i)
		class_ele.appendChild(((ClGenre)genres.get(i)).toXML(doc,"Genre"));

	return(class_ele);
}

/**
 * @return "VectorTyped" with elements of type "ClGenre"
 */
public VectorTyped getGenres( )
{
	return(this.genres);
}

}
